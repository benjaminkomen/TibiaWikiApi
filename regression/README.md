# API regression harness

Black-box HTTP snapshots for the TibiaWiki JSON API. This folder is **not** part of
the Gradle test task — Bun compares live HTTP JSON to committed goldens.

Wiki article data can go slightly stale. That is accepted. Refreshing goldens is
intentional: run `bun run capture` when you want a new baseline.

## Fixture mode vs live mode

| Mode | Wiki backend | When to use |
| --- | --- | --- |
| **fixtures** (CI + default local refresh) | In-process `FixtureArticleRepository` reading `regression/fixtures/` | GitHub Actions, everyday golden refresh |
| **live** | jwiki → `wiki.api-url` (default `https://tibia.fandom.com/api.php`), with timeouts, retries, and a short-TTL cache | Rare, manual only |

**CI uses fixtures only.** The `fixtures` Spring profile never constructs
`JwikiArticleRepository`, so the process makes **no outbound calls** to
`fandom.com` or `tibiawiki.dev`. The Bun harness talks only to
`http://localhost:8080`.

Do **not** run a live capture from Cloud Run, parallel cloud agents, or CI.
Hitting Fandom or tibiawiki.dev from many workers will rate-limit or take the
public API down. If you ever need to refresh wiki *source* fixtures from Fandom,
do it **once**, single-threaded, with sleeps — there is no automated live job.

## Commands

| Script | What it does |
| --- | --- |
| `bun run capture` | GET each case in `endpoints.json` and write `goldens/<id>.json` as `{ path, status, body }` |
| `bun run test` | GET each case, normalize JSON, compare to the committed golden, print a short diff, exit non-zero on mismatch |

Use `bun run test` (not `bun test`). Bare `bun test` is Bun's built-in test runner and will not execute this script.

`BASE_URL` selects the HTTP API (default `http://localhost:8080`).

```bash
# fixture-backed local API (same as CI) — from the repo root
./regression/scripts/boot-fixtures.sh
# other terminal
./regression/scripts/wait-for-api.sh
cd regression
bun run capture    # refresh goldens from the fixture-backed server
bun run test

# live Fandom-backed bootRun (manual, sequential, never from CI)
./gradlew bootRun
cd regression
BASE_URL=http://localhost:8080 bun run test
```

JSON bodies are normalized with a recursive stable key sort and pretty-printed
before they are saved or compared, so key order alone is not a failure.

Requests are spaced (`REQUEST_GAP_MS`, default 400) and transient HTTP 5xx /
network errors are retried (`FETCH_RETRIES`, default 6).

## Current goldens

The committed files in `goldens/` were captured from the **fixture-backed**
local server (`SPRING_PROFILES_ACTIVE=fixtures`, `http://localhost:8080`).
They are self-consistent with `regression/fixtures/` and are **not** a snapshot
of production.

```bash
# after boot-fixtures.sh is serving :8080
cd regression
bun run capture
```

## Fixtures

`regression/fixtures/categories.json` lists wiki category members.
`regression/fixtures/articles/*.wiki` is the wikitext for those pages (and for
`Loot_Statistics:Ferumbras`). Titles use spaces; the repository also accepts
underscore aliases.

These files were written from the infobox samples already in this repo's unit
tests, plus representative field shapes from the 2026 freshness audit — not by
hammering Fandom.

Representative pages (fixtures profile only):

| Page | What it encodes |
| --- | --- |
| Dragon | Creature `mitigation` plus a loot table with rarity tokens |
| Light Healing | `spellid`, `basepower`, `libraryname`, and `voc` including Monks |
| Chained Penance | Monk-only spell (`voc=[[Monk]]s`) |
| Powerful Strike | `Infobox Imbuement` via `/api/pages/Powerful_Strike` |
| Bladespark | `Infobox Familiar` via `/api/pages/Bladespark` |
| `Loot_Statistics:Dragon` | `Loot2_RC` **first**, then `Loot2` (v1 vs v2 ordering) |

`wiki.fixtures.path` / `WIKI_FIXTURES_PATH` defaults to `regression/fixtures`
(resolved from the repo root).

## CI

`.github/workflows/api-regression.yml` on push/PR to `master`:

1. JDK 25 + Bun
2. `SPRING_PROFILES_ACTIVE=fixtures ./gradlew bootRun`
3. wait for `http://localhost:8080/api/corpses`
4. `cd regression && bun run test`

On failure the workflow uploads `boot-fixtures.log` and `regression-test.log`.

## Coverage

Every resource in the root README table has a name-list GET and the documented
example detail GET. Underscore encoding is used for spaces (and parentheses in
the book example). `/api/v2/loot` has the same list + Ferumbras detail coverage
as `/api/loot` (v2 returns `loot2` / `loot2_rc` parts instead of a single loot2
object). Ferumbras is Loot2 then Loot2_RC; Demon and Dragon are Loot2_RC first
then Loot2 so v1 vs v2 stay disambiguated when the reward-chest table precedes
regular loot (`{{Loot2` must not match `{{Loot2_RC`). `/api/pages/{title}` is
covered with Dragon (same infobox JSON as `/api/creatures/Dragon`) plus one
Imbuement and one Familiar.

Missing: `/api/creatures/ThisDoesNotExistXYZ123` (fixture has no such article →
**404**, empty body).

`?expand=true` is included for the same types as before (small fixture
categories, so expand payloads stay tiny). Types whose *production* expand
response is huge still have name-only lists only.
