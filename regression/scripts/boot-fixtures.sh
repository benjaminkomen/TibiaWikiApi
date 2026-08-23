#!/usr/bin/env bash
# Start the Java API with the offline fixtures profile (no Fandom / tibiawiki.dev).
# Run from the repository root.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-fixtures}"
export WIKI_FIXTURES_PATH="${WIKI_FIXTURES_PATH:-${ROOT}/regression/fixtures}"
exec ./gradlew bootRun --no-daemon --args="--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"
