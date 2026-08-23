# TibiaWiki freshness audit (August 2026)

**Audience:** Benjamin / maintainers of [TibiaWikiApi](https://github.com/benjaminkomen/TibiaWikiApi) 2.0.0.
**Scope:** report only — no parser, model, or endpoint changes in this PR.
**Compared against:** live TibiaWiki at [tibia.fandom.com](https://tibia.fandom.com) (MediaWiki API `https://tibia.fandom.com/api.php`) and a spot-check of production [tibiawiki.dev](https://tibiawiki.dev) on 2026-08-23.
**Code snapshot:** `master` at the time of this branch (`InfoboxTemplate`, REST controllers, `JsonFactory` / `ArticleFactory`, domain `fieldOrder()`, `regression/fixtures/`).

## Summary

**Grade: C**

The live **GET** path is more current than the domain models suggest. `JsonFactory` is a generic infobox key/value parser, so new wiki parameters on existing article types already appear on https://tibiawiki.dev (for example Dragon has `mitigation`, Light Healing has `spellid` / `libraryname` / `basepower`, Adrenaline Burst has `type=Minor`). Category names for every exposed list still resolve on the wiki. Production is pointed at `https://tibia.fandom.com/api.php` (not the retired tibia.wikia.com host, despite README / OpenAPI still saying so).

The API has **not been curated against the wiki since roughly 2022** (last creature-parameter commit) and several core models are older (Spell 2019, Charm 2021, City/Vocation 2018–2019). That shows up as: no collection endpoints for Imbuements / Familiars / Updates / Game Worlds; a **Loot2 / Loot2_RC prefix collision that silently drops the regular loot table on production**; charm types that the wiki fully replaced; and no Monk vocation anywhere in enums or Hunt/Spell teacher fields. Fixtures and goldens are intentionally stubby and do **not** represent live wiki pages.

Read-only consumers of existing `/api/{type}/{name}` URLs are mostly fine. Anyone depending on loot v1, charm types, spell city-teacher fields, typed PUT mapping, or “the API lists every Infobox type” is not.

## Method

1. Inventory of `InfoboxTemplate`, Java `*Resource` + Kotlin `*Controller`, `WikiObject.fieldOrder()`, `JsonFactory` special-case keys, and `regression/fixtures/categories.json`.
2. Live MediaWiki API (rate-limited, identifying User-Agent): `allpages` in Template namespace (`Infobox*`, `Loot*`), `siteinfo` namespaces, template `/Documentation` wikitext, category sizes, `embeddedin` counts, and full wikitext of sampled high-traffic pages.
3. Production GET spot-check of the same titles on https://tibiawiki.dev, plus 404s for missing collection routes.
4. Fixture / golden comparison for Dragon, Light Healing, Adrenaline Burst, Sam, Ferumbras loot.

Requests were sequential with ≥0.45s delay. No scrape of HTML article bodies beyond the MediaWiki API.

## What the API exposes today

### Collection + item REST routes

| Route | Wiki template | Wiki category used | Live category size (pages) |
| --- | --- | --- | ---: |
| `/api/achievements` | `Infobox Achievement` | Achievements | 575 |
| `/api/books` | `Infobox Book` | Book Texts | 1228 |
| `/api/buildings` | `Infobox Building` | Buildings | 1153 |
| `/api/charms` | `Infobox Charm` | Charms | 26 |
| `/api/corpses` | `Infobox Corpse` | Corpses | 48 |
| `/api/creatures` | `Infobox Creature` | Creatures | 2209 |
| `/api/effects` | `Infobox Effect` | Effects | 199 |
| `/api/huntingplaces` | `Infobox Hunt` | Hunting Places | 462 |
| `/api/items` | `Infobox Object` (pickupable only) | Pickupable Objects | 6560 |
| `/api/keys` | `Infobox Key` | Keys | 159 |
| `/api/locations` | `Infobox Geography` | Locations | 183 |
| `/api/loot` and `/api/v2/loot` | `Loot2` / `Loot2_RC` | Loot Statistics (ns 112) | 1448 |
| `/api/missiles` | `Infobox Missile` | Missiles | 64 |
| `/api/mounts` | `Infobox Mount` | Mounts | 256 |
| `/api/npcs` | `Infobox NPC` | NPCs | 1251 |
| `/api/objects` | `Infobox Object` | Objects | 9980 |
| `/api/outfits` | `Infobox Outfit` | Outfits | 137 |
| `/api/quests` | `Infobox Quest` | Quest Overview Pages | 370 |
| `/api/spells` | `Infobox Spell` | Spells | 220 |
| `/api/streets` | `Infobox Street` | Streets | 169 |

Plus `/api/pages/{title}` (`WikiPageController`): any page whose wikitext contains `{{Infobox` is parsed and returned. This already works for types we do not list (Familiar, Imbuement, Update, …). There is no collection listing for those types.

`InfoboxTemplate` also names **Cipsoft_Member**, **Fansite**, **Update**, and **World** — none of those have a REST resource.

### How parsing actually works (important for “missing fields”)

- **GET** (`RetrieveAny` → `ArticleFactory.extractInfoboxPartOfArticle` → `JsonFactory.convertInfoboxPartOfArticleToJson`) emits **every** `| key = value` pair. New wiki parameters show up without a domain-model change.
- Special shaping exists only for `sounds`, `spawntype`, `loot` (creatures), `droppedby`, `itemid`, `effectid`, `lowerlevels`, and Loot2 lines.
- **PUT / typed mapping** (`WikiObject.fieldOrder()`, enums, `WikiObjectFactory`) is what goes stale. Extra keys are dropped on write-back; unknown enum values fail mapping.
- `WikiObjectFactory` has **no** branch for Charm, Missile, Familiar, Imbuement, Update, World, Fansite, or CipSoft Member.

## What still matches

- Live wiki host and Loot Statistics **namespace 112** match `JwikiArticleRepository` / `RetrieveLoot`.
- Every category string used by an existing list endpoint still exists and is populated (see table above). `Pickupable Objects` (the 2021 Items/Objects merge) is still the right Items category.
- Template **names** we parse are still the live ones: `Infobox Creature`, `Object`, `Spell`, `NPC`, `Achievement`, `Charm`, `Hunt`, `Quest`, `Building`, `Book`, `Key`, `Effect`, `Missile`, `Street`, `Geography`, `Corpse`, `Mount`, `Outfit`, `Loot2`.
- `Infobox Item` is unused on main/loot namespaces (`embeddedin` = 0). The API’s decision to treat items as Objects with `templateType=Object` matches the wiki.
- High-traffic samples (Dragon, Ferumbras, Carlin Sword, Soulbleeder, Sam, Light Healing, Chained Penance, Goo Goo Dancer, Donkey, Pirate Outfits) still use those Infobox templates. Production GET for each of those titles returned **200** with the same parameter names as the live wikitext.
- Quest, Building, Key, Effect, Missile, Corpse, Street, Geography, and Book documentation parameters still line up with `fieldOrder()` (last wiki docs for most of these are 2021 — they have been stable).
- Creature resistance / bestiary / bosstiary / sound-list / loot-table conventions are unchanged. `Spawntype` and `AttackType` enums still match the creature docs.
- Charm **page set** is still small and still uses `Infobox Charm` (25 charm articles).
- `Loot3` exists but is marked `{{Deprecated}}` and has **zero** main/loot transclusions. Safe to ignore.
- `Infobox Event` has zero main-namespace transclusions (2016 leftover).

## Concrete gaps

### Missing collection endpoints (wiki types we do not list)

Live `embeddedin` / category sizes, 2026-08-23:

| Wiki type | Approx. articles | Escape hatch today | Notes |
| --- | --- | --- | --- |
| **Infobox Imbuement** | 151 transclusions / 95 in Category:Imbuements | `/api/pages/Powerful_Strike` works | Distinct template since ~2018; never exposed as a list. |
| **Infobox Update** | 3000+ transclusions / 716 in Category:Updates | `/api/pages/…` | Docs updated 2026-07-22. High editorial traffic. |
| **Infobox World** | 292 transclusions / 300 in **Category:Game Worlds** | none as a list | Enum category is the stale name `Gameworlds` (that category **does not exist**). |
| **Infobox Transcript** | 1313 transclusions / 675 in Category:Transcripts | none useful | Dialogue box (`height` + lines), not a stats infobox. Different shape. |
| **Infobox Familiar** | 20 transclusions / 10 familiars | `/api/pages/Bladespark` works | Bladespark, Emberwing, Grovebeast, Moonhunter, Mossmasher, Omniphant, Sandscourge, Skullfrost, Snowbash, Thundergiant. |
| **Infobox Fansite** | 113 | none | Already in `InfoboxTemplate`, no controller. |
| **Infobox Cipsoft Member** | 83 | none | Already in `InfoboxTemplate`, no controller. |
| **Infobox Store Bundle** | 4 | — | Tiny. |
| **Infobox Tournament** | 7 | — | Tiny. |

Blessings (11 pages) and Soul Cores (834 pages) do **not** have their own Infobox; they are ordinary pages / Objects. No new endpoint needed unless we want a filtered Objects view.

Monk content lives inside existing types: **42 Monk Spells** (Category:Monk Spells), vocation string `[[Monk]]s` on Light Healing / Chained Penance, etc. There is no `Infobox Monk`.

### Stale or missing fields on types we already expose

Wiki documentation timestamps vs last model-touch:

| Type | Wiki docs last edited | Last model-parameter commit | Missing on the wiki (we still model) | Missing in our model (wiki has) |
| --- | --- | --- | --- | --- |
| **Creature** | 2026-04-19 | 2022-07-31 (`bosstiaryclass`) | `isevent` not in current docs | **`mitigation`**, **`cooldown`** (boss hours), **`maxbattlelength`** |
| **Spell** | 2026-05-13 | **2019-01-13** | Entire city-teacher grid (`d-abd`…`s-yal`), `zoltanonly`, `specialspell`, `conjurespell` | **`spellid`**, **`libraryname`**, **`librarytext`**, **`secondarygroup`**, **`cooldown2`/`cooldown3`**, **`wheelspell`**, **`passivespell`**, **`basepower`**. `voc` now includes Monks. Docs no longer list `promotion` / `soul` in the syntax block (still described in older mental model). |
| **Charm** | 2021-05-28 (docs stale) | 2021-05-22 | `Type` enum `Offensive` / `Defensive` / `Passive` | Live pages use **`Minor` / `Major`**. Category:Offensive/Defensive/Passive Charms are **empty**. Category:Minor Charms = 11, Major = 15. `cost` is now a tiered string (`100 / 150 / 225`). |
| **Object / Item** | **2026-08-22** | 2022-06-22 (`pricecurrency`) | `destructable` (typo alias; wiki is `destructible`, which we also have) | **`augments`**, **`task_item`** (487 Task Items), **`wrappable`**, **`cooldown`**, **`basepower`**, undocumented but live **`slot`**. `WeaponType` enum is only Axe/Club/Sword/Distance — wiki also documents **Wand, Rod, Fist**. |
| **NPC** | 2023-02-21 | (job/pos fields older) | **`buys` / `sells`** removed from docs and from Sam | **`subarea`**, **`geolabel`**, locations **6–7**, **`race2`**. `bubble` is explicitly deprecated. |
| **Mount** | 2026-08-05 | older | — | **`mount_id`**, **`actualname`**, **`colourisable`**, **`pricecurrency`**. Donkey live page has `mount_id=387`. |
| **Outfit** | 2026-05-02 | older | — | **`male_id` / `female_id`**, **`store`**, **`artwork2`/`artwork3`**, labels, `lightcolor`/`lightradius`. Pirate Outfits live page uses several of these. |
| **Achievement** | 2025-07-27 | older | — | **`unknown`**. Otherwise a close match. |
| **Hunt** | 2022-11-22 | older | — | No **Monk** level/skill/def columns (`lvlknights` / `lvlpaladins` / `lvlmages` only). |
| **Imbuement** (unlisted) | 2021-05-28 | — | — | Live Powerful Strike also has `type=Strike` (not in the docs syntax). |

GET already returns the “wiki has” columns for sampled pages on tibiawiki.dev. They are gaps in **models, enums, write-back, and docs** — not in the raw JSON pipe.

### Loot statistics

- Namespace **112 / Loot Statistics** is correct. Template **Loot2** is still the current format (~1295 transclusions). **Loot2 RC** (~211) is still used for reward-chest tables. **Loot3** is dead.
- Live pages write `{{Loot2_RC` (underscore). `ArticleFactory` looks for that string for v2 — good.
- **Bug (confirmed on production 2026-08-23):** extraction of `{{Loot2` is a prefix of `{{Loot2_RC`. On [Loot Statistics:Ferumbras](https://tibia.fandom.com/wiki/Loot_Statistics:Ferumbras) the RC table is first (`kills=143`, `version=12.86.11580`); the regular table is second (`kills=49`, `version=8.6`).
  - `GET https://tibiawiki.dev/api/loot/Ferumbras` (v1) returned the **RC** table (`kills=143`, `version=12.86.11580`).
  - `GET https://tibiawiki.dev/api/v2/loot/Ferumbras` returned **identical** `loot2` and `loot2_rc` objects, both the RC table. The 8.6 / 49-kill table is **silently dropped**.
- Loot2 gained a `note` / `alv` caption surface in the template (2026-02-06). We do not model those; they would pass through as lowercase keys if present.

### Sample pages vs domain / fixtures

| Page | Live wiki (2026-08-23) | tibiawiki.dev | Fixture / golden |
| --- | --- | --- | --- |
| **Dragon** | Full creature infobox incl. `mitigation=1.56`, bestiary, sounds, 20-line loot table (page edited 2026-06-25) | Same keys, including `mitigation` | Stub: 6 loot-ish fields, no bestiary / mitigation / sounds |
| **Light Healing** | `spellid=1`, `basepower=40`, `libraryname`, `voc` includes **Monks**, no city-teacher fields (edited 2026-05-06) | Same 18 keys | Stub: 10 keys, no spellid / library / monks |
| **Chained Penance** | Monk-only spell, `implemented=15.00.249ccc` | 200, `voc=[[Monk]]s` | Not in fixtures |
| **Sam** | `buysell=yes`, no `buys`/`sells` (edited 2025-07-26) | Same | Fixture still a simplified NPC |
| **Goo Goo Dancer** | Unchanged since 2021-05-26; matches our field list | Same | Close to live (this one aged well) |
| **Adrenaline Burst** | `type=Minor`, `cost=100 / 150 / 225` (edited 2024-11-26) | Same | Golden still `type=Offensive`, `cost=100` |
| **Soulbleeder** | Object + `slot`, `augments` not present, `upgradeclass=4` | 200 as `/api/items/Soulbleeder` | Not in fixtures |
| **Bladespark / Powerful Strike** | Familiar / Imbuement infoboxes | 200 via `/api/pages/…`; **404** on `/api/familiars` and `/api/imbuements` | Not in fixtures |
| **Ferumbras loot** | `Loot2_RC` then `Loot2` | v1=RC; v2 both=RC (see above) | Fixture has a tiny Loot2 + Loot2_RC pair |

**tibiawiki.dev vs fixtures:** they are **not** consistent, and should not be treated as a live-wiki snapshot. The Bun goldens are contract tests against `regression/fixtures/`, which are hand-minimized. Production is a live jwiki read and is consistent with **current wiki wikitext** for the sampled GET URLs (loot prefix bug aside).

### Other wiki features the API does not treat as first-class

- **Monk** (client 15.00, 2024–2025): 42 spells, `voc` values, no Hunt columns, `Vocation` enum is still knight/paladin/druid/sorcerer only.
- **Cities / areas missing from `City`:** Issavi and Marapur exist as Geography pages; Category:Marapur NPCs has 33 members. `City` last grew in 2019 (no Issavi, Marapur, Bounac, …). GET still emits the string; typed NPC mapping cannot.
- **Weapon Proficiency / Augments** (Summer Update 2025): Object `augments` parameter; mentioned in Soulbleeder history. Not in `TibiaObject`.
- **Task items:** 487 pages, Object `task_item=yes`.
- **Wheel of Destiny** spell fields (`wheelspell`, `cooldown2`, `cooldown3`).
- NPC **buy/sell price tables** have moved to `Module:ItemPrices/data` (stated in Object docs). We still only expose `buyfrom` / `sellto` name lists.
- README and `OpenAPIConfiguration` still advertise `tibia.wikia.com`.

## Breaking changes on the wiki that can silently drop data

These are the ones that do **not** fail loudly on GET.

1. **`{{Loot2` matches `{{Loot2_RC`.** v1 can return the wrong table; v2 can duplicate RC and drop the regular table. Production Ferumbras confirms this. Highest-severity parser bug found in this audit.
2. **Charm type rename (Offensive/Defensive/Passive → Minor/Major).** GET returns the new strings. `Charm.Type` cannot represent them. PUT / `WikiObjectFactory` mapping (if used) fails or strips the value. Fixtures teach the old world.
3. **Spell city-teacher parameters removed** from live Light Healing (and current docs). GET no longer has `d-tha` etc. A PUT that writes `Spell.fieldOrder()` would **re-insert** dozens of obsolete keys or lose the new `spellid` / library / wheel fields.
4. **NPC `buys` / `sells` removed** from the template. Our `fieldOrder()` still lists them. Write-back would resurrect empty trade lists; readers looking only at those keys see nothing even when `buysell=yes`.
5. **`Infobox Item` retired.** Any client still keying on `templateType=Item` will not see live items (`templateType=Object`). README already documents this; it remains a footgun.
6. **`Gameworlds` vs `Game Worlds`.** If a Worlds list is ever wired from `InfoboxTemplate.WORLD.getCategoryName()`, it will return an empty category with no error.
7. **Unknown enum values** (`City=Issavi`, `WeaponType=Wand|Rod|Fist`, `Charm.Type=Minor`, future bestiary classes) fail only on the typed path. Expand/GET JSON is safe.
8. **Creature `isevent`** is in our model and not in current creature docs. Low risk (unused) but the same write-back class of bug.
9. **Object `destructable` vs `destructible`.** We keep both; wiki standardized on `destructible`. A writer using the typo key would not update the live parameter.

## Prioritized backlog

### P0 — correctness / silent data loss

1. **Disambiguate Loot2 vs Loot2_RC** in `ArticleFactory` (do not treat `{{Loot2` as a prefix). Add a fixture page that has RC **first**, then Loot2, and assert v1 vs v2. Re-check Ferumbras on a fixtures profile after the fix (do not capture goldens from live Fandom in CI).
2. **Charm types:** accept `Minor` / `Major` (and keep the old names as deprecated aliases if any historical pages remain). Refresh the Adrenaline Burst fixture to the live shape (`type=Minor`, tiered `cost`) so goldens stop encoding a retired taxonomy.
3. **Spell write-back inventory:** stop emitting the city-teacher grid / `zoltanonly` / `specialspell` / `conjurespell` unless a page still has them; add `spellid`, `libraryname`, `librarytext`, `basepower`, `wheelspell`, `passivespell`, `cooldown2`, `cooldown3`, `secondarygroup`. Treat this as a contract change for PUT, not GET.

### P1 — catalog and 2024–2026 game features

4. **Collection endpoints** (same list+expand pattern as existing resources), in this order:
   - `/api/imbuements` (Category:Imbuements)
   - `/api/updates` (Category:Updates)
   - `/api/worlds` (Category:**Game Worlds**, not `Gameworlds`)
   - `/api/familiars` (Category:Familiars)
5. **Monk:** add `monk` to `Vocation`; parse `voc` values that include Monks; add Hunt monk columns if/when the wiki adds them (today they do not — until then, document that Hunt is knight/paladin/mage-only).
6. **Creature model:** `mitigation`, boss `cooldown`, `maxbattlelength`. Dragon production already returns `mitigation`.
7. **Object model:** `augments`, `task_item`, `wrappable`, `cooldown`, `basepower`, `slot`; extend `WeaponType` with Wand, Rod, Fist.
8. **City enum:** Issavi, Marapur, and a pass over current Geography/NPC `city=` values so typed NPC mapping does not drop new hometowns.
9. **Mount / Outfit IDs:** `mount_id`, `male_id`, `female_id`, `store`, extra artwork fields, `colourisable`, `pricecurrency`.
10. **NPC:** `subarea`, `geolabel*`, positions 6–7; stop requiring `buys`/`sells` for a complete NPC.
11. **`WikiObjectFactory`:** Charm + Missile (already have GET resources) and any new P1 types, so PUT has a chance of working.
12. **Docs hygiene:** README + OpenAPI `tibia.wikia.com` → `tibia.fandom.com`. Optional: point at this audit.

### P2 — smaller or different-shaped data

13. Fansites and CipSoft Members (already in the enum).
14. Transcripts (675+ pages) — only if someone wants dialogue as JSON; the template is not a key/value infobox in the usual sense.
15. Store Bundles / Tournaments (single-digit counts).
16. Structured parse of `{{Ability List}}` / `{{Max Damage}}` / `{{Imbuement Effect/…}}` (today they are opaque strings — same as live production).
17. `Module:ItemPrices/data` as a first-class price table (wiki itself moved NPC prices there).
18. Enrich regression fixtures toward *representative* live shapes (Dragon with mitigation + loot rarities, Light Healing with `spellid` + Monks, one Monk-only spell, one Imbuement, one Familiar) **using the fixtures profile only**.
19. Hunt/Spell docs still say “sorcerer or druid” for mages; track wiki if they add Monk rows.
20. `Infobox Streets` (city overview) vs `Infobox Street` (single street) — Category:Streets mixes both.

## Suggested grade path

| If we only… | Likely grade |
| --- | --- |
| Fix Loot2/Loot2_RC + Charm types + Spell fieldOrder | **B** — existing resources trustworthy |
| Also add Imbuements, Updates, Worlds, Familiars + Monk/City/Object/Creature fields | **A-** — catalog matches the live Infobox set that editors actually use |
| Plus Transcripts / price module / structured abilities | **A** — nice-to-have, not required for “up to date with TibiaWiki” |

## Appendix: live Infobox templates that are real article types

Root templates seen under `Template:Infobox*` (ignoring `/Documentation`, `/List`, `/Draft`, and other subpages):

**In the API as lists:** Achievement, Book, Building, Charm, Corpse, Creature, Effect, Geography, Hunt, Key, Missile, Mount, NPC, Object, Outfit, Quest, Spell, Street.

**In the enum only:** Cipsoft Member, Fansite, Update, World.

**Not in the API:** Familiar, Imbuement, Transcript, Store Bundle, Tournament, Contest (unused), Event (unused), Item (retired), Rune/Wand (legacy), Creature2/Object2 (drafts), Location (alias/legacy vs Geography), Monster (legacy), World Quest, Streets (plural), QuestList.

## Appendix: production URLs checked

All 200 unless noted, 2026-08-23:

- `/api/creatures/Dragon`, `/api/spells/Light_Healing`, `/api/spells/Chained_Penance`, `/api/npcs/Sam`, `/api/achievements/Goo_Goo_Dancer`, `/api/charms/Adrenaline_Burst`
- `/api/items/Carlin_Sword`, `/api/items/Soulbleeder`, `/api/objects/Blueberry_Bush`, `/api/mounts/Donkey`, `/api/outfits/Pirate_Outfits`
- `/api/loot/Ferumbras`, `/api/v2/loot/Ferumbras` (wrong table assignment; see P0)
- `/api/pages/Bladespark`, `/api/pages/Powerful_Strike`
- **404:** `/api/familiars`, `/api/imbuements`, `/api/worlds`, `/api/updates`
