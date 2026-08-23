# API regression harness

Black-box HTTP snapshots for the TibiaWiki JSON API. This folder is **not** part of
the Gradle/Java test suite — run it with [Bun](https://bun.sh) against a live server.

Wiki article data can go slightly stale. That is accepted. Refreshing goldens is
intentional: run `bun run capture` when you want a new baseline.

## Commands

| Script | What it does |
| --- | --- |
| `bun run capture` | GET each case in `endpoints.json` and write `goldens/<id>.json` as `{ path, status, body }` |
| `bun run test` | GET each case, normalize JSON, compare to the committed golden, print a short diff, exit non-zero on mismatch |

Use `bun run test` (not `bun test`). Bare `bun test` is Bun's built-in test runner and will not execute this script.

`BASE_URL` selects the server (default `http://localhost:8080`).

```bash
# against a local bootRun (see the repo root README)
cd regression
bun run capture
bun run test

# against production
BASE_URL=https://tibiawiki.dev bun run capture
BASE_URL=https://tibiawiki.dev bun run test
```

JSON bodies are normalized with a recursive stable key sort and pretty-printed
before they are saved or compared, so key order alone is not a failure.

## Current goldens

The committed files in `goldens/` were captured from **`https://tibiawiki.dev`**
on 2026-08-23 (cloud VM; starting the Java app locally is heavier than hitting
the public API).

Re-capture from whichever host you want to treat as source of truth:

```bash
BASE_URL=https://tibiawiki.dev bun capture
```

## Coverage

Lists: `/api/corpses`, `/api/creatures`, `/api/achievements`, `/api/spells`, `/api/items`.

Details: Dragon, Carlin Sword, Sam, Light Healing, Dead Rat.

Missing: `/api/creatures/ThisDoesNotExistXYZ123` (live status/body is recorded;
production currently returns **404** with an empty body).

### Path encoding

Names with spaces accept both underscores and `%20` (HTTP 200):

- `/api/items/Carlin_Sword` and `/api/items/Carlin%20Sword`
- `/api/spells/Light_Healing` and `/api/spells/Light%20Healing`
- `/api/corpses/Dead_Rat` and `/api/corpses/Dead%20Rat`

The goldens use **underscores**, matching the examples in the root README.
Literal spaces in the URL path are not valid HTTP and were not used.

### `?expand=true`

Included only when the response is under ~500KB:

| Path | Approx. size | In harness? |
| --- | --- | --- |
| `/api/corpses?expand=true` | ~18KB | yes |
| `/api/achievements?expand=true` | ~289KB | yes |
| `/api/spells?expand=true` | ~293KB | yes |
| `/api/creatures?expand=true` | ~5.1MB | **skipped** |
| `/api/items?expand=true` | ~5.1MB | **skipped** |

Name-only lists for creatures and items are still snapshotted.
