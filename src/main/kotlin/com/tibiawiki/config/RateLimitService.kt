package com.tibiawiki.config

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicLong

/**
 * In-memory token buckets keyed by [ClientIpKeyResolver] output. Counters are
 * per JVM — with N Cloud Run instances the effective rate is about N× config.
 */
@Component
class RateLimitService(
    private val properties: RateLimitProperties
) {
    private val primaryBuckets: Cache<String, TokenBucket> = buildCache()
    private val expandBuckets: Cache<String, TokenBucket> = buildCache()

    val allowedCount = AtomicLong()
    val rejectedCount = AtomicLong()

    fun tryConsume(clientKey: String, expand: Boolean): Verdict {
        val primary = bucketFor(primaryBuckets, clientKey) {
            TokenBucket(properties.capacity, properties.refillPerSecond)
        }
        val primaryResult = primary.tryConsume()
        if (!primaryResult.allowed) {
            rejectedCount.incrementAndGet()
            return Verdict(
                allowed = false,
                limit = properties.sustainedLimitPerMinute(),
                remaining = primaryResult.remaining.toLong().coerceAtLeast(0),
                retryAfterSeconds = primaryResult.retryAfterSeconds.coerceAtMost(MAX_RETRY_AFTER),
                scope = Scope.PRIMARY
            )
        }

        if (expand) {
            val expandBucket = bucketFor(expandBuckets, clientKey) {
                TokenBucket(properties.expandCapacity, properties.expandRefillPerSecond)
            }
            val expandResult = expandBucket.tryConsume()
            if (!expandResult.allowed) {
                rejectedCount.incrementAndGet()
                return Verdict(
                    allowed = false,
                    limit = properties.expandSustainedLimitPerMinute(),
                    remaining = expandResult.remaining.toLong().coerceAtLeast(0),
                    retryAfterSeconds = expandResult.retryAfterSeconds.coerceAtMost(MAX_RETRY_AFTER),
                    scope = Scope.EXPAND
                )
            }
        }

        allowedCount.incrementAndGet()
        return Verdict(
            allowed = true,
            limit = properties.sustainedLimitPerMinute(),
            remaining = primary.remaining().toLong().coerceAtLeast(0),
            retryAfterSeconds = 0,
            scope = if (expand) Scope.EXPAND else Scope.PRIMARY
        )
    }

    private fun bucketFor(
        cache: Cache<String, TokenBucket>,
        key: String,
        factory: () -> TokenBucket
    ): TokenBucket {
        return try {
            cache.get(key) { factory() }
        } catch (e: ExecutionException) {
            val cause = e.cause
            if (cause is RuntimeException) {
                throw cause
            }
            throw IllegalStateException("rate-limit bucket create failed", e)
        }
    }

    private fun buildCache(): Cache<String, TokenBucket> {
        return CacheBuilder.newBuilder()
            .maximumSize(properties.maxKeys.coerceAtLeast(1L))
            .expireAfterAccess(Duration.ofMinutes(properties.keyTtlMinutes.coerceAtLeast(1L)))
            .build()
    }

    enum class Scope {
        PRIMARY,
        EXPAND
    }

    data class Verdict(
        val allowed: Boolean,
        val limit: Long,
        val remaining: Long,
        val retryAfterSeconds: Int,
        val scope: Scope
    )

    companion object {
        private const val MAX_RETRY_AFTER = 60
    }
}
