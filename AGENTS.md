# TibiaWikiApi — agent notes

## What this is

Spring Boot REST API over TibiaWiki (Fandom). Public Swagger: https://tibiawiki.dev. Deployed to GCP Cloud Run (`cloudbuild.yaml`). Resource list and `?expand=true`: [README.md](README.md).

## Stack

- **JDK 25** (`java.toolchain` and Kotlin `jvmTarget` 25 in `build.gradle`). Gradle wrapper **9.7.1** — always use `./gradlew`.
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

## PR / verify

Before considering work done:

1. `./gradlew ktlintCheck jacocoTestReport` is green.
2. If you change API responses, controllers, wiki parsing, fixtures, or goldens, run fixture regression as above.
3. For multi-PR dependency work, use a formal GitHub PR stack ([Stacked PRs](#stacked-prs)).
