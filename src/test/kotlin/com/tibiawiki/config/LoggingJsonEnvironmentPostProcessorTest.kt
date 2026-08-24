package com.tibiawiki.config

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.StandardEnvironment

class LoggingJsonEnvironmentPostProcessorTest {

    @Test
    fun unsetFlagLeavesStructuredFormatAlone() {
        val environment = environment()

        processor().postProcessEnvironment(environment, SpringApplication())

        assertThat(environment.getProperty(STRUCTURED), nullValue())
    }

    @Test
    fun falseFlagLeavesStructuredFormatAlone() {
        val environment = environment("LOGGING_JSON" to "false")

        processor().postProcessEnvironment(environment, SpringApplication())

        assertThat(environment.getProperty(STRUCTURED), nullValue())
    }

    @Test
    fun trueFlagSelectsGcpConsoleFormatter() {
        val environment = environment("LOGGING_JSON" to "true")

        processor().postProcessEnvironment(environment, SpringApplication())

        assertThat(
            environment.getProperty(STRUCTURED),
            `is`(LoggingJsonEnvironmentPostProcessor.GCP_CONSOLE_FORMATTER)
        )
    }

    @Test
    fun existingStructuredFormatWins() {
        val environment = environment(
            "LOGGING_JSON" to "true",
            STRUCTURED to "logstash"
        )

        processor().postProcessEnvironment(environment, SpringApplication())

        assertThat(environment.getProperty(STRUCTURED), `is`("logstash"))
    }

    private fun processor() = LoggingJsonEnvironmentPostProcessor()

    private fun environment(vararg pairs: Pair<String, String>): ConfigurableEnvironment {
        val environment = StandardEnvironment()
        if (pairs.isNotEmpty()) {
            environment.propertySources.addFirst(
                MapPropertySource("test-logging-json", pairs.toMap())
            )
        }
        return environment
    }

    companion object {
        private const val STRUCTURED =
            LoggingJsonEnvironmentPostProcessor.STRUCTURED_FORMAT_PROPERTY
    }
}
