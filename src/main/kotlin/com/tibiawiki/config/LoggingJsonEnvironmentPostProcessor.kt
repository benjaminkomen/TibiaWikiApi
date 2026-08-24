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
        if (!isEnabled(environment.getProperty(ENV_FLAG))) {
            return
        }
        val existing = environment.getProperty(STRUCTURED_FORMAT_PROPERTY)
        if (!existing.isNullOrBlank()) {
            return
        }
        applySystemProperty()
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
        const val STRUCTURED_FORMAT_SYSTEM_PROPERTY = "CONSOLE_LOG_STRUCTURED_FORMAT"
        const val GCP_CONSOLE_FORMATTER =
            "com.tibiawiki.config.GcpConsoleStructuredLogFormatter"
        const val ENV_FLAG = "LOGGING_JSON"

        fun isEnabled(raw: String?): Boolean {
            val value = raw?.trim().orEmpty()
            return value.isNotEmpty() &&
                !value.equals("false", ignoreCase = true) &&
                value != "0"
        }

        fun applyFromProcessEnvironment() {
            if (isEnabled(System.getenv(ENV_FLAG))) {
                applySystemProperty()
            }
        }

        private fun applySystemProperty() {
            if (System.getProperty(STRUCTURED_FORMAT_SYSTEM_PROPERTY).isNullOrBlank()) {
                System.setProperty(STRUCTURED_FORMAT_SYSTEM_PROPERTY, GCP_CONSOLE_FORMATTER)
            }
        }
    }
}
