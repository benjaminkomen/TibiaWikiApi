package com.tibiawiki.config

import org.springframework.boot.EnvironmentPostProcessor
import org.springframework.boot.SpringApplication
import org.springframework.core.Ordered
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource

/**
 * Cloud Run sets `LOGGING_JSON=true`. Map that onto Boot 4.1 structured
 * console logging so stdout is JSON without a Janino `<if>` in logback.xml.
 *
 * An existing `logging.structured.format.console` value wins.
 */
class LoggingJsonEnvironmentPostProcessor : EnvironmentPostProcessor, Ordered {

    override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE - 10

    override fun postProcessEnvironment(
        environment: ConfigurableEnvironment,
        application: SpringApplication
    ) {
        if (!isEnabled(environment)) {
            return
        }
        val existing = environment.getProperty(STRUCTURED_FORMAT_PROPERTY)
        if (!existing.isNullOrBlank()) {
            return
        }
        environment.propertySources.addFirst(
            MapPropertySource(
                PROPERTY_SOURCE_NAME,
                mapOf(STRUCTURED_FORMAT_PROPERTY to GCP_CONSOLE_FORMATTER)
            )
        )
    }

    companion object {
        const val PROPERTY_SOURCE_NAME = "logging-json"
        const val STRUCTURED_FORMAT_PROPERTY = "logging.structured.format.console"
        const val GCP_CONSOLE_FORMATTER =
            "com.tibiawiki.config.GcpConsoleStructuredLogFormatter"
        const val ENV_FLAG = "LOGGING_JSON"

        fun isEnabled(environment: ConfigurableEnvironment): Boolean {
            val raw = environment.getProperty(ENV_FLAG)?.trim().orEmpty()
            return raw.isNotEmpty() &&
                !raw.equals("false", ignoreCase = true) &&
                raw != "0"
        }
    }
}
