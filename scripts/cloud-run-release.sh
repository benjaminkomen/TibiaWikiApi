#!/usr/bin/env bash
# Deploy a pre-built image to Cloud Run with the production env/probe flags,
# wait until the new revision is Ready, then run regression smoke:docs.
#
# This is the single source of truth for Cloud Run deploy flags (F-02).
# Callers:
#   cloudbuild.yaml     IMAGE=...:$COMMIT_SHA, BASE_URL unset → smoke the
#                       revision URL (Swagger + actuator; no wiki/Fandom).
#   scripts/deploy.sh   IMAGE=...:$COMMIT_SHA, BASE_URL=https://tibiawiki.dev (ops).
#                       :latest is retagged only after this script succeeds.
#
# gcloud run deploy already sends 100% traffic to the new revision once it
# becomes Ready. Do not add `gcloud alpha run services update-traffic
# --to-latest` — that can send traffic to latestCreated before Ready.
#
# Required: gcloud, bun, a reachable IMAGE.
#
# Env:
#   IMAGE                  Image to deploy (required).
#   PROJECT                GCP project (default tibiawikiapi-246008).
#   REGION                 Cloud Run region (default europe-west1).
#   SERVICE                Cloud Run service (default tibiawikiapi).
#   BASE_URL               smoke:docs target. Unset = the new revision URL.
#   READY_TIMEOUT_SECONDS  Wait for Ready (default 420; probe is 36×10s).
#   READY_POLL_SECONDS     Poll interval (default 10).
#
# Deploy/Ready failure skips smoke and exits non-zero (previous revision may
# still be serving). Smoke failure exits non-zero; the new revision may already
# be serving 100% of traffic.

set -euo pipefail

PROJECT="${PROJECT:-tibiawikiapi-246008}"
REGION="${REGION:-europe-west1}"
SERVICE="${SERVICE:-tibiawikiapi}"
READY_TIMEOUT_SECONDS="${READY_TIMEOUT_SECONDS:-420}"
READY_POLL_SECONDS="${READY_POLL_SECONDS:-10}"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

need_cmd() {
  local cmd="$1"
  local hint="$2"
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "ERROR: ${cmd} is required. ${hint}" >&2
    exit 1
  fi
}

need_cmd gcloud "Authenticate with a principal that can deploy ${SERVICE}."
need_cmd bun "Install bun from https://bun.sh. Post-deploy smoke runs: cd regression && bun run smoke:docs"

if [[ -z "${IMAGE:-}" ]]; then
  echo "ERROR: IMAGE is required (e.g. gcr.io/${PROJECT}/tibiawikiapi:\$COMMIT_SHA)." >&2
  exit 1
fi

# Must stay aligned with cloudbuild.yaml / docker/README.md probe table.
UPDATE_ENV_VARS="LOGGING_JSON=true,WIKI_WRITE_ENABLED=false"
STARTUP_PROBE="httpGet.path=/actuator/health/readiness,timeoutSeconds=4,periodSeconds=10,failureThreshold=36"
LIVENESS_PROBE="httpGet.path=/actuator/health/liveness,timeoutSeconds=4,periodSeconds=30,failureThreshold=3"

trim() {
  printf '%s' "$1" | tr -d '[:space:]'
}

revision_ready_fields() {
  local revision="$1"
  gcloud run revisions describe "$revision" \
    --project "$PROJECT" \
    --region "$REGION" \
    --format='value[separator=|](status.conditions[?type=Ready].status,status.conditions[?type=Ready].message)'
}

latest_ready_revision() {
  gcloud run services describe "$SERVICE" \
    --project "$PROJECT" \
    --region "$REGION" \
    --platform managed \
    --format='value(status.latestReadyRevisionName)'
}

revision_url() {
  local revision="$1"
  gcloud run revisions describe "$revision" \
    --project "$PROJECT" \
    --region "$REGION" \
    --format='value(status.url)'
}

