#!/usr/bin/env bash
# Boot the production image and fail unless Cloud Run's startup probe path is UP.
#
# Catches ENTRYPOINT/PORT bugs that Gradle, ITs, fixture smoke:docs, and Sonar
# miss (issue #476 / #473): nothing else ran the container entrypoint before
# Cloud Run. Dockerfile `$$` in `sh -c` expanded to PID 1 (`1PORT`).
#
# Usage (repo root):
#   docker build -t tibiawikiapi -f ./docker/Dockerfile .
#   ./scripts/docker-boot-smoke.sh tibiawikiapi
#   BUILD=1 ./scripts/docker-boot-smoke.sh          # build then smoke
#
# Env:
#   BUILD            If 1, docker build -f ./docker/Dockerfile . first.
#   SMOKE_PORTS      Ports to boot (default "8080 19080").
#   SMOKE_NETWORK    If set (Cloud Build: cloudbuild), attach containers to
#                    that network and probe by container name. Otherwise
#                    publish -p PORT:PORT and GET http://127.0.0.1:PORT/...
#   SMOKE_ATTEMPTS   Readiness polls per port (default 90).
#   SMOKE_SLEEP_SECS Poll interval (default 2).
#   PROBE_IMAGE      Sidecar used when SMOKE_NETWORK is set
#                    (default mirror.gcr.io/library/busybox:1.36.1).

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

IMAGE="${1:-tibiawikiapi}"
BUILD="${BUILD:-0}"
SMOKE_PORTS="${SMOKE_PORTS:-8080 19080}"
SMOKE_ATTEMPTS="${SMOKE_ATTEMPTS:-90}"
SMOKE_SLEEP_SECS="${SMOKE_SLEEP_SECS:-2}"
PROBE_IMAGE="${PROBE_IMAGE:-mirror.gcr.io/library/busybox:1.36.1}"
EXPECTED_ENTRYPOINT='["java","-Dserver.address=0.0.0.0","-jar","/project/TibiaWikiApi.jar"]'
NAME_PREFIX="tibiawikiapi-boot-smoke"
active_container=""

need_cmd() {
  local cmd="$1"
  local hint="$2"
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "ERROR: ${cmd} is required. ${hint}" >&2
    exit 1
  fi
}

need_cmd docker "Install Docker. Image boot smoke is the CI gate for ENTRYPOINT/PORT."

cleanup() {
  if [[ -n "$active_container" ]]; then
    docker rm -f "$active_container" >/dev/null 2>&1 || true
    active_container=""
  fi
}
trap cleanup EXIT

compact_json() {
  printf '%s' "$1" | tr -d ' \t\n\r'
}

assert_exec_form_java_entrypoint() {
  local entrypoint cmd compact
  entrypoint="$(docker inspect -f '{{json .Config.Entrypoint}}' "$IMAGE")"
  compact="$(compact_json "$entrypoint")"
  echo "docker inspect Entrypoint: ${entrypoint}"

  if [[ "$compact" != "$EXPECTED_ENTRYPOINT" ]]; then
    echo "ERROR: image ENTRYPOINT must be exec-form java with no shell and no \$ / \$\$." >&2
    echo "  expected: ${EXPECTED_ENTRYPOINT}" >&2
    echo "  actual:   ${entrypoint}" >&2
    exit 1
  fi
  if [[ "$entrypoint" == *'$'* || "$entrypoint" == *'$$'* ]]; then
    echo "ERROR: image ENTRYPOINT contains \$ / \$\$ (shell PORT wiring). Use Spring server.port=\${PORT:8080}." >&2
    exit 1
  fi
  # Exact match above already bans sh; keep an explicit check for the bug class.
  if [[ "$compact" == *'"sh"'* || "$compact" == *'"-c"'* ]]; then
    echo "ERROR: image ENTRYPOINT uses sh -c. Ban shell ENTRYPOINT for PORT wiring." >&2
    exit 1
  fi

  cmd="$(docker inspect -f '{{json .Config.Cmd}}' "$IMAGE")"
  if [[ "$cmd" == *'$'* || "$cmd" == *'"sh"'* ]]; then
    echo "ERROR: image CMD must not wire PORT through a shell (${cmd})." >&2
    exit 1
  fi
}

