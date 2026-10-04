package com.tibiawiki.config

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ExecutionException

/**
 * In-memory token buckets keyed by [ClientIpKeyResolver] output. Buckets are
 * per JVM — with N Cloud Run instances the effective rate is about N× config.
 */
@Component
class RateLimitService(
    private val properties: RateLimitProperties
) {
    private val primaryBuckets: Cache<String, TokenBucket> = buildCache()
    private val expandBuckets: Cache<String, TokenBucket> = buildCache()

    fun tryConsume(clientKey: String, expand: Boolean): Verdict {
        val scale = scaleFor(clientKey)
        val primary = bucketFor(primaryBuckets, clientKey) {
            TokenBucket(properties.capacity * scale, properties.refillPerSecond * scale)
        }
        val primaryResult = primary.tryConsume()
        if (!primaryResult.allowed) {
            return Verdict(
                allowed = false,
                limit = scaledLimit(properties.sustainedLimitPerMinute(), scale),
                remaining = primaryResult.remaining.toLong().coerceAtLeast(0),
                retryAfterSeconds = primaryResult.retryAfterSeconds.coerceAtMost(MAX_RETRY_AFTER),
                scope = Scope.PRIMARY
            )
        }

        if (expand) {
            val expandBucket = bucketFor(expandBuckets, clientKey) {
                TokenBucket(properties.expandCapacity * scale, properties.expandRefillPerSecond * scale)
            }
            val expandResult = expandBucket.tryConsume()
            if (!expandResult.allowed) {
                return Verdict(
                    allowed = false,
                    limit = scaledLimit(properties.expandSustainedLimitPerMinute(), scale),
                    remaining = expandResult.remaining.toLong().coerceAtLeast(0),
                    retryAfterSeconds = expandResult.retryAfterSeconds.coerceAtMost(MAX_RETRY_AFTER),
                    scope = Scope.EXPAND
                )
            }
        }

        return Verdict(
            allowed = true,
            limit = scaledLimit(properties.sustainedLimitPerMinute(), scale),
            remaining = primary.remaining().toLong().coerceAtLeast(0),
            retryAfterSeconds = 0,
            scope = if (expand) Scope.EXPAND else Scope.PRIMARY
        )
    }

    /**
     * Requests without a per-client address share one bucket, so it is scaled up
     * instead of using the normal per-client limit. It is still a single bucket:
     * nothing a client sends can select a different one.
     */
    private fun scaleFor(clientKey: String): Double {
        return if (clientKey == ClientIpKeyResolver.UNATTRIBUTED_KEY) {
            properties.unattributedMultiplier.coerceAtLeast(1.0)
        } else {
            1.0
        }
    }

    private fun scaledLimit(limit: Long, scale: Double): Long {
        return Math.round(limit * scale).coerceAtLeast(1L)
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
