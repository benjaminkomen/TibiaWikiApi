#!/usr/bin/env bash
# Ops deploy: build :latest, then the shared Cloud Run release (Ready +
# tibiawiki.dev docs/health smoke).
#
# Merge-to-prod is cloudbuild.yaml (tag $COMMIT_SHA and :latest, Ready wait,
# smoke against the revision URL). Do not call this from PR CI — live
# https://tibiawiki.dev smoke is ops only.
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
# Env/probe flags (LOGGING_JSON, WIKI_WRITE_ENABLED, startup/liveness) live in
# scripts/cloud-run-release.sh so they cannot drift from cloudbuild.yaml.
#
# Success = new revision Ready AND smoke:docs pass. Deploy/Ready failure skips
# smoke and exits non-zero (previous revision may still be serving).

set -euo pipefail

PROJECT="tibiawikiapi-246008"
REGION="europe-west1"
SERVICE="tibiawikiapi"
IMAGE="gcr.io/${PROJECT}/tibiawikiapi"
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

echo "Building and pushing ${IMAGE} ..."
gcloud builds submit --tag "$IMAGE" --project "$PROJECT"

export PROJECT REGION SERVICE IMAGE BASE_URL
exec "$ROOT/scripts/cloud-run-release.sh"
