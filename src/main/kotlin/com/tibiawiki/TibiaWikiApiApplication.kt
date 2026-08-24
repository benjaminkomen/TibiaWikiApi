package com.tibiawiki

import com.tibiawiki.config.LoggingJsonEnvironmentPostProcessor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TibiaWikiApiApplication

fun main(args: Array<String>) {
    // Apply before Logback/Spring so Cloud Run's LOGGING_JSON=true is never silent.
    LoggingJsonEnvironmentPostProcessor.applyFromProcessEnvironment()
    runApplication<TibiaWikiApiApplication>(*args)
}
