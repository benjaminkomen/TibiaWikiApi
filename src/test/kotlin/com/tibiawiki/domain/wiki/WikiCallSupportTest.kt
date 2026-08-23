package com.tibiawiki.domain.wiki

import com.tibiawiki.config.WikiClientProperties
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.lessThan
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.time.Duration
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
                calls.call("slow") {
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
}
