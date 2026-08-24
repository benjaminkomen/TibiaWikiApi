#!/usr/bin/env bash
# Build, deploy TibiaWikiApi to Cloud Run, then fail closed unless the new
# revision is Ready and prod docs/health smoke passes.
#
# Prerequisites:
#   gcloud  authenticated for project tibiawikiapi-246008
#   bun     required for post-deploy smoke (https://bun.sh). Missing bun is
#           a hard error — image build / Ready is not enough.
#
# Env (all optional):
#   READY_TIMEOUT_SECONDS  How long to wait for the new revision Ready (default 420).
#                          Cloud Run startup probe is 36 × 10s ≈ 360s; this adds buffer.
#   READY_POLL_SECONDS     Poll interval while waiting (default 10).
#   BASE_URL               Post-deploy smoke target (default https://tibiawiki.dev).
#                          Passed through to regression `bun run smoke:docs`.
#                          Unset leftover localhost BASE_URL before a real deploy.
#
# Success = new revision Ready AND smoke:docs pass. Deploy/Ready failure skips
# smoke and exits non-zero (previous revision may still be serving).

set -euo pipefail

PROJECT="tibiawikiapi-246008"
REGION="europe-west1"
SERVICE="tibiawikiapi"
IMAGE="gcr.io/${PROJECT}/tibiawikiapi"
READY_TIMEOUT_SECONDS="${READY_TIMEOUT_SECONDS:-420}"
READY_POLL_SECONDS="${READY_POLL_SECONDS:-10}"
BASE_URL="${BASE_URL:-https://tibiawiki.dev}"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

need_cmd() {
  local cmd="$1"
  local hint="$2"
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "ERROR: ${cmd} is required. ${hint}" >&2
    exit 1
  fi
}

need_cmd gcloud "Authenticate with a principal that can build and deploy ${SERVICE}."
need_cmd bun "Install bun from https://bun.sh. Post-deploy smoke runs: cd regression && bun run smoke:docs"

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
      echo "Cloud Run did not promote this revision; the previous revision may still be serving. Skipping prod smoke." >&2
      gcloud run revisions describe "$revision" --project "$PROJECT" --region "$REGION" >&2 || true
      return 1
    fi

    if (( $(date +%s) >= deadline )); then
      echo "ERROR: timed out after ${READY_TIMEOUT_SECONDS}s waiting for ${revision} to become Ready (Ready=${status:-unknown}, latestReady=${latest_ready:-none})${message:+: $message}" >&2
      echo "Image build success is not deploy success. Skipping prod smoke." >&2
      gcloud run revisions describe "$revision" --project "$PROJECT" --region "$REGION" >&2 || true
      return 1
    fi

    echo "  ${revision} Ready=${status:-Unknown} latestReady=${latest_ready:-none}; retrying in ${READY_POLL_SECONDS}s"
    sleep "$READY_POLL_SECONDS"
  done
}

run_prod_smoke() {
  echo "Running post-deploy docs/health smoke against ${BASE_URL} ..."
  if ! (cd "$ROOT/regression" && BASE_URL="$BASE_URL" bun run smoke:docs); then
    echo "ERROR: post-deploy smoke failed against ${BASE_URL}. Revision may be Ready, but the docs/health contract is broken." >&2
    exit 1
  fi
}

echo "Building and pushing ${IMAGE} ..."
gcloud builds submit --tag "$IMAGE" --project "$PROJECT"

echo "Deploying ${SERVICE} in ${REGION} ..."
if ! gcloud run deploy "$SERVICE" \
  --image "$IMAGE" \
  --platform managed \
  --region "$REGION" \
  --memory 1Gi \
  --project "$PROJECT"; then
  echo "ERROR: gcloud run deploy failed. The new revision is not serving; the previous revision may still be up. Skipping prod smoke." >&2
  exit 1
fi

CREATED="$(trim "$(gcloud run services describe "$SERVICE" \
  --project "$PROJECT" \
  --region "$REGION" \
  --platform managed \
  --format='value(status.latestCreatedRevisionName)')")"

if [[ -z "$CREATED" ]]; then
  echo "ERROR: could not read status.latestCreatedRevisionName after deploy. Skipping prod smoke." >&2
  exit 1
fi

if ! wait_for_revision_ready "$CREATED"; then
  exit 1
fi

run_prod_smoke
echo "Deploy succeeded: ${CREATED} is Ready and smoke:docs passed against ${BASE_URL}."
