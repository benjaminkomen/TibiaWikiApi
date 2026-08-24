FROM gradle:9.7.1-jdk25 AS builder

# Copy local code to the container image.
WORKDIR /app
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
# bootJar compiles production sources only; keep tests out of the builder.
COPY src/main ./src/main

# Produce the boot jar only. Unit/IT coverage runs in GitHub Actions.
# bootJar archiveFileName is TibiaWikiApi.jar (see build.gradle).
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25.0.4_7-jre
RUN mkdir /project
COPY --from=builder --chown=65532:65532 /app/build/libs/TibiaWikiApi.jar /project/TibiaWikiApi.jar
WORKDIR /project

# Cloud Run injects PORT (8080 locally if unset). Bind all interfaces.
ENV PORT=8080

# Numeric uid/gid so the runtime user does not depend on /etc/passwd.
USER 65532:65532

# Exec-form JSON + `exec`: sh expands $PORT, then the JVM is PID 1 so Cloud Run
# SIGTERM reaches Spring Boot. Drop the Java 8 -Djava.security.egd leftover.
# $$ escapes Dockerfile build-time ENV substitution so Cloud Run can override PORT.
ENTRYPOINT ["sh", "-c", "exec java -Dserver.address=0.0.0.0 -Dserver.port=$${PORT:-8080} -jar /project/TibiaWikiApi.jar"]
