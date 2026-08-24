package com.tibiawiki.domain.wiki

import com.tibiawiki.config.WikiClientProperties
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.lessThan
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class WikiCallSupportTest {

    @Test
    fun retriesRetryableFailuresThenSucceeds() {
        val delays = mutableListOf<Duration>()
        val properties = WikiClientProperties().apply {
            retry.maxAttempts = 3
            retry.baseDelay = Duration.ofMillis(100)
            retry.maxDelay = Duration.ofSeconds(1)
            callTimeout = Duration.ZERO
        }
        WikiCallSupport(
            properties,
            sleeper = { delays.add(it) },
            random = { 0.5 }
        ).use { calls ->
            val attempts = AtomicInteger()
            val result = calls.call("getArticle") {
                if (attempts.incrementAndGet() < 3) {
                    throw IOException("flaky")
                }
                "ok"
            }
            assertThat(result, `is`("ok"))
            assertThat(attempts.get(), `is`(3))
            assertThat(delays.size, `is`(2))
            assertThat(delays[0], `is`(Duration.ofMillis(50)))
            assertThat(delays[1], `is`(Duration.ofMillis(100)))
        }
    }

    @Test
    fun doesNotRetryExpandCap() {
        val properties = WikiClientProperties().apply {
            retry.maxAttempts = 5
            callTimeout = Duration.ZERO
        }
        WikiCallSupport(properties, sleeper = { }).use { calls ->
            val attempts = AtomicInteger()
            assertThrows<ExpandTooLargeException> {
                calls.call("getArticles") {
                    attempts.incrementAndGet()
                    throw ExpandTooLargeException(9, 1)
                }
            }
            assertThat(attempts.get(), `is`(1))
        }
    }

    @Test
    fun timesOutLongCalls() {
        val properties = WikiClientProperties().apply {
            retry.maxAttempts = 1
            callTimeout = Duration.ofMillis(50)
        }
        WikiCallSupport(properties).use { calls ->
            val thrown = assertThrows<WikiUnavailableException> {
                calls.call<String>("slow") {
                    Thread.sleep(2_000)
                    "never"
                }
            }
            assertThat(thrown.message!!.contains("timed out"), `is`(true))
        }
    }

    @Test
    fun directModeDoesNotRetry() {
        val attempts = AtomicInteger()
        assertThrows<IOException> {
            WikiCallSupport.direct().call("x") {
                attempts.incrementAndGet()
                throw IOException("boom")
            }
        }
        assertThat(attempts.get(), `is`(1))
    }

    @Test
    fun jitterStaysAtOrBelowCap() {
        val properties = WikiClientProperties().apply {
            retry.maxAttempts = 2
            retry.baseDelay = Duration.ofMillis(200)
            retry.maxDelay = Duration.ofMillis(200)
            callTimeout = Duration.ZERO
        }
        val delays = mutableListOf<Duration>()
        WikiCallSupport(
            properties,
            sleeper = { delays.add(it) },
            random = { 0.99 }
        ).use { calls ->
            assertThrows<WikiUnavailableException> {
                calls.call("x") { throw IOException("still down") }
            }
        }
        assertThat(delays.size, `is`(1))
        assertThat(delays[0].toMillis(), lessThan(201L))
    }

    @Test
    fun ioPoolIsFixedAndBounded() {
        val properties = WikiClientProperties().apply {
            io.threads = 3
            io.queueCapacity = 7
        }
        WikiCallSupport(properties).use { calls ->
            val pool = calls.threadPoolExecutor()
            assertThat(pool.corePoolSize, `is`(3))
            assertThat(pool.maximumPoolSize, `is`(3))
            assertThat(pool.queue.remainingCapacity(), `is`(7))
        }
    }

    @Test
    fun closeShutsDownTheIoPool() {
        val calls = WikiCallSupport(WikiClientProperties())
        val pool = calls.threadPoolExecutor()
        assertThat(pool.isShutdown, `is`(false))
        calls.close()
        assertThat(pool.isShutdown, `is`(true))
        assertThat(pool.isTerminated, `is`(true))
    }

    @Test
    fun concurrentCallsDoNotCreateMoreThreadsThanThePoolSize() {
        val properties = WikiClientProperties().apply {
            io.threads = 2
            io.queueCapacity = 8
            retry.maxAttempts = 1
            callTimeout = Duration.ofSeconds(3)
        }
        val inFlight = CountDownLatch(2)
        val release = CountDownLatch(1)
        val thirdEntered = AtomicBoolean(false)
        WikiCallSupport(properties).use { calls ->
            val first = Thread {
                calls.call("a") {
                    inFlight.countDown()
                    release.await()
                    "ok"
                }
            }
            val second = Thread {
                calls.call("b") {
                    inFlight.countDown()
                    release.await()
                    "ok"
                }
            }
            first.start()
            second.start()
            try {
                assertThat(inFlight.await(2, TimeUnit.SECONDS), `is`(true))
                val third = Thread {
                    calls.call("c") {
                        thirdEntered.set(true)
                        "ok"
                    }
                }
                third.start()
                val queued = waitUntil { calls.threadPoolExecutor().queue.size == 1 }
                assertThat(queued, `is`(true))
                assertThat(thirdEntered.get(), `is`(false))
                assertThat(calls.threadPoolExecutor().activeCount, `is`(2))
                assertThat(calls.threadPoolExecutor().maximumPoolSize, `is`(2))
                release.countDown()
                first.join(2_000)
                second.join(2_000)
                third.join(2_000)
            } finally {
                release.countDown()
            }
        }
    }

    @Test
    fun saturatedPoolRejectsWithoutRetry() {
        val properties = WikiClientProperties().apply {
            io.threads = 1
            io.queueCapacity = 1
            retry.maxAttempts = 3
            callTimeout = Duration.ofSeconds(5)
        }
        val workerStarted = CountDownLatch(1)
        val hold = CountDownLatch(1)
        WikiCallSupport(properties).use { calls ->
            val worker = Thread {
                calls.call("a") {
                    workerStarted.countDown()
                    hold.await()
                    "1"
                }
            }
            worker.start()
            try {
                assertThat(workerStarted.await(2, TimeUnit.SECONDS), `is`(true))
                val queued = Thread {
                    calls.call("b") { "2" }
                }
                queued.start()
                assertThat(waitUntil { calls.threadPoolExecutor().queue.size == 1 }, `is`(true))
                val thrown = assertThrows<WikiUnavailableException> {
                    calls.call("c") { "3" }
                }
                assertThat(thrown.message!!.contains("saturated"), `is`(true))
                assertThat(thrown.retryable, `is`(false))
                hold.countDown()
                worker.join(2_000)
                queued.join(2_000)
            } finally {
                hold.countDown()
            }
        }
    }

    @Test
    fun poolRejectionIsNotRetryable() {
        val rejected = RejectedExecutionException("full")
        assertThat(WikiCallSupport.isRetryable(rejected), `is`(false))
        val wrapped = WikiCallSupport.wrapIfNeeded("getArticles", rejected)
        assertThat((wrapped as WikiUnavailableException).retryable, `is`(false))
        assertThat(wrapped.message!!.contains("saturated"), `is`(true))
    }

    private fun waitUntil(timeoutMs: Long = 2_000, condition: () -> Boolean): Boolean {
        val deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs)
        while (System.nanoTime() < deadline) {
            if (condition()) {
                return true
            }
            Thread.sleep(10)
        }
        return condition()
    }
}
