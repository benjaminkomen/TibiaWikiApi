FROM gradle:7.3-jdk17 as builder

# Copy local code to the container image.
WORKDIR /app
COPY build.gradle .
COPY src ./src

ARG GITHUB_TOKEN
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

# Build a release artifact.
RUN gradle build --no-daemon

FROM openjdk:17-slim-buster
RUN mkdir /project
COPY --from=builder /app/build/libs/app-2.0.0.jar /project/TibiaWikiApi.jar
WORKDIR /project

# Run the web service on container startup.
CMD java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT} -jar /project/TibiaWikiApi.jar
