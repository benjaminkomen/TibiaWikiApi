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

    /**
     * Number of rightmost `X-Forwarded-For` entries appended by trusted proxies.
     * The client IP is the leftmost of these. `1` fits Cloud Run (its front end
     * appends the client IP); `0` ignores the header and uses the socket address.
     */
    var forwardedForTrustedHops: Int = DEFAULT_FORWARDED_FOR_TRUSTED_HOPS

    /**
     * Scale factor for the shared [ClientIpKeyResolver.UNATTRIBUTED_KEY] buckets
     * (requests with no per-client address). Applies to capacity and refill of
     * both the primary and the expand bucket. Normal per-client limits are unchanged.
     */
    var unattributedMultiplier: Double = DEFAULT_UNATTRIBUTED_MULTIPLIER

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
        const val DEFAULT_FORWARDED_FOR_TRUSTED_HOPS = 1
        const val DEFAULT_UNATTRIBUTED_MULTIPLIER = 5.0
        private const val SECONDS_PER_MINUTE = 60.0
    }
}
