#!/usr/bin/env bash
# Parse Cloud Run revision/service JSON and decide Ready-wait outcome.
#
# Sourced by scripts/cloud-run-release.sh. Also a CLI for regression:
#   ./scripts/lib/cloud-run-ready.sh self-test
#   ./scripts/lib/cloud-run-ready.sh evaluate --created REV \
#       --revision-json FILE --service-json FILE [--http-ready yes|no]
#
# Why JSON + python3 (issue #477):
#   gcloud --format='value[separator=|](status.conditions[?type=Ready].status,...)'
#   returns empty in the Cloud Build gcloud image, so bash ${status:-Unknown}
#   loops until timeout even when the revision is Ready and is
#   latestReadyRevisionName. python3 is already used in that same Cloud Build
#   step; jq is not guaranteed in gcr.io/cloud-builders/gcloud.
#
# evaluate_ready_wait never treats an unparsed Ready condition as success by
# itself: empty/Unknown must be corroborated by latestReadyRevisionName or
# HTTP readiness. Ready=False always fails closed, even if those fallbacks
# would otherwise pass.
#
# Post-Ready smoke URL (Cloud Build after #482): prefer revision status.url /
# status.address.url when present; otherwise the service URL. Cloud Run
# *revisions* often omit status.url; *services* expose it.

trim() {
  printf '%s' "$1" | tr -d '[:space:]'
}

# Pull status.url, else status.address.url, from a describe JSON status object.
# Cloud Run *services* expose status.url; *revisions* often omit it (Cloud Build
# post-deploy smoke failed with empty revision status.url after Ready).
_cloud_run_status_url_py() {
  python3 -c '
import json, sys

raw = sys.stdin.read()
url = ""
try:
    doc = json.loads(raw)
except json.JSONDecodeError:
    doc = None
if isinstance(doc, dict):
    st = doc.get("status") or {}
    if isinstance(st, dict):
        url = st.get("url") or ""
        if not url:
            addr = st.get("address") or {}
            if isinstance(addr, dict):
                url = addr.get("url") or ""
sys.stdout.write(str(url).replace("\n", "").replace("\r", "").replace("\t", "") + "\n")
'
}

# stdin: gcloud run {revisions|services} describe --format=json
# stdout: status.url or status.address.url (may be empty).
parse_describe_url_json() {
  _cloud_run_status_url_py
}

# stdin: gcloud run services describe --format=json
# stdout: status.url or status.address.url (may be empty).
parse_service_url_json() {
  _cloud_run_status_url_py
}

# stdin: gcloud run revisions describe --format=json
# stdout: three lines — status, message, url (any may be empty).
# Not TAB-separated: bash IFS=tab collapses empty fields (issue #477 class of bug).
# url is status.url or status.address.url; revisions often have neither.
parse_revision_describe_json() {
  python3 -c '
import json, sys

raw = sys.stdin.read()
status = message = url = ""
try:
    doc = json.loads(raw)
except json.JSONDecodeError:
    doc = None
if isinstance(doc, dict):
    st = doc.get("status") or {}
    if isinstance(st, dict):
        url = st.get("url") or ""
        if not url:
            addr = st.get("address") or {}
            if isinstance(addr, dict):
                url = addr.get("url") or ""
        conds = st.get("conditions")
        if isinstance(conds, list):
            for cond in conds:
                if isinstance(cond, dict) and cond.get("type") == "Ready":
                    status = cond.get("status") or ""
                    message = cond.get("message") or ""
                    break


def clean(value):
    return str(value).replace("\n", " ").replace("\r", " ")

sys.stdout.write(clean(status) + "\n" + clean(message) + "\n" + clean(url) + "\n")
'
}

# Reads parse_revision_describe_json stdout into the three named variables.
read_revision_describe_fields() {
  local _status _message _url
  IFS= read -r _status
  IFS= read -r _message
  IFS= read -r _url
  printf -v "$1" '%s' "$_status"
  printf -v "$2" '%s' "$_message"
  printf -v "$3" '%s' "$_url"
}

