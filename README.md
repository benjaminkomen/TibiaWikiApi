![Build Status](https://github.com/benjaminkomen/TibiaWikiApi/workflows/Build/badge.svg)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.tibiawiki%3ATibiaWikiApi&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.tibiawiki%3ATibiaWikiApi)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.tibiawiki%3ATibiaWikiApi&metric=coverage)](https://sonarcloud.io/dashboard?id=com.tibiawiki%3ATibiaWikiApi)

# TibiaWikiApi
Gets data from https://tibia.fandom.com and exposes this data using a RESTful JSON API.

## View online
Navigate to https://tibiawiki.dev to view the Swagger API of this project.

## Run locally
Requires JDK 25. Clone this git project to your local computer and compile it using: `./gradlew build` from your favourite command line
terminal. Then execute: `./gradlew bootRun` and open your browser on http://localhost:8080
 
You can now access the REST resources using your browser or any REST client such as Postman or curl from your command line.
E.g. navigating to http://localhost:8080/api/corpses should give you a list of corpses.

Process health (no Fandom/wiki calls): `GET /actuator/health`,
`/actuator/health/liveness`, `/actuator/health/readiness`, and `/actuator/info`.
Cloud Run probe settings are documented in [`docker/README.md`](docker/README.md).

## Fandom client (default profile)

The default Spring profile talks to TibiaWiki on Fandom. That hop is treated as unreliable:

- `wiki.api-url` and `wiki.user-agent` are configurable (no hardcoded client constants)
- Each wiki call has a timeout (`wiki.call-timeout`, default 20s) and is retried with full jitter
- Category member lists and single-page wikitext are cached in-process (`wiki.cache.ttl`, default 60s)
- `?expand=true` is served from that cache and rejected with HTTP 413 if the category is larger than `wiki.expand.max-pages` (default 5000)
- The jwiki `Wiki` client is created on first use, so a Fandom outage does not fail process start
- Set `wiki.warm-on-startup=true` on Cloud Run min-instances to build `Wiki` at boot instead of on the first request

The `fixtures` profile does not construct this client and never calls Fandom.

## API regression

A Bun-based black-box harness in [`regression/`](regression/README.md) snapshots HTTP JSON
and compares later responses to those goldens. It is not part of the Gradle test task.

GitHub Actions (`.github/workflows/api-regression.yml`) boots the API with
`--spring.profiles.active=fixtures` — an in-process wiki repository that reads
`regression/fixtures/` — then runs `bun run test` against `http://localhost:8080`.
That job never calls Fandom or tibiawiki.dev.

```bash
./regression/scripts/boot-fixtures.sh   # repo root, other terminal
cd regression && bun run test
```

Use `bun run capture` against the fixture-backed server to refresh goldens.
See [`regression/README.md`](regression/README.md).

## Wiki writes (PUT)

Public Cloud Run leaves `WIKI_WRITE_ENABLED` unset (false). Unauthenticated
clients cannot mutate TibiaWiki through tibiawiki.dev. `ModifyAny` stays in
the codebase for a future bot.

To enable PUT locally or for a bot:

```bash
export WIKI_WRITE_ENABLED=true
export WIKI_WRITE_TOKEN=optional-shared-secret   # optional; if set, required on PUT
```

When a token is configured, send `Authorization: Bearer <token>` or
`X-WIKI-Write-Token`. Missing or wrong tokens return HTTP 401. When writes
are disabled, PUT returns HTTP 403.

CORS allows GET (plus HEAD/OPTIONS) from `https://tibiawiki.dev` and local
`bootRun` origins. Credentials are not enabled. Override origins with
`WIKI_CORS_ALLOWED_ORIGINS` (`*` for any GET origin).

## Query parameters
For all resources the query parameter `?expand=true` can be appended to get a full list of JSON objects
 at the collection resource level. For example, instead of https://tibiawiki.dev/api/achievements the url
 https://tibiawiki.dev/api/achievements?expand=true can be used. Categories larger than
 `wiki.expand.max-pages` return HTTP 413 instead of bulk-fetching Fandom.

## Resources

The following resources are available:

| Entity          | List                                                        | Example                                                                                            |
|:-------------   |:------------------------------------------------------      |:-------------------------------------------------------------------------------------------------- |
| Achievement     | [achievements](https://tibiawiki.dev/api/achievements)      | [Goo Goo Dancer](https://tibiawiki.dev/api/achievements/Goo_Goo_Dancer)                            |
| Books           | [books](https://tibiawiki.dev/api/books)                    | [Dungeon Survival Guide (Book)](https://tibiawiki.dev/api/books/Dungeon_Survival_Guide_%28Book%29) |
| Buildings       | [buildings](https://tibiawiki.dev/api/buildings)            | [Theater Avenue 8b](https://tibiawiki.dev/api/buildings/Theater_Avenue_8b)                         |
| Charms          | [charms](https://tibiawiki.dev/api/charms)                  | [Adrenaline Burst](https://tibiawiki.dev/api/charms/Adrenaline_Burst)                              |
| CipSoft Members | [cipsoftmembers](https://tibiawiki.dev/api/cipsoftmembers)  | [Knightmare](https://tibiawiki.dev/api/cipsoftmembers/Knightmare)                                   |
| Corpses         | [corpses](https://tibiawiki.dev/api/corpses)                | [Dead Rat](https://tibiawiki.dev/api/corpses/Dead_Rat)                                             |
| Creatures       | [creatures](https://tibiawiki.dev/api/creatures)            | [Dragon](https://tibiawiki.dev/api/creatures/Dragon)                                               |
| Effects         | [effects](https://tibiawiki.dev/api/effects)                | [Blue Electricity Effect](https://tibiawiki.dev/api/effects/Blue_Electricity_Effect)               |
| Familiars       | [familiars](https://tibiawiki.dev/api/familiars)            | [Grovebeast](https://tibiawiki.dev/api/familiars/Grovebeast)                                       |
| Fansites        | [fansites](https://tibiawiki.dev/api/fansites)              | [TibiaWiki](https://tibiawiki.dev/api/fansites/TibiaWiki)                                           |
| Hunting Places  | [hunting places](https://tibiawiki.dev/api/huntingplaces)   | [Hero Cave](https://tibiawiki.dev/api/huntingplaces/Hero_Cave)                                     |
| Imbuements      | [imbuements](https://tibiawiki.dev/api/imbuements)          | [Powerful Strike](https://tibiawiki.dev/api/imbuements/Powerful_Strike)                            |
| Items<sup>1</sup> | [items](https://tibiawiki.dev/api/items)                  | [Carlin Sword](https://tibiawiki.dev/api/items/Carlin_Sword)                                       |
| Keys            | [keys](https://tibiawiki.dev/api/keys)                      | [Key 4055](https://tibiawiki.dev/api/keys/Key_4055)                                                |
| Locations       | [locations](https://tibiawiki.dev/api/locations)            | [Thais](https://tibiawiki.dev/api/locations/Thais)                                                 |
| Loot Statistics | [loot](https://tibiawiki.dev/api/loot)                      | [Ferumbras](https://tibiawiki.dev/api/loot/Ferumbras)                                              |
| Missiles        | [missiles](https://tibiawiki.dev/api/missiles)              | [Throwing Cake Missile](https://tibiawiki.dev/api/missiles/Throwing_Cake_Missile)                  |
| Mounts          | [mounts](https://tibiawiki.dev/api/mounts)                  | [Donkey](https://tibiawiki.dev/api/mounts/Donkey)                                                  |
| NPCs            | [npcs](https://tibiawiki.dev/api/npcs)                      | [Sam](https://tibiawiki.dev/api/npcs/Sam)                                                          |
| Objects<sup>1</sup> | [objects](https://tibiawiki.dev/api/objects)            | [Blueberry Bush](https://tibiawiki.dev/api/objects/Blueberry_Bush)                                 |
| Outfits         | [outfits](https://tibiawiki.dev/api/outfits)                | [Pirate Outfits](https://tibiawiki.dev/api/outfits/Pirate_Outfits)                                 |
| Quests          | [quests](https://tibiawiki.dev/api/quests)                  | [The Paradox Tower Quest](https://tibiawiki.dev/api/quests/The_Paradox_Tower_Quest)                |
| Spells          | [spells](https://tibiawiki.dev/api/spells)                  | [Light Healing](https://tibiawiki.dev/api/spells/Light_Healing)                                    |
| Streets         | [streets](https://tibiawiki.dev/api/streets)                | [Sugar Street](https://tibiawiki.dev/api/streets/Sugar_Street)                                     |
| Updates         | [updates](https://tibiawiki.dev/api/updates)                | [Summer Update 2020](https://tibiawiki.dev/api/updates/Summer_Update_2020)                         |
| Worlds          | [worlds](https://tibiawiki.dev/api/worlds)                  | [Antica](https://tibiawiki.dev/api/worlds/Antica)                                                  |

<sup>1</sup> as of 2021 the categories Items and Objects were merged on the wiki. To be backwards compatible with systems
relying on an accurate list of Items, this API now returns a list of 'Pickupable Items' from the Items endpoint, but which
have the Object templateType.

Hunting Places follow Infobox Hunt: recommended level, skill, and defence are knight, paladin, and mage only.
TibiaWiki has not added Monk columns yet, so this API does not invent them. Spell `voc` values that include Monks
are parsed into typed `vocations` (the original `voc` wiki string is unchanged).
