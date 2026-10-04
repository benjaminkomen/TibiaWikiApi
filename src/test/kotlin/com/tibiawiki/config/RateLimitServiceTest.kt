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
            "2001:db8:abcd:12:1111:2222:3333:4444",
            "2001:0db8:abcd:0012:aaaa:bbbb:cccc:dddd",
            "2001:db8:abcd:12::1"
        ).map { service.tryConsume(resolver.resolveKey(it), expand = false).allowed }

        assertThat(allowed, `is`(listOf(true, true, false)))
        assertThat(service.tryConsume(resolver.resolveKey("2001:db8:abcd:13::1"), expand = false).allowed, `is`(true))
    }
}
