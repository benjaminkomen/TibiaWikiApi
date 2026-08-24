package com.tibiawiki.config

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import org.json.JSONObject
import org.springframework.boot.logging.structured.StructuredLogFormatter
import java.time.Instant

/**
 * Cloud Logging JSON on stdout. Used when [LoggingJsonEnvironmentPostProcessor]
 * sets `logging.structured.format.console` from `LOGGING_JSON`.
 */
class GcpConsoleStructuredLogFormatter : StructuredLogFormatter<ILoggingEvent> {

    override fun format(event: ILoggingEvent): String {
        val payload = JSONObject()
        payload.put("severity", severity(event.level))
        payload.put("message", event.formattedMessage)
        payload.put("logger", event.loggerName)
        payload.put("thread", event.threadName)
        payload.put("timestamp", Instant.ofEpochMilli(event.timeStamp).toString())
        val throwable = event.throwableProxy
        if (throwable != null) {
            payload.put("exception", ThrowableProxyUtil.asString(throwable))
        }
        return payload.toString() + "\n"
    }

    private fun severity(level: Level): String {
        return when {
            level.isGreaterOrEqual(Level.ERROR) -> "ERROR"
            level.isGreaterOrEqual(Level.WARN) -> "WARNING"
            level.isGreaterOrEqual(Level.INFO) -> "INFO"
            else -> "DEBUG"
        }
    }
}
