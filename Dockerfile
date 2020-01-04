FROM maven:3.6.2-jdk-13 as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

ARG GITHUB_TOKEN
ENV GITHUB_TOKEN=${GITHUB_TOKEN}
COPY .travis.settings.xml /root/.m2/settings.xml

# Build a release artifact.
RUN mvn package -DskipTests

FROM adoptopenjdk/openjdk13:jdk-13_33-alpine-slim

COPY --from=builder /app/target/TibiaWikiApi.jar /TibiaWikiApi.jar

# Run the web service on container startup.
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=${PORT}","-jar","/TibiaWikiApi.jar"]
