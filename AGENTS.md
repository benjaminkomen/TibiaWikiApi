# TibiaWikiApi — agent notes

## What this is

Spring Boot REST API over TibiaWiki (Fandom). Public Swagger: https://tibiawiki.dev. Deployed to GCP Cloud Run (`cloudbuild.yaml`). Resource list and `?expand=true`: [README.md](README.md).

## Stack

- **JDK 25** (`java.toolchain` and Kotlin `jvmTarget` 25 in `build.gradle.kts`). Gradle wrapper **9.7.1** — always use `./gradlew`.
- Kotlin **2.4.10**, Spring Boot **4.1.1**.
- Layout: production, unit, and integration code is Kotlin under `src/main/kotlin`, `src/test/kotlin`, and `src/integrationTest/kotlin`.

## Build / run

JDK 25 required.

```bash
./gradlew build
./gradlew bootRun          # http://localhost:8080 — default profile is live jwiki → Fandom
```

GitHub Actions Gradle workflow is `.github/workflows/buid.yml` (filename typo; do not rename unless asked).

## Tests (Gradle)

CI (`buid.yml`) runs:

```bash
./gradlew ktlintCheck jacocoTestReport
```

`jacocoTestReport` depends on `check`, which also runs the `integrationTest` source set. A slice of those ITs (`FixturesProfileIT`) uses `@ActiveProfiles("fixtures")` and the real `FixtureArticleRepository` — no mocks and no Fandom. ktlint is the style checker — do not add a style guide.

Cloud Run uses the **default** profile (live `JwikiArticleRepository`, `LOGGING_JSON=true`). When changing beans, constructors, logging, or Boot config: add or keep a default (or prod-like) profile IT that constructs **real** beans (`DefaultProfileWikiBeansIT`). Fixtures and `@MockitoBean` alone do not cover that path. Prod env flags that affect logging (e.g. `LOGGING_JSON=true`) must be exercised in CI (`LoggingJsonActuatorIT`).

The Bun harness in `regression/` is **not** part of the Gradle `test` task.

## Fixture regression (critical)

CI (`.github/workflows/api-regression.yml`) boots with `--spring.profiles.active=fixtures` and `regression/fixtures/`. That profile uses in-process `FixtureArticleRepository` and never constructs `JwikiArticleRepository` — **no outbound calls** to Fandom or tibiawiki.dev.

Never call live Fandom or https://tibiawiki.dev from CI or agent verification. Commands and goldens: [regression/README.md](regression/README.md).

```bash
./regression/scripts/boot-fixtures.sh    # repo root; leave running
./regression/scripts/wait-for-api.sh     # waits on http://localhost:8080/api/corpses
cd regression && bun run smoke:docs      # Swagger UI / OpenAPI 3.0 / health
cd regression && bun run test            # not `bun test` (that is Bun’s built-in runner)
```

Refresh goldens with `bun run capture` **only** against the fixture-backed server (`BASE_URL` default `http://localhost:8080`).

If you touch OpenAPI, springdoc, Swagger, or controllers that affect the public catalog, run `cd regression && bun run smoke:docs` (and/or `SwaggerUiIT`). Status-200 HTML is not enough: OpenAPI must stay **3.0.x** so the bundled UI can render; concrete `WikiCategory` paths must stay enumerated (no generic `{category}` template only).

## Boundaries

- Do not expand into unrelated open/draft feature work unless tasked.
- Prefer the fixtures profile over hitting the production wiki during routine PR work.

## Stacked PRs

Required for multi-PR work. Install the GitHub CLI stack extension:

```bash
gh extension install github/gh-stack
```

Prefer `gh stack` over opening many independent PRs onto `master`.

- **New layered work:** `gh stack init <bottom>` → commit → `gh stack add <next>` → … → `gh stack submit --auto --open`
- **Existing open PRs that should be one stack:** `gh stack link --base master --open <bottom-pr> … <tip-pr>`
- **Sync / rebase:** `gh stack sync` / `gh stack rebase --upstack`
- **Merge:** `gh stack merge <tip-or-stack-number> --yes` (never plain `gh pr merge` for a stack)

Non-interactive: always pass flags (`submit --auto`, `view --json`, `merge --yes`). Bare TUI commands hang agents.

One concern per layer. Keep history linear: each tip must contain the parent tip.

## Deploy

Merge-to-prod is `cloudbuild.yaml` (not PR CI): tag `$COMMIT_SHA` and `:latest`, `gcloud run deploy` with env/probe flags (`LOGGING_JSON=true`, `WIKI_WRITE_ENABLED=false`, startup/liveness), wait until the **new revision is Ready**, then `cd regression && bun run smoke:docs` against the **revision URL** (Swagger + actuator only; no wiki/Fandom paths). `gcloud run deploy` already sends 100% to the new revision; do not add `gcloud alpha run services update-traffic --to-latest`. Flags and the Ready-then-smoke gate live in `scripts/cloud-run-release.sh`.

`scripts/deploy.sh` is the **ops** path: builds `$COMMIT_SHA`, then the same release script, with `smoke:docs` against `https://tibiawiki.dev` (`BASE_URL` override is documented in the script). `:latest` is retagged only after Ready + smoke. `bun` is required for that smoke.

- Image build success is not deploy success. The Cloud Run revision must be Ready (startup probe passed); then docs/health smoke must pass.
- Prod images are `gcr.io/tibiawikiapi-246008/tibiawikiapi:$COMMIT_SHA`. `:latest` is a pointer updated after success. `cloudbuild-pr.yaml` tags `pr-$SHORT_SHA` only and must not list untagged / `:latest` images. Rollback and Cloud Run knobs: [`docker/README.md`](docker/README.md).
- If deploy or Ready fails, do not smoke; exit non-zero. The previous revision may still be serving traffic.
- If smoke fails after Ready, Cloud Build / the script still fail; the new revision may already be serving 100%.
- Do not declare success from build logs or “looks fine.” User screenshots of Swagger UI and health beat agent claims.
- Live `https://tibiawiki.dev` smoke is **ops** (`deploy.sh` only). PR/CI verification still uses fixtures and must not hit Fandom or tibiawiki.dev.

## PR / verify

Before considering work done:

1. `./gradlew ktlintCheck jacocoTestReport` is green.
2. If you change beans, constructors, logging, or Boot config, add/keep a default (or prod-like) profile IT with real beans; fixtures/`@MockitoBean` alone is not enough. Prod logging flags (`LOGGING_JSON=true`) must run in CI.
3. If you change API responses, controllers, wiki parsing, fixtures, or goldens, run fixture regression as above.
4. If you touch OpenAPI/springdoc/Swagger or controllers that affect the public catalog, run `cd regression && bun run smoke:docs` (and/or `SwaggerUiIT`). OpenAPI must stay 3.0.x; WikiCategory paths must be enumerated (no generic `{category}` template only).
5. For multi-PR dependency work, use a formal GitHub PR stack ([Stacked PRs](#stacked-prs)).
6. Deploy success is a Ready revision **then** `smoke:docs` (Cloud Build: revision URL; ops `deploy.sh`: tibiawiki.dev), not image build alone (see [Deploy](#deploy)).
