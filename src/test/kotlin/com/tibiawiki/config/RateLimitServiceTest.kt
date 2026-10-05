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

    @Test
    fun unattributedKeyUsesScaledSharedBucket() {
        val properties = RateLimitProperties().apply {
            capacity = 2.0
            refillPerSecond = 0.0
            unattributedMultiplier = 3.0
        }
        val service = RateLimitService(properties)

        val unattributed = (1..7).map { service.tryConsume(ClientIpKeyResolver.UNATTRIBUTED_KEY, expand = false).allowed }
        assertThat(unattributed, `is`(listOf(true, true, true, true, true, true, false)))

        val normal = (1..3).map { service.tryConsume("203.0.113.7", expand = false).allowed }
        assertThat(normal, `is`(listOf(true, true, false)))
    }

    @Test
    fun unattributedMultiplierBelowOneDoesNotShrinkTheBucket() {
        val properties = RateLimitProperties().apply {
            capacity = 2.0
            refillPerSecond = 0.0
            unattributedMultiplier = 0.0
        }
        val service = RateLimitService(properties)

        val allowed = (1..3).map { service.tryConsume(ClientIpKeyResolver.UNATTRIBUTED_KEY, expand = false).allowed }
        assertThat(allowed, `is`(listOf(true, true, false)))
    }

    @Test
    fun unattributedVerdictReportsScaledLimit() {
        val properties = RateLimitProperties().apply { unattributedMultiplier = 5.0 }
        val service = RateLimitService(properties)

        assertThat(service.tryConsume(ClientIpKeyResolver.UNATTRIBUTED_KEY, expand = false).limit, `is`(150L))
        assertThat(service.tryConsume("203.0.113.8", expand = false).limit, `is`(30L))
    }
}
