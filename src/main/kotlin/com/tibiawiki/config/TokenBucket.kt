package com.tibiawiki.config

import kotlin.math.ceil
import kotlin.math.min

/**
 * Simple in-process token bucket. [clockNanos] is injectable so unit tests can
 * advance time without sleeping.
 */
class TokenBucket(
    private val capacity: Double,
    private val refillPerSecond: Double,
    private val clockNanos: () -> Long = System::nanoTime
) {
    private var tokens: Double = capacity.coerceAtLeast(0.0)
    private var lastRefillNanos: Long = clockNanos()

    @Synchronized
    fun tryConsume(cost: Double = 1.0): TryConsumeResult {
        require(cost > 0.0) { "cost must be positive" }
        refill()
        if (tokens >= cost) {
            tokens -= cost
            return TryConsumeResult(
                allowed = true,
                remaining = tokens,
                retryAfterSeconds = 0
            )
        }
        val deficit = cost - tokens
        val retryAfter = if (refillPerSecond > 0.0) {
            ceil(deficit / refillPerSecond).toInt().coerceAtLeast(1)
        } else {
            Int.MAX_VALUE
        }
        return TryConsumeResult(
            allowed = false,
            remaining = tokens,
            retryAfterSeconds = retryAfter
        )
    }

    @Synchronized
    fun remaining(): Double {
        refill()
        return tokens
    }

    private fun refill() {
        if (refillPerSecond <= 0.0 || capacity <= 0.0) {
            return
        }
        val now = clockNanos()
        val elapsedSeconds = (now - lastRefillNanos) / NANOS_PER_SECOND
        if (elapsedSeconds <= 0.0) {
            return
        }
        tokens = min(capacity, tokens + elapsedSeconds * refillPerSecond)
        lastRefillNanos = now
    }

    data class TryConsumeResult(
        val allowed: Boolean,
        val remaining: Double,
        val retryAfterSeconds: Int
    )

    companion object {
        private const val NANOS_PER_SECOND = 1_000_000_000.0
    }
}
