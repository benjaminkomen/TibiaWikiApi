# Docker

The `Dockerfile` in this folder is used to build an image and
deploy to GCP Cloud Run. You can test the docker image locally using
the following commands:

- Make sure you have Docker installed and running on your machine
- Build the docker image from the repository root:

```bash
docker build -t tibiawikiapi -f ./docker/Dockerfile .
```

- Run it with:

```bash
docker run -it -e PORT=8080 -p 8080:8080 tibiawikiapi
```

The image build uses the Gradle wrapper and `settings.gradle`. It does **not**
need a `GITHUB_TOKEN`: `jwiki` is resolved from Maven Central.

## Cloud Run probes

Actuator probe groups are process-local (application liveness/readiness state).
They do **not** call Fandom or tibiawiki.dev.

| Probe | Path | When Cloud Run uses it |
| --- | --- | --- |
| Startup | `/actuator/health/readiness` | Until the process is ready to serve |
| Liveness | `/actuator/health/liveness` | Periodically; failures restart the instance |

`cloudbuild.yaml` sets these on `gcloud run deploy`. To set them by hand:

```bash
gcloud run services update tibiawikiapi \
  --region europe-west1 \
  --startup-probe=httpGet.path=/actuator/health/readiness,timeoutSeconds=4,periodSeconds=10,failureThreshold=36 \
  --liveness-probe=httpGet.path=/actuator/health/liveness,timeoutSeconds=4,periodSeconds=30,failureThreshold=3
```

Related endpoints (same process, still no wiki I/O):

- `GET /actuator/health` — `status: UP` plus the `liveness`/`readiness` groups
- `GET /actuator/info` — build coordinates from `bootJar` `buildInfo()`

Startup can take a while on the default profile because `JwikiArticleRepository`
talks to Fandom while the Spring context is created. That is independent of
these probe paths: a passing probe does not mean Fandom is up.

## Image tags (issue #449)

Prod (`cloudbuild.yaml` and `scripts/deploy.sh`) pushes and deploys a commit SHA
tag. `:latest` is a pointer, not the deploy identity.

| Tag | Who writes it | Used for |
| --- | --- | --- |
| `gcr.io/$PROJECT_ID/tibiawikiapi:$COMMIT_SHA` | prod Cloud Build / `deploy.sh` | Cloud Run `--image` |
| `gcr.io/$PROJECT_ID/tibiawikiapi:latest` | prod pipeline **after** success | convenience pointer |
| `gcr.io/$PROJECT_ID/tibiawikiapi:pr-$SHORT_SHA` | `cloudbuild-pr.yaml` only | PR image, if the trigger is on |

`$COMMIT_SHA` / `$SHORT_SHA` are Cloud Build GitHub-trigger substitutions.
`deploy.sh` uses `git rev-parse HEAD` for the same SHA tag.

**PR builds cannot overwrite prod `:latest`.** `cloudbuild-pr.yaml` builds
`:pr-$SHORT_SHA` and lists only that name under `images:`. Cloud Build's
`images:` push would clobber `:latest` if this file listed an untagged image
(`gcr.io/.../tibiawikiapi`) or `:latest`. It does neither. Prod `:latest` is
written only from `cloudbuild.yaml` / `deploy.sh`.

Artifact Registry (`europe-west1-docker.pkg.dev/...`) is a follow-up: it needs
a one-time repository plus IAM. SHA tags on `gcr.io` are the unblocking fix
(GCR hostnames already map at this project).

## Rollback

Send 100% traffic to a previous Cloud Run revision (the image SHA stays in the
registry):

```bash
gcloud run revisions list --service tibiawikiapi \
  --region europe-west1 --project tibiawikiapi-246008

gcloud run services update-traffic tibiawikiapi \
  --region europe-west1 --project tibiawikiapi-246008 \
  --to-revisions PREVIOUS_REVISION=100
```

Or redeploy a known commit:

```bash
gcloud run deploy tibiawikiapi \
  --image gcr.io/tibiawikiapi-246008/tibiawikiapi:$COMMIT_SHA \
  --region europe-west1 --project tibiawikiapi-246008
```

Canary (`--no-traffic` then a traffic split) is optional at this volume.
Fail-closed Ready + docs/health smoke on Cloud Build is [#446](https://github.com/benjaminkomen/TibiaWikiApi/issues/446), not this change.

## Cloud Run knobs (measured follow-up)

Do **not** change these without expand-cache / cold-start evidence. Memory is
already explicit (`1Gi`, deferred by #399). The rest are unset, so Cloud Run
platform defaults apply:

| Knob | Current | Why it is a follow-up |
| --- | --- | --- |
| `--cpu` | unset (1) | no CPU-vs-latency measurement |
| `--concurrency` | unset (80) | needs expand-cache metrics; do not guess |
| `--min-instances` | unset (0) | in-process wiki cache dies on every cold start; `1` may help — measure first |
| `--timeout` | unset (300s) | no request-duration evidence to lower it |
| `--cpu-boost` | unset | cheap for startup; still a measured change, not a guess |

`--cpu-boost`, `--min-instances`, and `--concurrency` stay comments in
`cloudbuild.yaml` until those measurements exist.
