package com.tibiawiki.domain.wiki

import java.time.Duration
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * Per-instance budget for bulk `?expand=true` Fandom fetches. Cheap in-process
 * backpressure so a 1Gi Cloud Run instance cannot run unbounded concurrent
 * category expansions. Mapped to HTTP 503 when a permit cannot be acquired.
 */
class ExpandConcurrencyLimiter(
    maxConcurrent: Int,
    private val acquireTimeout: Duration
) {
    private val limit = maxConcurrent.coerceAtLeast(1)
    private val permits = Semaphore(limit, true)

    fun <T> withPermit(block: () -> T): T {
        val acquired = try {
            permits.tryAcquire(acquireTimeout.toMillis().coerceAtLeast(0), TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw WikiUnavailableException("expand interrupted while waiting for a concurrency permit", e)
        }
        if (!acquired) {
            throw WikiUnavailableException(
                "expand concurrency limit reached ($limit in flight)",
                retryable = true
            )
        }
        try {
            return block()
        } finally {
            permits.release()
        }
    }
}
