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
BASE_URL=https://tibiawiki.dev bun run capture
```

## Coverage

Every resource in the root README table has a name-list GET and the documented
example detail GET. Underscore encoding is used for spaces (and parentheses in
the book example); those paths return HTTP 200. `%20` also works but is not used
in the goldens.

| Resource | List | Example detail |
| --- | --- | --- |
| Achievement | `/api/achievements` | `/api/achievements/Goo_Goo_Dancer` |
| Books | `/api/books` | `/api/books/Dungeon_Survival_Guide_(Book)` |
| Buildings | `/api/buildings` | `/api/buildings/Theater_Avenue_8b` |
| Charms | `/api/charms` | `/api/charms/Adrenaline_Burst` |
| Corpses | `/api/corpses` | `/api/corpses/Dead_Rat` |
| Creatures | `/api/creatures` | `/api/creatures/Dragon` |
| Effects | `/api/effects` | `/api/effects/Blue_Electricity_Effect` |
| Hunting Places | `/api/huntingplaces` | `/api/huntingplaces/Hero_Cave` |
| Items | `/api/items` | `/api/items/Carlin_Sword` |
| Keys | `/api/keys` | `/api/keys/Key_4055` |
| Locations | `/api/locations` | `/api/locations/Thais` |
| Loot Statistics | `/api/loot` | `/api/loot/Ferumbras` |
| Missiles | `/api/missiles` | `/api/missiles/Throwing_Cake_Missile` |
| Mounts | `/api/mounts` | `/api/mounts/Donkey` |
| NPCs | `/api/npcs` | `/api/npcs/Sam` |
| Objects | `/api/objects` | `/api/objects/Blueberry_Bush` |
| Outfits | `/api/outfits` | `/api/outfits/Pirate_Outfits` |
| Quests | `/api/quests` | `/api/quests/The_Paradox_Tower_Quest` |
| Spells | `/api/spells` | `/api/spells/Light_Healing` |
| Streets | `/api/streets` | `/api/streets/Sugar_Street` |

Missing: `/api/creatures/ThisDoesNotExistXYZ123` (live status/body is recorded;
production currently returns **404** with an empty body).

### `?expand=true`

Included only when the response is under ~500KB. Name-only lists are always
snapshotted, including for types whose expand payload is too large.

| Path | Approx. size | In harness? |
| --- | --- | --- |
| `/api/achievements?expand=true` | ~289KB | yes |
| `/api/books?expand=true` | ~1.5MB | **skipped** |
| `/api/buildings?expand=true` | ~782KB | **skipped** |
| `/api/charms?expand=true` | ~18KB | yes |
| `/api/corpses?expand=true` | ~18KB | yes |
| `/api/creatures?expand=true` | ~5.1MB | **skipped** |
| `/api/effects?expand=true` | ~55KB | yes |
| `/api/huntingplaces?expand=true` | ~218KB | yes |
| `/api/items?expand=true` | ~5.1MB | **skipped** |
| `/api/keys?expand=true` | ~86KB | yes |
| `/api/locations?expand=true` | ~20KB | yes |
| `/api/loot?expand=true` | ~1.2MB | **skipped** |
| `/api/missiles?expand=true` | ~12KB | yes |
| `/api/mounts?expand=true` | ~148KB | yes |
| `/api/npcs?expand=true` | ~656KB | **skipped** |
| `/api/objects?expand=true` | ~6.8MB | **skipped** |
| `/api/outfits?expand=true` | ~85KB | yes |
| `/api/quests?expand=true` | ~256KB | yes |
| `/api/spells?expand=true` | ~293KB | yes |
| `/api/streets?expand=true` | ~160KB | yes |
