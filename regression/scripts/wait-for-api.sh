#!/usr/bin/env bash
# Wait until the local API answers on BASE_URL (default http://localhost:8080).
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
ATTEMPTS="${ATTEMPTS:-90}"
SLEEP_SECS="${SLEEP_SECS:-2}"

for i in $(seq 1 "$ATTEMPTS"); do
  if curl -fsS -o /dev/null --max-time 5 "${BASE_URL}/api/corpses"; then
    echo "API is up at ${BASE_URL} (attempt ${i})"
    exit 0
  fi
  echo "waiting for ${BASE_URL} (${i}/${ATTEMPTS})"
  sleep "$SLEEP_SECS"
done

echo "API did not become ready at ${BASE_URL}" >&2
exit 1
