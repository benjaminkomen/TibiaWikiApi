FROM gradle:7.0.2-jdk16 as builder

# Copy local code to the container image.
WORKDIR /app
COPY build.gradle .
COPY src ./src

ARG GITHUB_TOKEN
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

# Build a release artifact.
RUN gradle build --no-daemon

FROM adoptopenjdk/openjdk16:jre16u-alpine-nightly
RUN mkdir /project
COPY --from=builder /app/build/libs/app-2.0.0.jar /project/TibiaWikiApi.jar
WORKDIR /project

# Run the web service on container startup.
CMD java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT} -jar /project/TibiaWikiApi.jar
