package com.tibiawiki.config

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.LoggingEvent
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.`is`
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class GcpConsoleStructuredLogFormatterTest {

    @Test
    fun formatsCloudLoggingJsonWithSeverity() {
        val logger = LoggerFactory.getLogger("com.tibiawiki.logging") as Logger
        val event = LoggingEvent(null, logger, Level.WARN, "probe failed", null, null)
        event.timeStamp = 1_700_000_000_000L

        val line = GcpConsoleStructuredLogFormatter().format(event)
        val json = JSONObject(line.trim())

        assertThat(json.getString("severity"), `is`("WARNING"))
        assertThat(json.getString("message"), `is`("probe failed"))
        assertThat(json.getString("logger"), `is`("com.tibiawiki.logging"))
        assertThat(line, containsString("\n"))
    }

    @Test
    fun includesExceptionText() {
        val logger = LoggerFactory.getLogger("com.tibiawiki.logging") as Logger
        val event = LoggingEvent(
            null,
            logger,
            Level.ERROR,
            "startup failed",
            IllegalStateException("no appenders"),
            null
        )

        val json = JSONObject(GcpConsoleStructuredLogFormatter().format(event).trim())

        assertThat(json.getString("severity"), `is`("ERROR"))
        assertThat(json.getString("exception"), containsString("IllegalStateException"))
        assertThat(json.getString("exception"), containsString("no appenders"))
    }
}
