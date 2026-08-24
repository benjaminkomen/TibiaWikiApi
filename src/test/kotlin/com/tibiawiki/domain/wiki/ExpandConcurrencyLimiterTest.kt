package com.tibiawiki.domain.wiki

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ExpandConcurrencyLimiterTest {

    @Test
    fun secondCallerTimesOutWhilePermitIsHeld() {
        val limiter = ExpandConcurrencyLimiter(1, Duration.ofMillis(80))
        val hold = CountDownLatch(1)
        val started = CountDownLatch(1)
        val worker = Thread {
            limiter.withPermit {
                started.countDown()
                hold.await()
                "ok"
            }
        }
        worker.start()
        try {
            assertThat(started.await(2, TimeUnit.SECONDS), `is`(true))
            val thrown = assertThrows<WikiUnavailableException> {
                limiter.withPermit { "nope" }
            }
            assertThat(thrown.message!!.contains("concurrency limit"), `is`(true))
            assertThat(thrown.retryable, `is`(true))
        } finally {
            hold.countDown()
            worker.join(2_000)
        }
    }

    @Test
    fun permitIsReleasedForTheNextCaller() {
        val limiter = ExpandConcurrencyLimiter(1, Duration.ofSeconds(1))
        val calls = AtomicInteger()
        assertThat(limiter.withPermit { calls.incrementAndGet() }, `is`(1))
        assertThat(limiter.withPermit { calls.incrementAndGet() }, `is`(2))
    }
}
