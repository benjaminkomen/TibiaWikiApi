FROM gradle:9.7.1-jdk25 as builder

# Copy local code to the container image.
WORKDIR /app
COPY build.gradle .
COPY src ./src

ARG GITHUB_TOKEN
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

# Produce the boot jar only. Unit/IT coverage runs in GitHub Actions;
# this image does not copy test fixtures or a settings.gradle (WORKDIR
# name "app" keeps the artifact at app-2.0.0.jar).
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:25.0.4_7-jre
RUN mkdir /project
COPY --from=builder /app/build/libs/app-2.0.0.jar /project/TibiaWikiApi.jar
WORKDIR /project

# Run the web service on container startup.
CMD java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT} -jar /project/TibiaWikiApi.jar