# stdin: gcloud run services describe --format=json
# stdout: latestReadyRevisionName (may be empty)
parse_service_latest_ready_json() {
  python3 -c '
import json, sys

raw = sys.stdin.read()
name = ""
try:
    doc = json.loads(raw)
except json.JSONDecodeError:
    doc = None
if isinstance(doc, dict):
    st = doc.get("status") or {}
    if isinstance(st, dict):
        name = st.get("latestReadyRevisionName") or ""
sys.stdout.write(str(name).replace("\n", "").replace("\r", "").replace("\t", "") + "\n")
'
}

# HTTP GET {revision_url}/actuator/health/readiness → 200.
http_readiness_ok() {
  local base="$1"
  local url="${base%/}/actuator/health/readiness"
  python3 -c '
import sys
import urllib.error
import urllib.request

url = sys.argv[1]
try:
    req = urllib.request.Request(url, method="GET")
    with urllib.request.urlopen(req, timeout=5) as resp:
        sys.exit(0 if resp.status == 200 else 1)
except Exception:
    sys.exit(1)
' "$url"
}

# After Ready wait succeeded: prefer a revision-specific URL if Cloud Run
# exposed one, else the service URL (status.url / status.address.url).
# Do not use the service URL when latestReady names a *different* revision
# (that would smoke whatever is currently serving, not this deploy).
# Empty latestReady is allowed: Ready wait already returned, and Cloud Run
# revisions often omit status.url even when the service URL is present.
resolve_smoke_url() {
  local revision_url="$1"
  local service_url="$2"
  local created="${3:-}"
  local latest_ready="${4:-}"
  revision_url="$(trim "$revision_url")"
  service_url="$(trim "$service_url")"
  latest_ready="$(trim "$latest_ready")"
  created="$(trim "$created")"

  if [[ -n "$revision_url" ]]; then
    printf '%s' "$revision_url"
    return 0
  fi
  if [[ -n "$service_url" ]]; then
    if [[ -n "$created" && "$latest_ready" == "$created" ]]; then
      printf '%s' "$service_url"
      return 0
    fi
    # Ready wait already returned; revision JSON often has no URL.
    if [[ -z "$latest_ready" ]]; then
      printf '%s' "$service_url"
      return 0
    fi
  fi
  printf '%s' ""
}

# Prints one token:
#   success:ready_true        Ready=True and latestReady matches or is unparsed
#   success:latest_ready      Ready not False; latestReady == created (issue #477)
#   success:http_readiness    Ready not False; HTTP 200 and latestReady not a different rev
#   fail:ready_false          Ready=False (fail closed)
#   wait                      keep polling
#
# Always returns 0 so `decision="$(evaluate_ready_wait ...)"` is set -e safe.
evaluate_ready_wait() {
  local created="$1"
  local ready_status="$2"
  local latest_ready="$3"
  local http_ready="${4:-}"

  if [[ "$ready_status" == "False" ]]; then
    printf '%s\n' "fail:ready_false"
    return 0
  fi

  if [[ "$ready_status" == "True" ]]; then
    if [[ "$latest_ready" == "$created" && -n "$created" ]]; then
      printf '%s\n' "success:ready_true"
      return 0
    fi
    if [[ -z "$latest_ready" ]]; then
      printf '%s\n' "success:ready_true"
      return 0
    fi
    # Ready=True on this revision, but Cloud Run still names another latestReady.
    printf '%s\n' "wait"
    return 0
  fi

  if [[ -n "$created" && "$latest_ready" == "$created" ]]; then
    printf '%s\n' "success:latest_ready"
    return 0
  fi

  if [[ "$http_ready" == "yes" ]]; then
    if [[ -z "$latest_ready" || "$latest_ready" == "$created" ]]; then
      printf '%s\n' "success:http_readiness"
      return 0
    fi
  fi

  printf '%s\n' "wait"
}

