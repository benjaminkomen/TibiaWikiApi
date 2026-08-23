package com.tibiawiki.domain.wiki

import com.tibiawiki.config.WikiClientProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import java.io.IOException
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.math.min

/**
 * Timeouts and retries with full jitter around jwiki I/O. jwiki's OkHttp client
 * hardcodes a 2-minute read timeout and does not accept a custom client, so
 * the deadline is enforced here.
 */
class WikiCallSupport(
    private val properties: WikiClientProperties,
    private val enabled: Boolean = true,
    private val sleeper: (Duration) -> Unit = { delay -> Thread.sleep(delay.toMillis()) },
    private val random: () -> Double = { ThreadLocalRandom.current().nextDouble() }
) : DisposableBean, AutoCloseable {

    private val executor: ExecutorService? = if (enabled) {
        Executors.newCachedThreadPool { runnable ->
            Thread(runnable, "wiki-io").apply { isDaemon = true }
        }
    } else {
        null
    }

    fun <T> call(operation: String, block: () -> T): T {
        if (!enabled) {
            return block()
        }
        val maxAttempts = properties.retry.maxAttempts.coerceAtLeast(1)
        var lastError: Throwable? = null
        for (attempt in 0 until maxAttempts) {
            try {
                return invokeWithTimeout(operation, block)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw WikiUnavailableException("$operation interrupted", e)
            } catch (e: Throwable) {
                if (!isRetryable(e) || attempt == maxAttempts - 1) {
                    throw wrapIfNeeded(operation, e)
                }
                lastError = e
                val delay = jitterDelay(attempt)
                LOG.warn(
                    "{} failed (attempt {}/{}), retrying in {}: {}",
                    operation,
                    attempt + 1,
                    maxAttempts,
                    delay,
                    e.toString()
                )
                sleeper(delay)
            }
        }
        throw wrapIfNeeded(operation, lastError ?: IllegalStateException("retry loop exhausted"))
    }

    override fun destroy() {
        close()
    }

    override fun close() {
        executor?.shutdownNow()
    }

    private fun <T> invokeWithTimeout(operation: String, block: () -> T): T {
        val timeout = properties.callTimeout
        if (timeout.isZero || timeout.isNegative) {
            return block()
        }
        val future = executor!!.submit(Callable { block() })
        try {
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS)
        } catch (e: TimeoutException) {
            future.cancel(true)
            throw WikiUnavailableException("$operation timed out after $timeout", e)
        } catch (e: ExecutionException) {
            throw e.cause ?: e
        } catch (e: InterruptedException) {
            future.cancel(true)
            Thread.currentThread().interrupt()
            throw e
        }
    }

    private fun jitterDelay(attempt: Int): Duration {
        val baseMs = properties.retry.baseDelay.toMillis().coerceAtLeast(0)
        val maxMs = properties.retry.maxDelay.toMillis().coerceAtLeast(baseMs)
        val exponential = baseMs * (1L shl attempt.coerceAtMost(16))
        val cap = min(maxMs, exponential)
        val jittered = (random() * cap).toLong().coerceAtLeast(0)
        return Duration.ofMillis(jittered)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(WikiCallSupport::class.java)

        fun direct(): WikiCallSupport {
            return WikiCallSupport(WikiClientProperties(), enabled = false)
        }

        fun isRetryable(throwable: Throwable): Boolean {
            var current: Throwable? = throwable
            val seen = HashSet<Throwable>()
            while (current != null && seen.add(current)) {
                when (current) {
                    is ExpandTooLargeException -> return false
                    is InterruptedException -> return false
                    is TimeoutException -> return true
                    is IOException -> return true
                    is WikiUnavailableException -> return current.retryable
                }
                current = current.cause
            }
            return false
        }

        fun wrapIfNeeded(operation: String, error: Throwable): RuntimeException {
            return when (error) {
                is ExpandTooLargeException -> error
                is WikiUnavailableException -> error
                is RuntimeException -> WikiUnavailableException("$operation failed", error)
                else -> WikiUnavailableException("$operation failed", error)
            }
        }
    }
}
