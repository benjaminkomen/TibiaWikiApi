# GitHub issues triage — August 2026

Analysis only. No features were implemented. Open issues were **not** closed.

**Repo:** [benjaminkomen/TibiaWikiApi](https://github.com/benjaminkomen/TibiaWikiApi)
**Base:** `master` @ `f780606` (Kotlin rewrite + Sonar follow-ups through #388)
**Stack:** Kotlin 2.4.10, Spring Boot 4.1.1, Java 25, jwiki 1.11.0
**Open issues scanned:** `gh issue list --state open` → **4** (`#210`, `#75`, `#57`, `#29`). No other open issues. No open PRs at triage time.

---

## How this API works (constraints that apply to every issue)

Production code is Kotlin under `src/main/kotlin`. The read path is a **live Fandom proxy**, not a search index:

```
Controller → Retrieve* (process) → ArticleRepository → ArticleFactory → JsonFactory → JSON
```

- `JwikiArticleRepository` (`!fixtures`) talks to `https://tibia.fandom.com/api.php` via jwiki. It can list category members, fetch page wikitext, and edit pages. It has **no** imageinfo or search methods.
- `FixtureArticleRepository` (`fixtures` profile) reads `regression/fixtures/` and never calls Fandom. CI and golden refresh use this path.
- `ArticleFactory` keeps only the `{{Infobox …}}` (plus loot templates). Body sections, Look boxes, and NPC transcript subpages are discarded.
- GET responses are **infobox JSON**, not the Kotlin data classes. `TibiaObject` / `NPC` / … are used for PUT. Extra response-only fields must be skipped on write (same pattern as `templateType`).
- List endpoints return names, or full infobox objects with `?expand=true`. There is **no cache**. `#15` (Add caching) was closed `not_planned` in 2024.
- `GET /api/pages/{title}` also parses only the infobox, and `{title}` does not match slashes (`Sam/Transcripts` 404s).

These limits matter more than the Kotlin/Boot upgrade: the four open issues are still about **what this proxy should compute or fetch**, not about Java vs Kotlin.

---

## Prioritized recommendations (for Benjamin)

| Priority | Issue | Recommendation | Why |
| ---: | --- | --- | --- |
| 1 | [#75](https://github.com/benjaminkomen/TibiaWikiApi/issues/75) images | **Backlog** — first slice is worth doing when you next touch GET JSON | Highest user value; MediaWiki already proven; keep CDN resolution off `?expand=true` |
| 2 | [#210](https://github.com/benjaminkomen/TibiaWikiApi/issues/210) green / look text | **Backlog** (`nicetohave`) — scoped constructor only | No extra Fandom calls; clients can already build it; full client-parity is a trap |
| 3 | [#57](https://github.com/benjaminkomen/TibiaWikiApi/issues/57) searchable | **Needs clarification** — name `?q=` is cheap; itemid index is not | Live proxy + no cache; OT itemid dumps belong in tibiawiki-sql / client-side `?expand=true` |
| 4 | [#29](https://github.com/benjaminkomen/TibiaWikiApi/issues/29) transcripts | **Backlog** — raw/lightly structured subpages only | Possible, but largest parser surface; do not start without a consumer |

**Do now:** none of these, unless you specifically want a small win. They are 5–7 years stale and none block the current API.

**Do not close yet:** none is clearly obsolete. The wiki still has look text, images, itemids, and NPC `/Transcripts` subpages.

**Related closed issue:** [#15 Add caching](https://github.com/benjaminkomen/TibiaWikiApi/issues/15) (`not_planned`, 2024). Reopening that is a prerequisite for cheap itemid search or imageinfo-on-expand. Do not treat search-as-an-index as in-scope while `#15` stays closed.

---

## #210 — In the case of the items, is it possible to obtain the green text?

- **Opened:** 2021-09-30 by [@nickcamastra](https://github.com/nickcamastra)
- **URL:** https://github.com/benjaminkomen/TibiaWikiApi/issues/210
- **Labels today:** none

### Restated ask

Expose the Tibia client “look” / green text for items, e.g.

```
You see a carlin sword (Atk:15, Def:13 +1).
It weighs 40.00 oz.
```

### Current behavior

`GET /api/items/Carlin_Sword` (fixture golden `regression/goldens/item-carlin-sword.json`) already returns the ingredients:

| Field | Fixture value |
| --- | --- |
| `article` | `a` |
| `actualname` | `carlin sword` |
| `attack` | `15` |
| `defense` | `13` |
| `weight` | `40.00` |
| `itemid` | `["3283"]` |

There is **no** `lookText` / `greentext` field. `flavortext` on `TibiaObject` is the extra italic line from the infobox, **not** the full look string. The Infobox Object template has no look-text parameter; TibiaWiki builds it in Lua from the same fields.

Owner reply (2021): construct it from item fields; could also add it explicitly.

Note: the issue example has `Def:13 +1`. The current fixture has `defense = 13` and no `defensemod`. The `+1` is only present when the wiki has `defensemod`.

### Feasibility

**Easy** for a documented, best-effort constructor covering common pickupable types (name/article, Atk/Def/Arm/Range/Vol, flavortext, weight).

**Hard** if the contract is “byte-identical to the CipSoft client for every object class” (wield requirements, resistances, charges, duration, imbuement slots, classification, containers, decaying items, corpses, …). Wiki Lua and the client both have long special cases.

### Approach in current architecture

1. Add a pure `LookTextBuilder` (Kotlin) that reads an item/object `JSONObject`.
2. Call it from `JsonFactory.enhanceJsonObject` when `templateType` is `Object` (items and objects share Infobox Object after the 2021 wiki merge).
3. Emit a response-only field, e.g. `lookText`. **Do not** add it to `TibiaObject.fieldOrder()` — PUT would write an unknown infobox key back to Fandom. Skip it in `convertJsonToInfoboxPartOfArticle` the same way as `templateType`.
4. Unit-test from existing `JsonFactory` / items IT fixtures (Carlin Sword is already there). Refresh `regression/goldens/item-carlin-sword.json` and any `?expand=true` item/object goldens.
5. No new MediaWiki calls. `ArticleRepository` and fixtures stay unchanged.

Optional later: corpses (`Corpse.flavortext` + decay/weight stages) if anyone asks.

### Effort

- Best-effort weapons/armor/weight/flavor: **4–8 hours**
- Broad object-class coverage with golden cases: **1–2 days**
- Client-parity: **not worth it** in this repo

### Dependencies / risks

- Look format drifts with Tibia updates; the API would own a second copy of wiki Lua rules.
- Document the field as reconstructed, not scraped from the client.
- PUT must ignore the field.
- `?expand=true` would add the string to every object (cheap CPU, larger JSON).

### Recommendation

**Backlog.** Label `enhancement` + `nicetohave`. Do not close — the ask is still valid and cheap if scoped.

Prefer a `lookText` convenience field over telling clients to concatenate forever, but only after `#75` if you pick one JSON-shape change.

### Suggested labels

`enhancement`, `nicetohave`

---

## #75 — Feature: image of item, creature etc.

- **Opened:** 2020-06-17 by [@biaggio12](https://github.com/biaggio12)
- **URL:** https://github.com/benjaminkomen/TibiaWikiApi/issues/75
- **Labels today:** `enhancement`

### Restated ask

Add a **direct image URL** on creature/item (and similar) responses. Requester confirmed they want the CDN file URL, not only `https://tibia.fandom.com/wiki/File:Dragon.gif`.

Owner already showed that MediaWiki `prop=imageinfo` works, e.g. `File:Dragon.gif` → a `vignette.wikia.nocookie.net/tibia/images/…` URL (2020). Fandom has since moved many assets to `static.wikia.nocookie.net`; the **API** is still the stable way to get the current URL.

### Current behavior

- Creatures, items, NPCs, mounts, spells, … have **no** image field on the model or in GET JSON.
- `Building.image` and `HuntingPlace.image` are **wiki filenames / wikitext**, not resolvable URLs (`"Hero"`, `"[[File:Theater Avenue 8b.png]]"`).
- `ArticleRepository` cannot call imageinfo. Fixtures store wikitext only.

Convention (usually true, not always): `File:{PAGENAME}.gif` for creatures and most items.

### Feasibility

**Easy** — add derived wiki links with no extra Fandom call:

- `imageFile`: `File:Dragon.gif`
- `imageWikiUrl`: `https://tibia.fandom.com/wiki/File:Dragon.gif`
- `imageRedirectUrl`: `https://tibia.fandom.com/wiki/Special:FilePath/Dragon.gif` (302 to the current file)

**Medium** — resolve a real CDN `imageUrl` via imageinfo on **single-resource GET** only.

**Hard / not recommended** — resolve CDN URLs on every `?expand=true` list. That is one extra batched MediaWiki query per ~50 titles on top of an already heavy category dump, against a host this API must not hammer from CI or many workers.

jwiki 1.11.0 already has `Wiki.getImageInfo(title)` and `MQuery.getImageInfo(wiki, titles)` (`ImageInfo.url`). The library was **archived November 2025**; using the existing methods is fine, forking jwiki for new query shapes is not.

### Approach in current architecture

**Slice A (recommended first):**

1. Small helper: page name → default `File:{name}.gif`, with overrides from Building/Hunt `image` when present.
2. Attach `imageFile` / `imageWikiUrl` (and optionally `Special:FilePath`) in the Retrieve layer or `JsonFactory` (no I/O).
3. Skip those keys on wiki write.
4. Update goldens for every resource that gains fields.

**Slice B (direct CDN URL):**

1. Extend `ArticleRepository` with `getImageInfo(fileTitles: List<String>): Map<String, ImageInfo>`.
2. Implement in `JwikiArticleRepository` via `MQuery.getImageInfo`.
3. Implement in `FixtureArticleRepository` from a new `regression/fixtures/images.json` (never call Fandom in CI).
4. Enrich **only** `getXJSON(pageName)` (detail GET), not `getArticlesFromInfoboxTemplateAsJSON`.
5. Swagger: document that `imageUrl` is the current file revision and may change.

Do not put imageinfo inside `JsonFactory` (it is a pure string→JSON converter).

### Effort

- Slice A: **3–6 hours**
- Slice B (detail GET + fixtures + goldens + tests): **1–2 days**
- Slice B on expand lists + filename exceptions (png maps, multi-sprite items, outfits): **several days**, plus rate-limit risk

### Dependencies / risks

- **URL stability:** CDN host, `/revision/latest?cb=…`, and `path-prefix` have already changed since 2020. Persist imageinfo results only as a live lookup.
- **Filename exceptions:** not every page is `{PAGENAME}.gif`. Outfits, buildings, maps, and some objects differ.
- **Fandom rate limits:** imageinfo on expand is the main danger. Same rule as `regression/README.md`: no live capture from CI or many agents.
- **Fixtures:** CI cannot resolve real URLs; ship static imageinfo fixtures if Slice B lands.
- **CORS / hotlinking:** Fandom CDN is generally usable as `<img src>`, but that is their policy, not ours.
- **jwiki archived:** prefer stock `getImageInfo`; avoid new jwiki surface.

### Recommendation

**Backlog.** Highest-value open issue. When implementing, do Slice A (or A + detail-only Slice B). Do not resolve CDN URLs on expand lists unless `#15` caching is revisited.

Keep the existing `enhancement` label; add `nicetohave` if you want it off the implied “must do” list.

### Suggested labels

`enhancement`, `nicetohave` (optional)

---

## #57 — searchable?

- **Opened:** 2020-04-15 by [@gpedro](https://github.com/gpedro)
- **URL:** https://github.com/benjaminkomen/TibiaWikiApi/issues/57
- **Labels today:** none

### Restated ask

Thread, in order:

1. Substring search (e.g. items whose name contains `"sword"`).
2. Clarification: OT-server tooling; today they dump [TibiaWikiSQL](https://galarzaa90.github.io/tibiawiki-sql/) to JSON and search **itemid (client id) and name**.
3. Final ask: **search by item id and item name across all game elements** (items, creatures, stones, walls, …).

Context: [opentibiabr/otservbr-global](https://github.com/opentibiabr/otservbr-global) and a client-id finder for raw packet bytes.

### Current behavior

- No query parameter except `expand`.
- `GET /api/items/{name}` / `GET /api/objects/{name}` are exact page-title lookups.
- `GET /api/items` (no expand) already returns every pickupable **name** (category `Pickupable Objects`). A client can filter `"sword"` locally with one category-members call.
- `GET /api/items?expand=true` already returns every pickupable infobox including `itemid`. Same for `/api/objects` over category `Objects` (the post-2021 merged item/object world).
- Creatures use `race_id`, not `itemid`. Effects use `effectid`. Stones/walls are objects.
- jwiki has `Wiki.search(query, limit, namespaces)` (MediaWiki title/text search). It does **not** search infobox `itemid`.
- There is no in-process index. `#15` caching is closed `not_planned`.

### Feasibility

| Slice | Feasibility |
| --- | --- |
| `?q=` / `?nameContains=` on existing list endpoints (filter category member names) | **Easy** |
| MediaWiki `Wiki.search` wrapper (`GET /api/search?q=`) | **Easy–Medium** (title/text, not itemid) |
| `GET /api/items?itemid=3283` by expanding all items every request | **Medium** to code, **not recommended** to run live |
| Cross-entity itemid index (items + objects + creatures + effects) | **Hard** without a cache/index; **not recommended** as a live Fandom proxy |

The OT use case is a **local dump + index**. This API is a **per-request wiki proxy**. Those are different products. TibiaWikiSQL (or `?expand=true` once, then filter) already matches the described workflow.

### Approach in current architecture

**If you add anything, add only name filtering:**

1. Optional `q` on `WikiResourceResponses.list` / each list controller.
2. Case-insensitive substring on category member names. No extra article fetches when `expand` is absent.
3. When `expand=true` **and** `q` is set, filter names first, then fetch only those articles (strictly better than expand-all).

**Itemid lookup, if ever:**

- Requires fetching and parsing every Object infobox (or a cache `#15` declined).
- Do not do this on Cloud Run per request. Objects is a large category.
- Honest alternative: document `GET /api/objects?expand=true` and client-side filter; or point at tibiawiki-sql.

**Do not** add Elasticsearch / SQLite / Caffeine as a side effect of this issue while `#15` is closed.

### Effort

- Name `?q=` on one resource (items) + tests + goldens: **2–4 hours**
- Same `q` on all list controllers via `WikiResourceResponses`: **0.5–1 day**
- Live itemid / cross-entity search: **days**, plus operational risk; skip

### Dependencies / risks

- **Fandom rate limits:** expand-all objects on every search is the failure mode.
- **API design:** `{name}` path vs `?q=` vs `?itemid=` vs a new `/api/search`. Cross-entity results need a `type` discriminator (`item`, `object`, `creature`, …).
- **ID spaces:** `itemid` ≠ `race_id` ≠ `effectid`.
- **Stale requester:** 2020 OT tooling; they may already have a better dump pipeline.
- **Scope creep:** “all elements” without a type filter is an unbounded product.

### Recommendation

**Needs clarification** before a large design; **Backlog** only for name `?q=`.

Suggested close path if you do **not** want a search product: close with “out of scope for a live infobox proxy; filter `/api/items` or `/api/objects?expand=true`, or use tibiawiki-sql.” That is reasonable but **not** “obsolete” — only a product boundary. Prefer a comment asking whether name-only `?q=` is still useful, rather than closing silently.

Do **not** implement an itemid search engine in this service.

### Suggested labels

`enhancement`, `question` (until the slice is chosen)

---

## #29 — What about transcripts?

- **Opened:** 2019-06-12 by [@joseluis2g](https://github.com/joseluis2g)
- **URL:** https://github.com/benjaminkomen/TibiaWikiApi/issues/29
- **Labels today:** `enhancement`

### Restated ask

NPC responses should include **dialogue transcripts**. Owner (2019): not planned; they live on **subpages**; possible later.

### Current behavior

- `NPC` has jobs, location, buy/sell, sounds — **no** transcript field.
- `Quest.transcripts` is a `YesNo` **flag** (“does a transcript exist?”), not the dialogue.
- `ArticleFactory` / `GET /api/npcs/Sam` only see `{{Infobox NPC}}`. Fixture `Sam.wiki` has no transcript body.
- Transcripts live on pages such as `Sam/Transcripts` (and sometimes further subpages). They are wikitext (`==Topics==`, `{{Transcript}}` / dialogue templates), not an infobox.
- `GET /api/pages/Sam%2FTranscripts` still goes through infobox extraction and would 404 on empty parse. Unencoded `/` does not match `/{title}`.

### Feasibility

**Medium** — new endpoint that returns raw (or lightly cleaned) wikitext for `{name}/Transcripts`.

**Hard** — structured JSON (`topics[]` → `npc`/`player` lines) that stays correct across template variants and quest-specific subpages.

### Approach in current architecture

1. `GET /api/npcs/{name}/transcripts` on `NPCsController` (more than one path segment, so it will not collide with `/{name}`).
2. `RetrieveNPCs.getTranscript(pageName)` → `articleRepository.getArticle("$pageName/Transcripts")`.
3. **Do not** run `ArticleFactory.extractInfoboxPartOfArticle` on that page.
4. v1 response: `{ "page": "Sam/Transcripts", "wikitext": "…" }` or 404 if missing.
5. Optional v1.1: split `==Heading==` sections; leave template bodies intact.
6. Fixtures: add `Sam/Transcripts.wiki` (filename with a slash is awkward on some filesystems — use `Sam_Transcripts.wiki` plus a title map, or a nested fixtures convention).
7. Quests: same idea later (`{Quest}/Spoiler`, `{Quest}/Transcripts`) if anyone asks; the infobox flag already tells you whether to bother.

`ArticleRepository.getArticle` already supports arbitrary titles; only the parse/HTTP layers are missing.

### Effort

- Raw wikitext endpoint + fixture + golden + 404 case: **0.5–1 day**
- Heading split only: **+ a few hours**
- Faithful dialogue AST: **multiple days**, high ongoing wiki-drift cost

### Dependencies / risks

- **Wiki drift:** transcript templates are not a stable schema.
- **Size:** some NPCs have very large transcript pages; Cloud Run response size is fine, expand-all-NPCs-with-transcripts is not.
- **Multiple subpages:** one NPC can have several transcript pages.
- **License / attribution:** same Fandom content as the rest of the API; no new legal surface beyond usual wiki reuse.
- **Fixtures / CI:** must ship transcript wikitext; never scrape Fandom from the regression job.

### Recommendation

**Backlog.** Keep `enhancement`. Do not close. Implement only if a consumer appears; start with raw wikitext, not a dialogue parser.

### Suggested labels

`enhancement`, `nicetohave`

---

## Other open issues / PRs

- Open issues: **only the four above**.
- Open PRs at triage: **none** (many Kotlin-rewrite PRs merged the same day as this note).
- No extra open feature requests.

### Closed-but-still-relevant (optional)

| Issue | Why it matters |
| --- | --- |
| [#15 Add caching](https://github.com/benjaminkomen/TibiaWikiApi/issues/15) (closed `not_planned`, 2024) | Prerequisite for itemid search and imageinfo-on-expand. Comments mentioned JCache/Ehcache and later Caffeine. Revisit **only** if those features become must-haves. |
| [#4 Support every infobox flavor](https://github.com/benjaminkomen/TibiaWikiApi/issues/4) (closed, done) | Green text / images are **not** missing infobox types; they are computed or File-namespace data. |
| [#91 Rewrite to Kotlin](https://github.com/benjaminkomen/TibiaWikiApi/issues/91) (closed) | Landed on `master` (2026). Does not change feasibility of the four issues. |
| [#167 Add Difficulty and Creature Charms](https://github.com/benjaminkomen/TibiaWikiApi/issues/167) (closed) | Example of the preferred pattern: add infobox fields the wiki already has. |

### Suggested comments (not posted)

Issue comments were **not** posted, so you can edit tone before speaking as the maintainer on 2019–2021 threads. Suggested text:

**#210**

> 2026 triage (current Kotlin / Boot 4.1 / Java 25 tree): still valid. The look/green text is not an infobox field; GET already returns the parts (`actualname`, `attack`, `defense`, `weight`, `flavortext`, …). A response-only `lookText` built in `JsonFactory` is Easy if we document it as best-effort. Full client-parity is not worth it. Planning to leave this open as `nicetohave`.

**#75**

> 2026 triage: still the highest-value open request. jwiki 1.11.0 can call `MQuery.getImageInfo`. I would add wiki/Special:FilePath links with no extra Fandom call, and optionally a CDN `imageUrl` on **detail** GET only — not on `?expand=true` (rate limits; `#15` caching stays closed). The 2020 vignette URL shape is already stale; imageinfo remains the stable lookup.

**#57**

> 2026 triage: need to pick a slice. Name substring `?q=` on list endpoints is Easy (we already have category member names). Search-by-`itemid` across all objects is a dump/index job; this service is a live Fandom proxy and we closed caching (`#15`) as not planned. For OT client-id work, `GET /api/objects?expand=true` or [tibiawiki-sql](https://galarzaa90.github.io/tibiawiki-sql/) is the better fit. If name-only `?q=` is still useful, say so; otherwise I am inclined to keep this parked.

**#29**

> 2026 triage: still possible, still not planned as a parser. Transcripts are `{NPC}/Transcripts` wikitext, not the Infobox. A raw `GET /api/npcs/{name}/transcripts` is Medium; structured dialogue JSON is Hard because templates drift. Leaving open as `nicetohave` until there is a concrete consumer.

---

## Out of scope for this note

- Implementing any of the four features
- Closing issues
- Posting the comments above
- Calling live Fandom or tibiawiki.dev
