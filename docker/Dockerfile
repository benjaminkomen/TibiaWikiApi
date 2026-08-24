FROM gradle:9.7.1-jdk25 AS builder

# Copy local code to the container image.
WORKDIR /app
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src

# Produce the boot jar only. Unit/IT coverage runs in GitHub Actions.
# bootJar archiveFileName is TibiaWikiApi.jar (see build.gradle).
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25.0.4_7-jre
RUN mkdir /project
COPY --from=builder /app/build/libs/TibiaWikiApi.jar /project/TibiaWikiApi.jar
WORKDIR /project

# Run the web service on container startup.
CMD java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT} -jar /project/TibiaWikiApi.jar
