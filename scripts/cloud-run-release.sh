#!/usr/bin/env bash
# Deploy a pre-built image to Cloud Run with the production env/probe flags,
# wait until the new revision is Ready, then run regression smoke:docs.
#
# This is the single source of truth for Cloud Run deploy flags (F-02).
# Callers:
#   cloudbuild.yaml     IMAGE=...:$COMMIT_SHA, BASE_URL unset → smoke the
#                       revision URL if present, else the service URL after
#                       Ready (Swagger + actuator; no wiki/Fandom).
#   scripts/deploy.sh   IMAGE=...:$COMMIT_SHA, BASE_URL=https://tibiawiki.dev (ops).
#                       :latest is retagged only after this script succeeds.
#
# gcloud run deploy already sends 100% traffic to the new revision once it
# becomes Ready. Do not add `gcloud alpha run services update-traffic
# --to-latest` — that can send traffic to latestCreated before Ready.
#
# Required: gcloud, bun, python3, a reachable IMAGE.
#
# Env:
#   IMAGE                  Image to deploy (required).
#   PROJECT                GCP project (default tibiawikiapi-246008).
#   REGION                 Cloud Run region (default europe-west1).
#   SERVICE                Cloud Run service (default tibiawikiapi).
#   BASE_URL               smoke:docs target. Unset = revision status.url if
#                          present, else service status.url / status.address.url
#                          (Cloud Run revisions often omit status.url).
#   READY_TIMEOUT_SECONDS  Wait for Ready (default 420; probe is 36×10s).
#   READY_POLL_SECONDS     Poll interval (default 10).
#
# Ready wait uses gcloud --format=json + python3 (scripts/lib/cloud-run-ready.sh),
# not conditions[?type=Ready] projections (empty in Cloud Build; issue #477).
# Dry-run / regression: ./scripts/lib/cloud-run-ready.sh self-test
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
need_cmd python3 "Ready-wait JSON parse (issue #477) uses python3; it is present in gcr.io/cloud-builders/gcloud."

# shellcheck source=lib/cloud-run-ready.sh
source "$ROOT/scripts/lib/cloud-run-ready.sh"

if [[ -z "${IMAGE:-}" ]]; then
  echo "ERROR: IMAGE is required (e.g. gcr.io/${PROJECT}/tibiawikiapi:\$COMMIT_SHA)." >&2
  exit 1
fi

# Must stay aligned with cloudbuild.yaml / docker/README.md probe table.
UPDATE_ENV_VARS="LOGGING_JSON=true,WIKI_WRITE_ENABLED=false"
STARTUP_PROBE="httpGet.path=/actuator/health/readiness,timeoutSeconds=4,periodSeconds=10,failureThreshold=36"
LIVENESS_PROBE="httpGet.path=/actuator/health/liveness,timeoutSeconds=4,periodSeconds=30,failureThreshold=3"

revision_describe_json() {
  local revision="$1"
  gcloud run revisions describe "$revision" \
    --project "$PROJECT" \
    --region "$REGION" \
    --format=json
}

service_describe_json() {
  gcloud run services describe "$SERVICE" \
    --project "$PROJECT" \
    --region "$REGION" \
    --platform managed \
    --format=json
}

# Prefer a revision-specific URL when Cloud Run exposes one; otherwise the
# service URL. Called only after wait_for_revision_ready succeeded.
post_ready_smoke_url() {
  local revision="$1"
  local rev_json svc_json status message rev_url svc_url latest_ready
  rev_json="$(revision_describe_json "$revision")"
  svc_json="$(service_describe_json)"
  read_revision_describe_fields status message rev_url < <(printf '%s' "$rev_json" | parse_revision_describe_json)
  svc_url="$(trim "$(printf '%s' "$svc_json" | parse_service_url_json)")"
  latest_ready="$(trim "$(printf '%s' "$svc_json" | parse_service_latest_ready_json)")"
  resolve_smoke_url "$(trim "$rev_url")" "$svc_url" "$revision" "$latest_ready"
}

wait_for_revision_ready() {
  local revision="$1"
  local deadline=$(($(date +%s) + READY_TIMEOUT_SECONDS))
  echo "Waiting up to ${READY_TIMEOUT_SECONDS}s for revision ${revision} to become Ready..."

  while true; do
    local rev_json svc_json status message url latest_ready decision http_ready
    rev_json="$(revision_describe_json "$revision")"
    svc_json="$(service_describe_json)"
    read_revision_describe_fields status message url < <(printf '%s' "$rev_json" | parse_revision_describe_json)
    url="$(trim "$url")"
    latest_ready="$(trim "$(printf '%s' "$svc_json" | parse_service_latest_ready_json)")"
    http_ready=""

    decision="$(evaluate_ready_wait "$revision" "$status" "$latest_ready" "$http_ready")"
    case "$decision" in
      success:*)
        echo "Revision ${revision} is Ready (${decision#success:}; Ready=${status:-unparsed}, latestReady=${latest_ready:-none})."
        return 0
        ;;
      fail:ready_false)
        echo "ERROR: revision ${revision} Ready=False${message:+: $message}" >&2
        echo "Cloud Run did not promote this revision; the previous revision may still be serving. Skipping smoke." >&2
        gcloud run revisions describe "$revision" --project "$PROJECT" --region "$REGION" >&2 || true
        return 1
        ;;
    esac

    # Both JSON fields empty: last-resort HTTP on the revision URL (not tibiawiki.dev).
    if [[ -z "$status" || "$status" == "Unknown" ]] && [[ -z "$latest_ready" && -n "$url" ]]; then
      if http_readiness_ok "$url"; then
        decision="$(evaluate_ready_wait "$revision" "$status" "$latest_ready" "yes")"
        if [[ "$decision" == success:* ]]; then
          echo "Revision ${revision} Ready condition unparsed; revision URL readiness returned HTTP 200 (${url})."
          return 0
        fi
      fi
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

CREATED_URL="$(trim "$(post_ready_smoke_url "$CREATED")")"
if [[ -z "$CREATED_URL" ]]; then
  echo "ERROR: could not resolve a smoke URL for revision ${CREATED} (revision status.url empty; service status.url / status.address.url also empty or latestReady is a different revision). Skipping smoke." >&2
  exit 1
fi

SMOKE_URL="${BASE_URL:-$CREATED_URL}"
if [[ -n "${BASE_URL:-}" ]]; then
  echo "Smoke target BASE_URL=${BASE_URL} (ops). Resolved Cloud Run URL is ${CREATED_URL}."
else
  echo "Smoke target is ${CREATED_URL} (revision URL if present, else service URL; not tibiawiki.dev)."
fi

run_smoke "$SMOKE_URL"
echo "Deploy succeeded: ${CREATED} is Ready and smoke:docs passed against ${SMOKE_URL}."