cloud_run_ready_self_test() {
  local failed=0
  local got json status message url latest

  check() {
    local name="$1"
    local expected="$2"
    shift 2
    got="$(evaluate_ready_wait "$@")"
    if [[ "$got" != "$expected" ]]; then
      echo "FAIL ${name}: expected ${expected}, got ${got} (created=$1 ready=$2 latest=$3 http=${4:-})" >&2
      failed=1
    else
      echo "ok ${name}"
    fi
  }

  # Already Ready: one-poll success (do not sleep).
  check already_ready_true success:ready_true \
    tibiawikiapi-00142-mjz True tibiawikiapi-00142-mjz

  # Issue #477: empty projection parse + latestReady match must not wait.
  check empty_status_latest_ready_match success:latest_ready \
    tibiawikiapi-00142-mjz "" tibiawikiapi-00142-mjz

  check unknown_status_latest_ready_match success:latest_ready \
    tibiawikiapi-00142-mjz Unknown tibiawikiapi-00142-mjz

  # Fail closed: Ready=False wins over latestReady and HTTP.
  check ready_false_despite_latest fail:ready_false \
    tibiawikiapi-00142-mjz False tibiawikiapi-00142-mjz
  check ready_false_despite_http fail:ready_false \
    tibiawikiapi-00142-mjz False tibiawikiapi-00141-zww yes

  # Still starting: previous revision is latestReady, no HTTP — wait (not success).
  check still_starting_wait wait \
    tibiawikiapi-00142-mjz "" tibiawikiapi-00141-zww
  check still_starting_unknown_wait wait \
    tibiawikiapi-00142-mjz Unknown tibiawikiapi-00141-zww no

  # HTTP fallback only when latestReady is missing (both gcloud fields empty).
  check both_fields_empty_http_ok success:http_readiness \
    tibiawikiapi-00142-mjz "" "" yes
  check both_fields_empty_no_http wait \
    tibiawikiapi-00142-mjz "" "" no

  # Ready=True on this rev but another rev is still latestReady: not promoted.
  check true_but_not_promoted wait \
    tibiawikiapi-00142-mjz True tibiawikiapi-00141-zww
  check true_not_promoted_http_does_not_override wait \
    tibiawikiapi-00142-mjz True tibiawikiapi-00141-zww yes

  # Ready=True and latestReady unparsed: revision itself is Ready.
  check true_latest_unparsed success:ready_true \
    tibiawikiapi-00142-mjz True ""

  # Empty created must not match empty latestReady.
  check empty_created_does_not_match wait \
    "" "" ""

  json='{"status":{"conditions":[{"type":"Ready","status":"True","message":"Container started."}],"url":"https://rev.example"}}'
  read_revision_describe_fields status message url < <(printf '%s' "$json" | parse_revision_describe_json)
  if [[ "$status" != "True" || "$message" != "Container started." || "$url" != "https://rev.example" ]]; then
    echo "FAIL parse_ready_true: status=${status@Q} message=${message@Q} url=${url@Q}" >&2
    failed=1
  else
    echo "ok parse_ready_true"
  fi

  json='{"status":{"conditions":[{"type":"ContainerHealthy","status":"True"}],"url":"https://rev.example"}}'
  read_revision_describe_fields status message url < <(printf '%s' "$json" | parse_revision_describe_json)
  if [[ -n "$status" || "$url" != "https://rev.example" ]]; then
    echo "FAIL parse_missing_ready_condition: expected empty status + url, got status=${status@Q} url=${url@Q}" >&2
    failed=1
  else
    echo "ok parse_missing_ready_condition"
  fi

  json='{"status":{"conditions":[{"type":"Ready","status":"False","message":"probe failed"}],"url":"https://rev.example"}}'
  read_revision_describe_fields status message url < <(printf '%s' "$json" | parse_revision_describe_json)
  if [[ "$status" != "False" || "$message" != "probe failed" ]]; then
    echo "FAIL parse_ready_false: status=${status@Q} message=${message@Q}" >&2
    failed=1
  else
    echo "ok parse_ready_false"
  fi

  json='not-json'
  read_revision_describe_fields status message url < <(printf '%s' "$json" | parse_revision_describe_json)
  if [[ -n "$status" || -n "$message" ]]; then
    echo "FAIL parse_malformed_json: expected empty, got status=${status@Q}" >&2
    failed=1
  else
    echo "ok parse_malformed_json"
  fi

  json='{"status":{"latestReadyRevisionName":"tibiawikiapi-00142-mjz"}}'
  latest="$(printf '%s' "$json" | parse_service_latest_ready_json)"
  if [[ "$latest" != "tibiawikiapi-00142-mjz" ]]; then
    echo "FAIL parse_latest_ready: got ${latest@Q}" >&2
    failed=1
  else
    echo "ok parse_latest_ready"
  fi

  json='{"status":{}}'
  latest="$(printf '%s' "$json" | parse_service_latest_ready_json)"
  if [[ -n "$latest" ]]; then
    echo "FAIL parse_latest_ready_missing: got ${latest@Q}" >&2
    failed=1
  else
    echo "ok parse_latest_ready_missing"
  fi

  # Cloud Build #482: revision JSON is Ready but has no status.url.
  json='{"status":{"conditions":[{"type":"Ready","status":"True","message":"Container started."}]}}'
  read_revision_describe_fields status message url < <(printf '%s' "$json" | parse_revision_describe_json)
  if [[ "$status" != "True" || -n "$url" ]]; then
    echo "FAIL parse_revision_url_missing: status=${status@Q} url=${url@Q}" >&2
    failed=1
  else
    echo "ok parse_revision_url_missing"
  fi

  json='{"status":{"conditions":[{"type":"Ready","status":"True"}],"address":{"url":"https://rev-address.example"}}}'
  read_revision_describe_fields status message url < <(printf '%s' "$json" | parse_revision_describe_json)
  if [[ "$status" != "True" || "$url" != "https://rev-address.example" ]]; then
    echo "FAIL parse_revision_address_url: status=${status@Q} url=${url@Q}" >&2
    failed=1
  else
    echo "ok parse_revision_address_url"
  fi

  json='{"status":{"url":"https://tibiawikiapi-191142814790.europe-west1.run.app","latestReadyRevisionName":"tibiawikiapi-00144-wtl"}}'
  got="$(trim "$(printf '%s' "$json" | parse_service_url_json)")"
  if [[ "$got" != "https://tibiawikiapi-191142814790.europe-west1.run.app" ]]; then
    echo "FAIL parse_service_url: got ${got@Q}" >&2
    failed=1
  else
    echo "ok parse_service_url"
  fi

  json='{"status":{"address":{"url":"https://svc-address.example"},"latestReadyRevisionName":"tibiawikiapi-00144-wtl"}}'
  got="$(trim "$(printf '%s' "$json" | parse_service_url_json)")"
  if [[ "$got" != "https://svc-address.example" ]]; then
    echo "FAIL parse_service_address_url: got ${got@Q}" >&2
    failed=1
  else
    echo "ok parse_service_address_url"
  fi

  json='{"status":{"latestReadyRevisionName":"tibiawikiapi-00144-wtl"}}'
  got="$(trim "$(printf '%s' "$json" | parse_service_url_json)")"
  if [[ -n "$got" ]]; then
    echo "FAIL parse_service_url_missing: got ${got@Q}" >&2
    failed=1
  else
    echo "ok parse_service_url_missing"
  fi

  check_smoke_url() {
    local name="$1"
    local expected="$2"
    shift 2
    got="$(resolve_smoke_url "$@")"
    if [[ "$got" != "$expected" ]]; then
      echo "FAIL ${name}: expected ${expected@Q}, got ${got@Q} (rev=$1 svc=$2 created=$3 latest=$4)" >&2
      failed=1
    else
      echo "ok ${name}"
    fi
  }

  check_smoke_url resolve_prefers_revision_url \
    "https://rev.example" \
    "https://rev.example" "https://svc.example" tibiawikiapi-00144-wtl tibiawikiapi-00144-wtl

  # The #482 failure: Ready=True, latestReady matches, revision status.url empty.
  check_smoke_url resolve_smoke_url_service_fallback \
    "https://tibiawikiapi-191142814790.europe-west1.run.app" \
    "" "https://tibiawikiapi-191142814790.europe-west1.run.app" tibiawikiapi-00144-wtl tibiawikiapi-00144-wtl

  check_smoke_url resolve_smoke_url_unparsed_latest_ready \
    "https://svc.example" \
    "" "https://svc.example" tibiawikiapi-00144-wtl ""

  check_smoke_url resolve_smoke_url_other_latest_ready \
    "" \
    "" "https://svc.example" tibiawikiapi-00144-wtl tibiawikiapi-00141-zww

  check_smoke_url resolve_smoke_url_both_empty \
    "" \
    "" "" tibiawikiapi-00144-wtl tibiawikiapi-00144-wtl

  local tmp rc
  tmp="$(mktemp -d)"
  trap 'rm -rf "$tmp"' RETURN
  printf '%s\n' '{"status":{"conditions":[{"type":"ContainerHealthy","status":"True"}],"url":"https://rev.example"}}' >"$tmp/rev-empty-ready.json"
  printf '%s\n' '{"status":{"conditions":[{"type":"Ready","status":"False","message":"probe failed"}],"url":"https://rev.example"}}' >"$tmp/rev-false.json"
  printf '%s\n' '{"status":{"latestReadyRevisionName":"tibiawikiapi-00142-mjz"}}' >"$tmp/svc-match.json"
  printf '%s\n' '{"status":{"latestReadyRevisionName":"tibiawikiapi-00141-zww"}}' >"$tmp/svc-old.json"

  got="$(bash "$0" evaluate --created tibiawikiapi-00142-mjz --revision-json "$tmp/rev-empty-ready.json" --service-json "$tmp/svc-match.json")"
  if [[ "$got" != "success:latest_ready" ]]; then
    echo "FAIL cli_empty_ready_latest_match: got ${got@Q}" >&2
    failed=1
  else
    echo "ok cli_empty_ready_latest_match"
  fi

  rc=0
  got="$(bash "$0" evaluate --created tibiawikiapi-00142-mjz --revision-json "$tmp/rev-false.json" --service-json "$tmp/svc-match.json")" || rc=$?
  if [[ "$got" != "fail:ready_false" || "$rc" != "1" ]]; then
    echo "FAIL cli_ready_false: got ${got@Q} rc=${rc}" >&2
    failed=1
  else
    echo "ok cli_ready_false"
  fi

  rc=0
  got="$(bash "$0" evaluate --created tibiawikiapi-00142-mjz --revision-json "$tmp/rev-empty-ready.json" --service-json "$tmp/svc-old.json")" || rc=$?
  if [[ "$got" != "wait" || "$rc" != "2" ]]; then
    echo "FAIL cli_still_starting: got ${got@Q} rc=${rc}" >&2
    failed=1
  else
    echo "ok cli_still_starting"
  fi

  if (( failed )); then
    echo "cloud-run-ready self-test FAILED" >&2
    return 1
  fi
  echo "cloud-run-ready self-test passed"
}

