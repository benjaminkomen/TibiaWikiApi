package com.tibiawiki.config

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicLong

class TokenBucketTest {

    @Test
    fun allowsUpToCapacityThenRejectsWithoutRefill() {
        val clock = AtomicLong(0L)
        val bucket = TokenBucket(capacity = 2.0, refillPerSecond = 0.0, clockNanos = { clock.get() })

        assertThat(bucket.tryConsume().allowed, `is`(true))
        assertThat(bucket.tryConsume().allowed, `is`(true))
        val rejected = bucket.tryConsume()
        assertThat(rejected.allowed, `is`(false))
        assertThat(rejected.remaining, `is`(0.0))
    }

    @Test
    fun refillsOverTime() {
        val clock = AtomicLong(0L)
        val bucket = TokenBucket(capacity = 2.0, refillPerSecond = 1.0, clockNanos = { clock.get() })

        assertThat(bucket.tryConsume().allowed, `is`(true))
        assertThat(bucket.tryConsume().allowed, `is`(true))
        assertThat(bucket.tryConsume().allowed, `is`(false))

        clock.addAndGet(1_000_000_000L)
        val afterRefill = bucket.tryConsume()
        assertThat(afterRefill.allowed, `is`(true))
        assertThat(afterRefill.remaining, `is`(0.0))
    }

    @Test
    fun retryAfterSecondsReflectsDeficitAtHalfTokenPerSecond() {
        val clock = AtomicLong(0L)
        val bucket = TokenBucket(capacity = 1.0, refillPerSecond = 0.5, clockNanos = { clock.get() })

        assertThat(bucket.tryConsume().allowed, `is`(true))
        val rejected = bucket.tryConsume()
        assertThat(rejected.allowed, `is`(false))
        assertThat(rejected.retryAfterSeconds, `is`(2))
    }

    @Test
    fun doesNotExceedCapacityOnRefill() {
        val clock = AtomicLong(0L)
        val bucket = TokenBucket(capacity = 3.0, refillPerSecond = 100.0, clockNanos = { clock.get() })
        clock.addAndGet(60_000_000_000L)
        assertThat(bucket.remaining(), `is`(3.0))
        assertThat(bucket.remaining(), lessThanOrEqualTo(3.0))
        assertThat(bucket.remaining(), greaterThanOrEqualTo(3.0))
    }
}
