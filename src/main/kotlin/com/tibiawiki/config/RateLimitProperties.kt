package com.tibiawiki.config

import org.springframework.boot.context.properties.ConfigurationProperties
import kotlin.math.round

/**
 * Per-client token-bucket rate limits for GET/HEAD under the /api path.
 * Disabled by default; enable on Cloud Run with `RATE_LIMIT_ENABLED=true`.
 */
@ConfigurationProperties(prefix = "rate-limit")
class RateLimitProperties {
    var enabled: Boolean = false
    var capacity: Double = DEFAULT_CAPACITY
    var refillPerSecond: Double = DEFAULT_REFILL_PER_SECOND
    var expandCapacity: Double = DEFAULT_EXPAND_CAPACITY
    var expandRefillPerSecond: Double = DEFAULT_EXPAND_REFILL_PER_SECOND
    var maxKeys: Long = DEFAULT_MAX_KEYS
    var keyTtlMinutes: Long = DEFAULT_KEY_TTL_MINUTES

    fun sustainedLimitPerMinute(): Long {
        return round(refillPerSecond * SECONDS_PER_MINUTE).toLong().coerceAtLeast(1L)
    }

    fun expandSustainedLimitPerMinute(): Long {
        return round(expandRefillPerSecond * SECONDS_PER_MINUTE).toLong().coerceAtLeast(1L)
    }

    companion object {
        const val DEFAULT_CAPACITY = 20.0
        const val DEFAULT_REFILL_PER_SECOND = 0.5
        const val DEFAULT_EXPAND_CAPACITY = 2.0
        const val DEFAULT_EXPAND_REFILL_PER_SECOND = 0.0833
        const val DEFAULT_MAX_KEYS = 10_000L
        const val DEFAULT_KEY_TTL_MINUTES = 15L
        private const val SECONDS_PER_MINUTE = 60.0
    }
}