fetch_readiness() {
  local url="$1"
  local container="$2"
  local port="$3"
  if [[ -n "${SMOKE_NETWORK:-}" ]]; then
    docker run --rm --network "$SMOKE_NETWORK" "$PROBE_IMAGE" \
      wget -qO- -T 5 "http://${container}:${port}/actuator/health/readiness"
  else
    curl -fsS --max-time 5 "$url"
  fi
}

wait_for_readiness() {
  local port="$1"
  local container="$2"
  local url="http://127.0.0.1:${port}/actuator/health/readiness"
  local i body

  for i in $(seq 1 "$SMOKE_ATTEMPTS"); do
    if [[ "$(docker inspect -f '{{.State.Running}}' "$container" 2>/dev/null || true)" != "true" ]]; then
      echo "ERROR: container ${container} is not running while waiting for readiness." >&2
      docker logs "$container" >&2 || true
      return 1
    fi
    if body="$(fetch_readiness "$url" "$container" "$port" 2>/dev/null)" \
      && printf '%s' "$body" | grep -q 'UP'; then
      echo "readiness is UP on PORT=${port} (attempt ${i}): ${body}"
      return 0
    fi
    echo "waiting for readiness on PORT=${port} (${i}/${SMOKE_ATTEMPTS})"
    sleep "$SMOKE_SLEEP_SECS"
  done

  echo "ERROR: GET ${url} did not return 200/UP within $((SMOKE_ATTEMPTS * SMOKE_SLEEP_SECS))s." >&2
  docker logs "$container" >&2 || true
  return 1
}

assert_pid1_is_java() {
  local container="$1"
  local comm
  comm="$(docker exec "$container" cat /proc/1/comm)"
  if [[ "$comm" != "java" ]]; then
    echo "ERROR: PID 1 must be java (exec-form ENTRYPOINT), not '${comm}' (shell PID 1 expands \$\$)." >&2
    docker logs "$container" >&2 || true
    exit 1
  fi
  echo "PID 1 is java"
}

boot_port() {
  local port="$1"
  local name="${NAME_PREFIX}-${port}"
  local -a run_args

  docker rm -f "$name" >/dev/null 2>&1 || true
  run_args=(
    run -d --name "$name"
    -e "PORT=${port}"
    -e LOGGING_JSON=true
    -e WIKI_WRITE_ENABLED=false
  )
  if [[ -n "${SMOKE_NETWORK:-}" ]]; then
    run_args+=(--network "$SMOKE_NETWORK")
  else
    need_cmd curl "Install curl to probe http://127.0.0.1:${port}/actuator/health/readiness."
    run_args+=(-p "${port}:${port}")
  fi
  run_args+=("$IMAGE")

  echo "Booting ${IMAGE} with PORT=${port} ..."
  docker "${run_args[@]}"
  active_container="$name"

  wait_for_readiness "$port" "$name"
  assert_pid1_is_java "$name"

  docker rm -f "$name" >/dev/null
  active_container=""
}

if [[ "$BUILD" == "1" ]]; then
  echo "Building ${IMAGE} from ./docker/Dockerfile (repo root context) ..."
  docker build -t "$IMAGE" -f ./docker/Dockerfile .
fi

if ! docker image inspect "$IMAGE" >/dev/null 2>&1; then
  echo "ERROR: image '${IMAGE}' not found. Build first:" >&2
  echo "  docker build -t ${IMAGE} -f ./docker/Dockerfile ." >&2
  echo "  or: BUILD=1 $0 ${IMAGE}" >&2
  exit 1
fi

assert_exec_form_java_entrypoint

for port in $SMOKE_PORTS; do
  boot_port "$port"
done

echo "Docker image boot smoke passed for PORTS=${SMOKE_PORTS}."
