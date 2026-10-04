package com.tibiawiki.config

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class RateLimitServiceTest {

    private val resolver = ClientIpKeyResolver()

    @Test
    fun addressesInOneIpv6Slash64ShareOneBucket() {
        val properties = RateLimitProperties().apply {
            capacity = 2.0
            refillPerSecond = 0.0
        }
        val service = RateLimitService(properties)

        val allowed = listOf(
            "2804:7f0:84a2:57a8:17b:399d:f0d8:4db9",
            "2804:07f0:84a2:57a8:a07e:5ce9:46db:233c",
            "2804:7f0:84a2:57a8::1"
        ).map { service.tryConsume(resolver.resolveKey(it), expand = false).allowed }

        assertThat(allowed, `is`(listOf(true, true, false)))
        assertThat(service.tryConsume(resolver.resolveKey("2804:7f0:84a2:57a9::1"), expand = false).allowed, `is`(true))
    }
}
