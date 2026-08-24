FROM gradle:9.7.1-jdk25 AS builder

# Copy local code to the container image.
WORKDIR /app
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
# bootJar compiles production sources only; keep tests out of the builder.
COPY src/main ./src/main

# Produce the boot jar only. Unit/IT coverage runs in GitHub Actions.
# bootJar archiveFileName is TibiaWikiApi.jar (see build.gradle.kts).
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25.0.4_7-jre
RUN mkdir /project
COPY --from=builder --chown=65532:65532 /app/build/libs/TibiaWikiApi.jar /project/TibiaWikiApi.jar
WORKDIR /project

# Cloud Run injects PORT (8080 locally if unset). Spring Boot reads it from
# application.properties (`server.port=${PORT:8080}`). Do not expand PORT in a
# shell ENTRYPOINT: Dockerfile `$$` is not reliably reduced to `$` in this
# image's JSON ENTRYPOINT, so `sh` expands `$$` to PID 1 (`1PORT` /
# `1{PORT:-8080}`) and startup probes fail (issue #473; #474 was incomplete).
ENV PORT=8080

# Numeric uid/gid so the runtime user does not depend on /etc/passwd.
USER 65532:65532

# Exec-form java, no `sh -c`: the JVM is PID 1 so Cloud Run SIGTERM reaches
# Spring Boot. Bind all interfaces. Drop the Java 8 -Djava.security.egd leftover.
ENTRYPOINT ["java", "-Dserver.address=0.0.0.0", "-jar", "/project/TibiaWikiApi.jar"]