cloud_run_ready_cli_evaluate() {
  local created="" revision_json="" service_json="" http_ready=""
  while [[ $# -gt 0 ]]; do
    case "$1" in
      --created)
        created="$2"
        shift 2
        ;;
      --revision-json)
        revision_json="$2"
        shift 2
        ;;
      --service-json)
        service_json="$2"
        shift 2
        ;;
      --http-ready)
        http_ready="$2"
        shift 2
        ;;
      *)
        echo "ERROR: unknown argument: $1" >&2
        echo "usage: $0 evaluate --created REV --revision-json FILE --service-json FILE [--http-ready yes|no]" >&2
        return 1
        ;;
    esac
  done
  if [[ -z "$created" || -z "$revision_json" || -z "$service_json" ]]; then
    echo "ERROR: --created, --revision-json, and --service-json are required" >&2
    return 1
  fi

  local status message url latest decision
  read_revision_describe_fields status message url < <(parse_revision_describe_json <"$revision_json")
  latest="$(trim "$(parse_service_latest_ready_json <"$service_json")")"
  decision="$(evaluate_ready_wait "$created" "$status" "$latest" "$http_ready")"
  printf '%s\n' "$decision"
  case "$decision" in
    success:*) return 0 ;;
    fail:*) return 1 ;;
    *) return 2 ;;
  esac
}

if [[ "${BASH_SOURCE[0]}" == "$0" ]]; then
  set -euo pipefail
  cmd="${1:-}"
  case "$cmd" in
    self-test)
      cloud_run_ready_self_test
      ;;
    evaluate)
      shift
      cloud_run_ready_cli_evaluate "$@"
      ;;
    *)
      echo "usage: $0 self-test | evaluate --created REV --revision-json FILE --service-json FILE [--http-ready yes|no]" >&2
      exit 1
      ;;
  esac
fi
