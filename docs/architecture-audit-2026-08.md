# TibiaWikiApi architecture audit (August 2026)

Audit of [benjaminkomen/TibiaWikiApi](https://github.com/benjaminkomen/TibiaWikiApi) at version **2.0.0** on current `master` (`f780606`, after stacked rewrite PRs **#376–#385**, Sonar follow-up **#388**, and the Boot 4.1.1 / Java 25 / Kotlin 2.4.10 line).

**Correction:** an earlier draft of this document was written against a stale checkout from before those PRs and incorrectly described a Java-majority tree (`controller.*Resource`, “~99 Java vs ~12 Kotlin”). That is obsolete. This rewrite is based on `git fetch origin master` at `f780606`.

Read-only: no production, build, or CI files were changed for this report. The `buid.yml` filename typo is left alone.

---

## Executive summary

- **The Java→Kotlin rewrite is complete for application and test source.** `src/main` is **112 Kotlin files and 0 Java files**. Unit tests are **33 Kotlin / 0 Java**; integration tests are **9 Kotlin / 0 Java**. Controllers live under `com.tibiawiki.adapters.rest`; process services and domain models are Kotlin. Lombok is gone from `build.gradle`.
- The **next theme is post-rewrite consolidation**, not “finish the migration.” The mechanical port preserved Java shapes: `java.util.Optional` / `Stream`, Vavr `Try`, one class per wiki category, `PropertiesUtil` classpath reads, and a dual `org.json` + Jackson pipeline.
- **Layering is visible but leaky:** `adapters.rest` → `process` / `domain.RetrieveWikiPages` → `domain.factories` → `domain.repositories` → jwiki or fixtures. The domain package still depends on `io.github.fastily.jwiki.core.NS`, Spring stereotypes, Jackson, and `org.json`.
- **Reads and writes use different models.** GET returns loosely typed `org.json.JSONObject` maps (or pretty-printed JSON strings). The typed `WikiObject` hierarchy is used on the PUT path (`ModifyAny` → `WikiObjectFactory.createJSONObject`). `WikiObjectFactory.createWikiObject` is unused in production.
- **Fandom is a live, uncached dependency** on the default profile. `JwikiArticleRepository` talks to `https://tibia.fandom.com/api.php` and constructs `Wiki` (and optionally logs in) in its constructor. `?expand=true` bulk-fetches a whole category. There is no cache, timeout, circuit breaker, rate limit, or actuator health check.
- **Write endpoints are public.** Nearly every resource exposes `PUT /api/{type}` with only an `X-WIKI-Edit-Summary` header. OpenAPI documents HTTP 401; no controller returns it. If `credentials.properties` is present, `JwikiArticleRepository.login` authenticates a bot and `wiki.edit` mutates TibiaWiki. CORS allows credentialed PUT/DELETE from any origin.
- **`Retrieve*` / controller duplication survived the rewrite.** `WikiResourceResponses` already shares HTTP mapping, but ~20 `Retrieve*` classes and ~20 `*Controller` classes are still copy-paste around `InfoboxTemplate`. That is now the main maintainability cost.
- **The test strategy is a strength**, and #388 improved Sonar/Jacoco wiring. Gradle unit tests cover every `Retrieve*`; `RemainingWikiControllersTest` hits the cloned adapters; `integrationTest` uses `@MockitoBean ArticleRepository`; Bun fixture regression (`api-regression.yml` + `FixtureArticleRepository`) keeps CI off live Fandom.
- **Highest-leverage follow-ups:** generic `Retrieve*` + one category controller; treat Fandom as unreliable (cache + timeouts); lock down or disable unauthenticated PUT; one JSON pipeline for reads; replace Java leftover APIs (`Optional`/`Stream`/Vavr) with Kotlin types.

---

## Current architecture snapshot

### What the rewrite actually landed

Stacked PRs on `master`:

| PR | What moved to Kotlin |
| --- | --- |
| #376 | Domain enums, interfaces, validation |
| #380 | `WikiObject` models |
| #381 | Factories, Jackson mixin, domain utils |
| #382 | Article repositories |
| #383 | Process / services |
| #384 | Remaining REST controllers |
| #385 | Remaining tests |
| #388 | Sonar coverage/duplication (Jacoco `executionData`, `sonar.tests` includes `integrationTest`) |
| #377 / #387 | Kotlin **2.4.10**, `-Xannotation-default-target=param-property` |

Layout today: production under `src/main/kotlin`, tests under `src/test/kotlin` and `src/integrationTest/kotlin`. The Gradle `java` plugin remains (toolchain + Jacoco), but there is no `src/main/java`.

`AGENTS.md` still says Kotlin 2.3.21; `build.gradle` pins **2.4.10**.

### Runtime and packaging

| Piece | Pin / location |
| --- | --- |
| Version | `2.0.0` (`build.gradle`) |
| JDK | Toolchain **25**; Docker runtime `eclipse-temurin:25.0.4_7-jre` |
| Kotlin | **2.4.10** (`jvmTarget` 25, `-Xjsr305=strict`, `-Xannotation-default-target=param-property`) |
| Spring Boot | **4.1.1** (`spring-boot-starter-webmvc`, `spring-boot-starter-jackson`) |
| Gradle | Wrapper **9.7.1** |
| Wiki client | `io.github.fastily:jwiki:1.11.0` (Maven Central). The `benjaminkomen:jwiki:2.2.0` GitHub Packages line is commented out. |
| JSON | Jackson **3.1.5** (`tools.jackson` + `jackson-module-kotlin`) **and** `org.json:json:20260814` |
| OpenAPI | `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0`; docs at `/api-docs` |
| Other | Vavr 1.0.1, Guava 33.7.1 (`Strings.padEnd` in `JsonFactory`), slf4j 2.0.18, logstash-logback-encoder 9.0, `spring-cloud-gcp-starter-logging` 8.1.0. **No Lombok.** |
| Style | ktlint 14.2.0, `intellij_idea` with many rules disabled (`.editorconfig`) |
| Coverage | Jacoco 0.8.15; `executionData` includes `build/jacoco/*.exec` (unit + IT); SonarCloud `com.tibiawiki:TibiaWikiApi` |

Default Boot profile constructs `JwikiArticleRepository` (`@Profile("!fixtures")`) and calls Fandom. Profile `fixtures` (`application-fixtures.properties`) binds `FixtureArticleRepository` to `wiki.fixtures.path` / `WIKI_FIXTURES_PATH` (default `regression/fixtures`).

### Package map

```
HTTP
  com.tibiawiki.adapters.rest
    *Controller (Achievements … Streets, Loot v1/v2, WikiPage)
    WikiResourceResponses          shared list / detail / modify HTTP mapping

process / application
  com.tibiawiki.process            RetrieveAny + Retrieve* + ModifyAny + RetrieveLoot
  com.tibiawiki.domain             RetrieveWikiPages  ← application service living in "domain"

domain model
  com.tibiawiki.domain.objects     WikiObject + data-class subtypes
  com.tibiawiki.domain.enums       InfoboxTemplate, YesNo, …
  com.tibiawiki.domain.factories   ArticleFactory, JsonFactory, WikiObjectFactory
  com.tibiawiki.domain.repositories  ArticleRepository, JwikiArticleRepository, FixtureArticleRepository
  com.tibiawiki.domain.utils       TemplateUtils, PropertiesUtil, ListUtil
  com.tibiawiki.domain.jackson     WikiObjectMixin

config
  com.tibiawiki.config             JacksonConfiguration, OpenAPIConfiguration, CORSResponseFilter
  com.tibiawiki                    TibiaWikiApiApplication
```

This is a **ports-and-adapters sketch**, not a closed hexagon:

- `ArticleRepository` is the port, but it leaks `io.github.fastily.jwiki.core.NS` on `getPageNamesFromCategory(String, NS)`.
- Factories live under `domain` but are Spring `@Component`s and speak `org.json` / Jackson.
- Domain **objects** are no longer Spring beans (the Java `@Component` on `Creature` et al. is gone). `spring.main.allow-bean-definition-overriding=true` is still set and now looks unused.
- `RetrieveAny` is `abstract` **and** `@Component`.
- `RetrieveWikiPages` is a `@Service` in `domain`; siblings are `@Component` in `process`.
- Integration tests still use package `com.tibiawiki.controller` (`CreaturesResourceIT`, …) after production moved to `adapters.rest`.

### Read path

1. `*Controller` accepts `expand: Boolean?` and optional `{name}`.
2. `WikiResourceResponses.list` / `jsonOrNotFound` maps the result.
3. `Retrieve*` asks `ArticleRepository` for category members and/or page wikitext.
4. Category lists drop pages that are also in category `Lists` (`RetrieveAny.CATEGORY_LISTS`).
5. `ArticleFactory.extractInfoboxPartOfArticle` pulls the `{{Infobox …}}` block via `TemplateUtils` balanced-bracket scan.
6. `JsonFactory.convertInfoboxPartOfArticleToJson` splits `| key = value` lines, then “enhances” known fields (`sounds`, `loot`, `droppedby`, `itemid`, hunting-place `lowerlevels`, …) into `JSONArray`s.
7. Collection + `expand=true` returns `Stream<JSONObject>` mapped through `JSONObject.toMap()`. Detail GET returns `JSONObject.toString(2)` as `ResponseEntity<String>`.

`WikiObjectFactory.createWikiObject` / `createWikiObjects` are **not** on this path (only the factory itself defines them). GET clients see wiki parameter names (`hp`, `exp`), not Kotlin property names (`hitPoints`, `experiencePoints`). `Creature` uses `@JsonProperty("hp")` / `"exp"` so the write side can round-trip those keys.

Special cases:

- **Items vs objects.** Wiki merged Items into Objects (README footnote, 2021). `InfoboxTemplate.ITEM` uses category `Pickupable Objects`; `OBJECT` uses `Objects`. Both map to `TibiaObject` on PUT. List endpoints stay split for compatibility.
- **Loot.** `RetrieveLoot` uses namespace 112 via reflection on `NS`’s constructor (`makeLootNamespace`). v1 (`LootStatisticsController` → `/api/loot`) extracts `{{Loot2`. v2 (`LootStatisticsV2Controller` → `/api/v2/loot`) extracts both `Loot2` and `Loot2_RC`.
- **Hunting places.** `HuntingPlacesController` uses `@GetMapping("/**")` and splits the URI after `/huntingplaces/` so names may contain slashes.
- **Generic page.** `WikiPageController` (`/api/pages/{title}`) reimplements `RetrieveAny.getArticleAsJSON` in `RetrieveWikiPages` (nullable `JSONObject?` instead of `Optional`).

### Write path

`ModifyAny.modify(wikiObject, editSummary)`:

1. `articleRepository.getArticle(wikiObject.name ?: "")`
2. `wikiObject.validate()` — `WikiObject.validate()` returns `emptyList()`; `Achievement.validate()` concatenates that with another empty list. No other subtype adds rules.
3. `WikiObjectFactory.createJSONObject` (`ObjectMapper.convertValue` → `JSONObject` + `templateType`)
4. `JsonFactory.convertJsonToInfoboxPartOfArticle` using `wikiObject.fieldOrder()`
5. `ArticleFactory.insertInfoboxPartOfArticle` (replace the Infobox block)
6. `articleRepository.modifyArticle`

`WikiResourceResponses.modify` wraps the Vavr `Try` with `ValidationException → 400` and `→ 500`. HTTP 401 is documented on every PUT and never produced. `JwikiArticleRepository.modifyArticle` logs **full page content** at INFO. `isDebugEnabled` (default false) short-circuits to `true` without calling the wiki.

Credentials come from classpath `credentials.properties` via `PropertiesUtil` (not Spring `Environment`). `.gitignore` excludes `*.properties` except a few application files; `credentials.properties.example` is the template. Test classpath ships `src/test/resources/credentials.properties` (`username=Foo`, `password=Bar`).

### MediaWiki / jwiki / Fandom coupling

| Concern | Evidence |
| --- | --- |
| Hardcoded API | `JwikiArticleRepository.DEFAULT_WIKI_URI = "https://tibia.fandom.com/api.php"` |
| Client | `Wiki.Builder().withApiEndpoint(DEFAULT_WIKI_URI.toHttpUrlOrNull()).build()` in the no-arg constructor; `MQuery.getPageText` for bulk |
| Login | Constructor calls `login(wiki)` when username/password are non-null |
| Namespace leak | `ArticleRepository` method takes `NS`; `RetrieveLoot` reflects a custom namespace |
| Fork leftover | Commented GitHub Packages repo + `benjaminkomen:jwiki:2.2.0`; `.sample.settings.xml` still documents that server; Cloud Build still injects `GITHUB_TOKEN` into `docker build` |
| Stale product copy | README and `OpenAPIConfiguration` still say `tibia.wikia.com` |
| Offline substitute | `FixtureArticleRepository` (`@Profile("fixtures")`) reads `categories.json` + `articles/*.wiki`. `getPageNamesUsingTemplate` is empty; `modifyArticle` always returns `false`. |

`InfoboxTemplate` enumerates wiki templates/categories the API understands, including types **without** REST resources (`Cipsoft_Member`, `Fansite`, `Update`, `World`).

### Spring, Jackson, OpenAPI

- `TibiaWikiApiApplication` is a standard `runApplication` entry.
- `application.properties`: `springdoc.api-docs.path=/api-docs`, `spring.main.allow-bean-definition-overriding=true`, `spring.jackson.use-jackson2-defaults=true` (Boot 4 / Jackson 3 compatibility).
- `JacksonConfiguration` registers `KotlinModule`, `NON_NULL` inclusion, disables fail-on-unknown/ignored, `ECT` default timezone (deprecated ID), and the **correct** mixin order:

  ```kotlin
  .addMixIn(WikiObject::class.java, WikiObjectMixin::class.java)
  ```

  `WikiObjectMixin` declares `@JsonTypeInfo(property = "templateType")` and `@JsonSubTypes` but **omits `Charm` and `Missile`**. GET does not use this mixin (org.json path). PUT binds concrete types, so the mixin is mostly unused.
- Domain annotations still import `com.fasterxml.jackson.annotation` (`@JsonIgnore`, `@JsonValue`, `@JsonProperty`, mixin). Jackson 3 implementation types are `tools.jackson.*`.
- `OpenAPIConfiguration` sets title, MIT license, contact, and version from `BuildProperties`. Description still cites `tibia.wikia.com`. Per-method `@Operation` / `@ApiResponses` are copy-pasted; 401 is aspirational.
- `CORSResponseFilter`: `allowCredentials = true`, `allowedOriginPatterns = ["*"]`, methods GET/POST/PUT/DELETE, headers `X-Requested-With` and `Content-Type` only — **not** `X-WIKI-Edit-Summary`. Browser PUT preflight with that header fails; curl/Postman do not care.

There is **no** `@ControllerAdvice`, no Bean Validation (`@Valid`), no Spring Security, no Cache abstraction, no Actuator.

### Testing strategy

| Layer | What it is | Fandom? |
| --- | --- | --- |
| `src/test/kotlin` | Mockito unit tests for **every** `Retrieve*`, factories, `TemplateUtils`, both repositories, `ModifyAny`, `WikiResourceResponses`, `RemainingWikiControllersTest` (direct controller construction), `JacksonConfigurationTest` | No (mocked `Wiki` / `ArticleRepository`) |
| `src/integrationTest/kotlin` | `@SpringBootTest` + `@MockitoBean ArticleRepository` + `TestRestTemplate` for Creatures, Books, Items, HuntingPlaces, Loot v1/v2, Achievements, plus `JsonFactoryIT` (infobox round-trip) | Default profile, repository bean replaced. Not the fixtures profile. |
| `regression/` | Bun `capture` / `test` against HTTP goldens. CI: `api-regression.yml` boots `--spring.profiles.active=fixtures` | **No outbound wiki HTTP** |

Notes after #385 / #388:

- Controller clones that are not in `integrationTest` are still exercised by `RemainingWikiControllersTest` (in-process, not HTTP).
- `sonar.tests` is now `src/test,src/integrationTest`. Jacoco XML includes both `*.exec` files — the previous “IT coverage invisible” gap is closed.
- `regression/endpoints.json` covers README list/detail (and many `expand=true` cases) plus one 404. It does **not** include `/api/pages/{title}` or `/api/v2/loot`. Large production expands (creatures, items, objects, books, loot, NPCs) are list-only, by design.
- Regression README CI blurb still says “JDK 17 + Bun”; the workflow uses JDK 25.
- ITs do not activate the `fixtures` profile, so Gradle never boots the same repository CI regression uses.
- ktlint is the only style gate.

### Deploy / Gradle / Sonar

- **CI build:** `.github/workflows/buid.yml` — JDK 25 Temurin, `./gradlew ktlintCheck jacocoTestReport`, then `./gradlew sonar`. Filename typo left as-is.
- **CI regression:** `.github/workflows/api-regression.yml` — boot fixtures, wait on `/api/corpses`, `bun run test`.
- **Dependabot:** daily Gradle, GitHub Actions, and `/docker`.
- **Docker:** `gradle:9.7.1-jdk25` builder copies `build.gradle` + `src` only (no wrapper, no `settings.gradle` — project name becomes directory `app`, artifact `app-2.0.0.jar`). Still accepts `GITHUB_TOKEN` though GitHub Packages is unused. Runtime: `java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT} -jar …` (egd is a Java 8-era leftover).
- **Cloud Build:** `cloudbuild.yaml` builds, pushes `gcr.io/tibiawikiapi-246008/tibiawikiapi`, deploys Cloud Run `tibiawikiapi` in `europe-west1`, `--memory 1Gi`, `LOGGING_JSON=true`, traffic `--to-latest`. `cloudbuild-pr.yaml` builds only. Both still KMS-unwrap a `GITHUB_TOKEN` into the image build.
- **Logging:** `logback.xml` — console locally; if `LOGGING_JSON` is set, GCP `logback-json-appender`. Large commented logstash/Stackdriver blocks remain.

### Duplication pattern (still the dominant smell)

`WikiResourceResponses` already extracts the three HTTP shapes (`list` / `jsonOrNotFound` / `modify`). What remains is:

- **~20 controllers** that differ only in route, `@Tag`, injected `Retrieve*`, and the Kotlin property names they pass (`retrieveCreatures.creaturesJSON` vs `retrieveBooks.booksJSON`).
- **~19 `Retrieve*` classes** that are the same 30 lines with a different `InfoboxTemplate` and property names. Example (`RetrieveCreatures` / `RetrieveBooks` / `RetrieveCharms`):

  ```text
  category = repo.getPageNamesFromCategory(InfoboxTemplate.X.categoryName)
  lists    = repo.getPageNamesFromCategory("Lists")
  return category.filter { it !in lists }
  ```

  plus a `Stream<JSONObject>` getter and an `Optional<JSONObject>` by-name method.

`HuntingPlacesController` is the only structural HTTP variant (greedy path). `RetrieveLoot` is the only semantic process variant (different templates + namespace). `RemainingWikiControllersTest` is itself a clone-of-clones, which is a hint the production types should collapse.

---

## Strengths

1. **The rewrite finished.** One language, one source root, no Lombok, Kotlin data classes for wiki types, Spring constructor injection without `@Autowired` on most types. That is a real consolidation of the 2.0.0 line (Boot 4.1.1 / Java 25 / Kotlin 2.4).
2. **`WikiResourceResponses` is the right first cut** at de-duplicating HTTP mapping without losing per-controller OpenAPI annotations. Further generics should extend this, not revert it.
3. **`ArticleRepository` + `@Profile` is a real port.** Fixtures vs jwiki never coexist. `FixtureArticleRepository` plus committed goldens and `api-regression.yml` keep CI off Fandom and tibiawiki.dev.
4. **Wiki parsing is concentrated.** `ArticleFactory` + `JsonFactory` + `TemplateUtils` own infobox/loot syntax. `JsonFactoryIT` asserts achievement infobox round-trip stability.
5. **Jackson mixin registration is correct** after the rewrite (`addMixIn(WikiObject, WikiObjectMixin)`). `Creature` `@JsonProperty("hp"|"exp")` shows awareness of the wiki-key contract.
6. **Test surface grew with the rewrite.** Every `Retrieve*` has a unit test; cloned controllers have `RemainingWikiControllersTest`; #388 wired IT coverage into Jacoco/Sonar.
7. **Public contract is documented.** README resource table, `?expand=true`, springdoc UI at tibiawiki.dev, per-controller `@Tag`s. Items/Objects compatibility is an explicit product decision in `InfoboxTemplate`.
8. **Loot v2** (`Loot2` + `Loot2_RC`) versions a breaking parse without deleting v1.
9. **Logging can be JSON on Cloud Run** (`LOGGING_JSON` + GCP appender).

---

## Issues / risks

### High

| ID | Issue | Evidence |
| --- | --- | --- |
| H1 | **Unauthenticated wiki mutation on a public API.** PUT is open on every resource. If a bot `credentials.properties` is packaged or mounted, any client can edit TibiaWiki through tibiawiki.dev. 401 is documented and never returned. Validation is a no-op. | `CreaturesController.putCreature` and siblings; `WikiResourceResponses.modify`; `ModifyAny`; `JwikiArticleRepository.login` / `wiki.edit`; `WikiObject.validate()` empty |
| H2 | **Open Fandom proxy with no cache, quota, or backoff.** Default profile hits `tibia.fandom.com` on every request, including constructor-time `Wiki` build. `?expand=true` on large categories (creatures, items, objects) is a bulk `MQuery.getPageText`. Cloud Run is 1Gi, no concurrency/min-instance/health config. Abuse or a stampede becomes Fandom rate-limits **and** Cloud Run OOM/timeouts. | `JwikiArticleRepository`; `RetrieveAny.getArticlesFromInfoboxTemplateAsJSON`; `cloudbuild.yaml` `--memory 1Gi` |
| H3 | **N-fold clone of HTTP + process types (now in Kotlin).** Adding a field, header, error body, or pagination still means touching ~20 controllers and ~20 services. `WikiResourceResponses` only removed the inner `ResponseEntity` boilerplate. | `adapters.rest/*Controller.kt`, `process/Retrieve*.kt` |

### Medium

| ID | Issue | Evidence |
| --- | --- | --- |
| M1 | **Split JSON stacks and split response types.** List/expand = `List<Map>` via Jackson. Detail = pretty-printed `String` from `org.json`. Clients and OpenAPI schemas cannot describe a single model. `JsonFactory` is still a god parser. | `WikiResourceResponses`; `JsonFactory` |
| M2 | **Typed domain model is write-only and incomplete.** GET never maps to `WikiObject`. `createWikiObject` is dead. Mixin and `WikiObjectFactory` omit Charm and Missile. Kotlin `Charm` only constructs `type`/`cost`/`effect`; parent `name` stays null — PUT charm cannot populate `ModifyAny`’s `wikiObject.name`. | `WikiObjectFactory`; `WikiObjectMixin`; `Charm.kt` |
| M3 | **jwiki / domain leak and reflection.** Port interface imports `NS`. Loot namespace 112 is `isAccessible = true` + `newInstance`. Central `jwiki` 1.11.0; the maintained fork line is commented; Cloud Build still threads `GITHUB_TOKEN` into Docker for that unused repo. | `ArticleRepository`; `RetrieveLoot.makeLootNamespace`; `build.gradle`; `cloudbuild.yaml` |
| M4 | **CORS is both too open and too closed.** Credentialed `*` origins + PUT/DELETE is a CSRF-shaped config for a cookie-less API. The one write header is not allowed, so browser clients cannot PUT even if the API is open to curl. | `CORSResponseFilter` |
| M5 | **Operational surface missing for Cloud Run.** No Actuator/health, no readiness distinct from “process started”, no request timeout to Fandom, no structured error payload. `JwikiArticleRepository` constructor failure is a failed revision, not a degraded read. | no actuator dependency; constructor `Wiki.Builder()` |
| M6 | **Java leftover APIs in Kotlin.** Process layer returns `Optional<JSONObject>` and `Stream<JSONObject>` instead of `JSONObject?` / `Sequence` / `List`. Controllers and tests then stay on Java stream APIs. `ModifyAny` uses Vavr `Try` instead of `Result` / `runCatching`. `TemplateUtils` / `ArticleFactory` still return `java.util.Optional`. | `RetrieveAny`, `WikiResourceResponses`, `ModifyAny` |
| M7 | **INFO log of full wikitext on edit.** `Attempting to publish page {} with new content {}.` ships page bodies to Cloud Logging. | `JwikiArticleRepository.modifyArticle` |
| M8 | **Layering leftovers.** `RetrieveWikiPages` is a `@Service` in `domain`; `RetrieveAny` is `@Component` in `process`. IT package name `com.tibiawiki.controller` no longer matches production. | those types / `src/integrationTest/kotlin/com/tibiawiki/controller` |

### Low

| ID | Issue | Evidence |
| --- | --- | --- |
| L1 | **Stale product URLs and docs.** `tibia.wikia.com` in README + OpenAPI; regression README still says CI uses JDK 17; `AGENTS.md` still says Kotlin 2.3.21. | those files |
| L2 | **Dockerfile / version coupling.** Hardcoded `app-2.0.0.jar`; copies all of `src` (tests included); unused `GITHUB_TOKEN`; `java.security.egd` relic. | `docker/Dockerfile` |
| L3 | **Dead / leftover config.** `.sample.settings.xml` (Maven + GitHub Packages); huge commented `logback.xml` block; `jar { manifest Main-Class: 'com/tibiawiki/TibiaWikiApiApplication' }` uses slashes (Boot plugin jar is what Cloud Run runs). | those files |
| L4 | **Timezone `ECT`.** Deprecated three-letter ID; prefer `Europe/Paris` (or UTC for an API). | `JacksonConfiguration` |
| L5 | **InfoboxTemplate / mixin inventory drift.** Enum has CipSoft / Fansite / Update / World with no API. Mixin and `WikiObjectFactory` miss Charm and Missile. | `InfoboxTemplate`, `WikiObjectMixin`, `WikiObjectFactory` |
| L6 | **Creature numeric fields remain `String?`** (`hitPoints`, `experiencePoints`, …). Changing them is an API break only if GET ever switches to the typed model; today GET emits wiki strings via `JSONObject`. | `Creature.kt` |
| L7 | **Hunting-place path parsing** (`split("/huntingplaces/")`) is brittle if the context path or a proxy prefix changes. | `HuntingPlacesController` |
| L8 | **ktlint rule set is heavily disabled** so new Kotlin will not converge on a modern default. | `.editorconfig` |
| L9 | **`*.properties` gitignore** is broad; easy to forget to force-add a new Spring profile file. | `.gitignore` |
| L10 | **`allow-bean-definition-overriding=true`** looks leftover now that domain objects are not beans. | `application.properties` |

---

## Kotlin idioms still missing (Java-shaped leftovers)

The rewrite was a faithful port. Consolidation means replacing these patterns, not translating more files:

- **`java.util.Optional` and `java.util.stream.Stream`** as the process-layer public API. `RetrieveWikiPages` already uses `JSONObject?`; the `Retrieve*` family should match.
- **Vavr `Try`** in `ModifyAny` / `WikiResourceResponses.modify`. Kotlin `Result` or a small sealed `ModifyResult` plus `@ControllerAdvice` would drop the Vavr dependency if nothing else needs it (`TemplateUtils` still uses Vavr `Tuple2`).
- **One class per `InfoboxTemplate`** instead of `class RetrieveByTemplate(private val template: InfoboxTemplate) : RetrieveAny(…)` or a single service + enum.
- **One controller per type** instead of a generic `@RequestMapping("/api/{category}")` or a small factory of `RouterFunction`s. OpenAPI tags can stay via a data table.
- **`PropertiesUtil` static classpath read** instead of `@ConfigurationProperties` / env vars (`WIKI_USERNAME`).
- **`@Component` on abstract `RetrieveAny`** and on every retrieve service; prefer `@Service` for application types.
- **`fieldOrder(): MutableList<String>` on `Charm`** vs `List<String>` on `Achievement` / `WikiObject`.
- **`@JvmStatic` / `@JvmOverloads`** left over from Java callers that no longer exist (`PropertiesUtil`, `WikiObject`, `RetrieveLoot.makeLootNamespace`, `ValidationException.fromResults`).
- **No sealed hierarchy for template types.** `templateType` is still a string `when` in `JsonFactory` and `WikiObjectFactory`.
- **Hamcrest + Mockito classic** in tests (`doReturn().when()`, `Matchers.\`is\``) rather than MockK / kotest / AssertJ. Fine to keep; just not idiomatic Kotlin.
- **`java.util.Map.entry(...)`** in `ArticleFactory` instead of `pageName to content` + an overload.

---

## Concrete improvement proposals (impact vs effort)

Ranked. Effort is technical scope, not calendar time. Theme: **post-rewrite consolidation**.

### 1. Generic `Retrieve*` + category controller — **high impact, medium effort**

One `RetrieveByTemplate(InfoboxTemplate)` (or a single `@Service` with methods that take the enum). One controller (or a thin generated/registered set) that already goes through `WikiResourceResponses`. Keep `RetrieveLoot` and `HuntingPlacesController` as the only specializations.

Unblocks consistent errors, pagination, caching annotations, and a single OpenAPI schema. Highest maintainability win; also deletes most of `RemainingWikiControllersTest` by construction.

### 2. Treat Fandom as an unreliable dependency — **high impact, medium effort**

- Make wiki base URL / user-agent configurable (drop hardcoded `DEFAULT_WIKI_URI`).
- Timeouts, retries with jitter, and a small in-memory or Cloud Run-friendly cache (category name lists + single-page wikitext) with a short TTL. `?expand=true` should be cached especially hard or rejected above a size.
- Do **not** construct `Wiki` / login in the repository constructor; lazy-init so broken Fandom does not fail process start.
- Optional: a scheduled warmer on Cloud Run min-instances so the public API is not a synchronous Fandom fan-out.

This is the difference between a public API and an unpaid reverse proxy.

### 3. Lock down writes — **high impact, low–medium effort**

Pick one:

- **A (smallest):** drop or profile-gate PUT on the public Cloud Run service (`WIKI_WRITE_ENABLED` + auth token). Keep `ModifyAny` for a future bot.
- **B:** require an API key / OIDC on PUT; map login failure to real 401; stop logging page bodies; add `X-WIKI-Edit-Summary` to CORS only if browser writes are intended.

Until credentials exist in the image, PUT already fails closed at `modifyArticle` — but the contract invites shipping a bot password.

Tighten CORS to GET (and the origins that actually use the UI) and drop `allowCredentials` unless cookies appear.

### 4. One JSON pipeline for reads — **high impact, high effort** (do incrementally)

Keep the **current GET JSON shape** (wiki keys) as the compatibility contract — fixture goldens lock it. Internally:

- Parse infobox → Jackson `JsonNode` / a `Map<String, Any?>` once; retire `org.json` from the HTTP boundary.
- Return the same type for list-expand and detail (drop `toString(2)`). Pretty-print is a logging/debug concern.
- Leave `WikiObject` mapping for PUT, or generate both from one schema.

Do **not** switch GET to Kotlin property names (`hitPoints`) without a `/api/v2` — that would break every golden and client.

### 5. Replace Java leftover APIs in the process layer — **medium impact, low–medium effort**

- `RetrieveAny.getArticleAsJSON` → `JSONObject?`
- `getArticlesFromInfoboxTemplateAsJSON` → `List<JSONObject>` or `Sequence<JSONObject>` (materialize before HTTP; expand already collects)
- `WikiResourceResponses.jsonOrNotFound(json: JSONObject?)`
- `ModifyAny` → `Result<WikiObject>` or a sealed type + `@ControllerAdvice`

This is the natural Kotlin follow-up now that there are no Java callers. Do it **with** proposal 1 so you do not edit 20 files twice.

### 6. Close the port around MediaWiki — **medium impact, medium effort**

- Remove `NS` from `ArticleRepository` (loot namespace as an `int` or in-house `WikiNamespace`).
- Replace `RetrieveLoot` reflection when the jwiki fork supports custom NS, or wrap namespace in the adapter only.
- Decide: stay on Central `jwiki` 1.11.0, or revive `benjaminkomen/jwiki` and delete GitHub Packages / `GITHUB_TOKEN` / `.sample.settings.xml` from Cloud Build.

### 7. Cloud Run / Docker / health — **medium impact, low effort**

- Actuator `health`/`info` (or `/api/health` that does not call Fandom) and Cloud Run startup/liveness probes.
- Copy `settings.gradle` + wrapper (or keep the `app` name but stop hardcoding `2.0.0` in the Dockerfile).
- Drop `GITHUB_TOKEN` from `docker build` / Cloud Build secrets if Packages stays unused.
- Revisit 1Gi and concurrency after adding expand caches.

### 8. Testing hygiene (remaining) — **medium impact, low effort**

- Add `/api/pages/{title}` and `/api/v2/loot` to `regression/endpoints.json` (fixtures already have `Loot_Statistics:Ferumbras`).
- Run a slice of ITs with `spring.profiles.active=fixtures` so the Boot profile used in regression is also a Gradle test.
- Move `src/integrationTest/.../controller` to `adapters.rest` to match production.
- Fix `regression/README.md` “JDK 17” and `AGENTS.md` Kotlin 2.3.21.

### 9. Validation, errors, Charm/Missile completeness — **low–medium impact, low effort**

- `@ControllerAdvice` mapping `ValidationException` → 400 with `message` / `validationResults`.
- Map missing article → 404 once, not in every controller.
- If PUT stays: real `Validatable` rules (required `name`, known `templateType`).
- Give `Charm` a `name` (and the parent fields it lists in `fieldOrder`) or stop advertising PUT charms; add Charm/Missile to mixin + `WikiObjectFactory` if polymorphic `WikiObject` is kept.

### 10. Small leftover cleanup — **low impact, low effort**

- Replace `ECT`; trim `logback.xml` comments; update OpenAPI/README hostnames.
- Drop `allow-bean-definition-overriding` if nothing else needs it.
- Drop unused `@JvmStatic` / `@JvmOverloads` now that callers are Kotlin.
- Align `Charm.fieldOrder()` return type with `List<String>`.

---

## Suggested follow-up PR backlog

Short titles only, roughly the order above:

1. Generic RetrieveByTemplate + category REST adapter
2. Disable or auth-gate public PUT
3. Configurable wiki URL, timeouts, and page/category cache
4. CORS GET-only for the public API
5. Replace Optional/Stream/Vavr on the process boundary
6. Actuator health + Cloud Run probes
7. Retire org.json from HTTP responses
8. Remove jwiki `NS` from `ArticleRepository`
9. Regression cases for `/api/pages` and `/api/v2/loot`
10. Drop unused Cloud Build `GITHUB_TOKEN` / Packages wiring
11. Fix Charm/Missile gaps in mixin, factory, and Charm.name
12. `@ControllerAdvice` for 404/400/500
13. Fixtures-profile Gradle IT slice
14. Docs: Fandom URL, OpenAPI description, regression JDK, AGENTS Kotlin pin
15. Dockerfile artifact name + wrapper copy
16. Real `Validatable` rules on write
17. Replace loot `NS` reflection
18. Pagination or expand size cap
19. Delete `.sample.settings.xml` and commented logback/jwiki blocks
20. Rename integrationTest package to `adapters.rest`

---

## Out of scope / non-goals of this audit

- No production or CI edits (including the `buid.yml` filename).
- No live calls to Fandom or tibiawiki.dev.
- No proposal to “finish migrating Java” — that work is already on `master`.
