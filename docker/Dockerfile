FROM gradle:7.0.2-jdk16 as builder

# Copy local code to the container image.
WORKDIR /app
COPY build.gradle .
COPY src ./src

ARG GITHUB_TOKEN
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

# Build a release artifact.
RUN gradle build --no-daemon

FROM adoptopenjdk/openjdk16:jdk16u-alpine-nightly-slim

RUN ls -la /
COPY --from=builder /app/build/libs/app-*.jar /TibiaWikiApi.jar

# Run the web service on container startup.
CMD [ "java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=${PORT}","-jar","/TibiaWikiApi.jar"]
