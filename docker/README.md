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
