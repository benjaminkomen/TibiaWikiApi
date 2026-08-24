import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    jacoco
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.spring.boot)
}

group = "com.tibiawiki"
version = "2.0.0"
description = "TibiaWikiApi"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.jackson)
    implementation(libs.spring.boot.starter.actuator)
    // Stay on Central jwiki 1.11.0: Wiki.getNS already resolves custom namespaces
    // (TibiaWiki loot is 112) from siteinfo, so the Packages fork is not needed.
    implementation(libs.jwiki)
    implementation(libs.slf4j.api)
    implementation(libs.json)
    implementation(libs.guava)
    implementation(libs.vavr)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.mockito.core)
    testImplementation(libs.hamcrest)
    testImplementation(libs.spring.boot.starter.webmvc.test) {
        exclude(group = "com.vaadin.external.google", module = "android-json")
    }
    testImplementation(libs.spring.boot.starter.restclient)

    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        // Spring Boot 4.1 / Kotlin 2.3+: apply constructor annotations to param + property
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
        jvmTarget.set(JvmTarget.JVM_25)
    }
}

sonar {
    properties {
        property("sonar.projectName", "TibiaWikiApi")
        property("sonar.projectKey", "com.tibiawiki:TibiaWikiApi")
        property("sonar.organization", "benjaminkomen-github")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.sources", "src/main")
        property("sonar.tests", "src/test,src/integrationTest")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "./build/reports/jacocoTestReport.xml")
        // CI (`buid.yml`) also passes -Dsonar.qualitygate.wait=true so a red
        // SonarCloud gate fails the GitHub Build job. Set here too so any
        // `./gradlew sonar` in GITHUB_ACTIONS is fail-closed.
        if (System.getenv("GITHUB_ACTIONS") == "true") {
            property("sonar.qualitygate.wait", "true")
        }
        // kotlin:S6474 wants gradle/verification-metadata.xml. Generating and
        // committing checksums for every Maven coordinate would churn on each
        // Dependabot bump and is too heavy for this repo; leave the hotspot
        // suppressed rather than adding sonar.coverage.exclusions.
        property("sonar.issue.ignore.multicriteria", "e1")
        property("sonar.issue.ignore.multicriteria.e1.ruleKey", "kotlin:S6474")
        property("sonar.issue.ignore.multicriteria.e1.resourceKey", "**")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    testLogging {
        events(
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.FAILED,
            TestLogEvent.STANDARD_ERROR,
        )
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com/tibiawiki/TibiaWikiApiApplication"
    }
}

val integrationTestSourceSet = sourceSets.create("integrationTest") {
    compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output +
        configurations.testRuntimeClasspath.get()
    runtimeClasspath += output + compileClasspath
}

configurations {
    named("integrationTestImplementation") {
        extendsFrom(configurations.implementation.get())
        extendsFrom(configurations.testImplementation.get())
    }
    all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion(libs.versions.kotlin.get())
            }
        }
    }
}

springBoot {
    buildInfo()
}

tasks.bootJar {
    // Stable name so the Dockerfile does not hardcode app-<version>.jar
    archiveFileName.set("TibiaWikiApi.jar")
}

idea {
    module {
        testSources.from(kotlin.sourceSets.getByName("integrationTest").kotlin.srcDirs)
        testResources.from(integrationTestSourceSet.resources.srcDirs)
    }
}

val integrationTest = tasks.register<Test>("integrationTest") {
    description = "Runs the integration tests."
    group = "verification"
    testClassesDirs = integrationTestSourceSet.output.classesDirs
    classpath = integrationTestSourceSet.runtimeClasspath
    outputs.upToDateWhen { false }
    mustRunAfter(tasks.test)
    // PUT ITs exercise ModifyAny. Do not add src/integrationTest/resources/application.properties:
    // Spring Boot loads only the first classpath:/application.properties, which would hide
    // main actuator exposure and springdoc.api-docs.path. @SpringBootTest(properties=...)
    // still overrides this per class (e.g. wiki.write.enabled=false).
    systemProperty("wiki.write.enabled", "true")
}

tasks.check {
    dependsOn(integrationTest)
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.jacocoTestReport {
    dependsOn(tasks.check)
    // jacocoTestReport defaults to the unit `test` exec only. Controllers that
    // are exercised solely from src/integrationTest otherwise report 0% in Sonar.
    executionData.setFrom(fileTree(layout.buildDirectory.dir("jacoco")).include("*.exec"))

    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacocoTestReport.xml"))
    }
}