wait_for_revision_ready() {
  local revision="$1"
  local deadline=$(($(date +%s) + READY_TIMEOUT_SECONDS))
  echo "Waiting up to ${READY_TIMEOUT_SECONDS}s for revision ${revision} to become Ready..."

  while true; do
    local fields status message latest_ready
    fields="$(revision_ready_fields "$revision")"
    status="$(trim "${fields%%|*}")"
    if [[ "$fields" == *"|"* ]]; then
      message="${fields#*|}"
    else
      message=""
    fi
    latest_ready="$(trim "$(latest_ready_revision)")"

    if [[ "$status" == "True" && "$latest_ready" == "$revision" ]]; then
      echo "Revision ${revision} is Ready and is status.latestReadyRevisionName."
      return 0
    fi

    if [[ "$status" == "False" ]]; then
      echo "ERROR: revision ${revision} Ready=False${message:+: $message}" >&2
      echo "Cloud Run did not promote this revision; the previous revision may still be serving. Skipping smoke." >&2
      gcloud run revisions describe "$revision" --project "$PROJECT" --region "$REGION" >&2 || true
      return 1
    fi

    if (( $(date +%s) >= deadline )); then
      echo "ERROR: timed out after ${READY_TIMEOUT_SECONDS}s waiting for ${revision} to become Ready (Ready=${status:-unknown}, latestReady=${latest_ready:-none})${message:+: $message}" >&2
      echo "Image build success is not deploy success. Skipping smoke." >&2
      gcloud run revisions describe "$revision" --project "$PROJECT" --region "$REGION" >&2 || true
      return 1
    fi

    echo "  ${revision} Ready=${status:-Unknown} latestReady=${latest_ready:-none}; retrying in ${READY_POLL_SECONDS}s"
    sleep "$READY_POLL_SECONDS"
  done
}

run_smoke() {
  local target="$1"
  echo "Running post-deploy docs/health smoke against ${target} (Swagger + actuator only; no wiki/Fandom paths) ..."
  if ! (cd "$ROOT/regression" && BASE_URL="$target" bun run smoke:docs); then
    echo "ERROR: post-deploy smoke failed against ${target}. Revision ${CREATED} is Ready and may already be serving traffic." >&2
    exit 1
  fi
}

echo "Deploying ${SERVICE} in ${REGION} from ${IMAGE} ..."
if ! gcloud run deploy "$SERVICE" \
  --image "$IMAGE" \
  --platform managed \
  --region "$REGION" \
  --memory 1Gi \
  --project "$PROJECT" \
  --update-env-vars "$UPDATE_ENV_VARS" \
  --startup-probe="$STARTUP_PROBE" \
  --liveness-probe="$LIVENESS_PROBE" \
  --quiet; then
  echo "ERROR: gcloud run deploy failed. The new revision is not serving; the previous revision may still be up. Skipping smoke." >&2
  exit 1
fi

CREATED="$(trim "$(gcloud run services describe "$SERVICE" \
  --project "$PROJECT" \
  --region "$REGION" \
  --platform managed \
  --format='value(status.latestCreatedRevisionName)')")"

if [[ -z "$CREATED" ]]; then
  echo "ERROR: could not read status.latestCreatedRevisionName after deploy. Skipping smoke." >&2
  exit 1
fi

if ! wait_for_revision_ready "$CREATED"; then
  exit 1
fi

CREATED_URL="$(trim "$(revision_url "$CREATED")")"
if [[ -z "$CREATED_URL" ]]; then
  echo "ERROR: could not read status.url for revision ${CREATED}. Skipping smoke." >&2
  exit 1
fi

SMOKE_URL="${BASE_URL:-$CREATED_URL}"
if [[ -n "${BASE_URL:-}" ]]; then
  echo "Smoke target BASE_URL=${BASE_URL} (ops). Revision URL is ${CREATED_URL}."
else
  echo "Smoke target is revision URL ${CREATED_URL} (not tibiawiki.dev)."
fi

run_smoke "$SMOKE_URL"
echo "Deploy succeeded: ${CREATED} is Ready and smoke:docs passed against ${SMOKE_URL}."
