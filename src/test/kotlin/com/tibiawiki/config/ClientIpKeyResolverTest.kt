package com.tibiawiki.config

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class ClientIpKeyResolverTest {

    private val resolver = ClientIpKeyResolver()

    @Test
    fun ipv4UsesFullAddress() {
        assertThat(resolver.resolveKey("203.0.113.10"), `is`("203.0.113.10"))
    }

    @Test
    fun ipv6NeighborsShareSlash64Prefix() {
        val a = resolver.resolveKey("2001:db8:abcd:12:1111:2222:3333:4444")
        val b = resolver.resolveKey("2001:db8:abcd:12:aaaa:bbbb:cccc:dddd")
        assertThat(a, `is`("2001:db8:abcd:12:0:0:0:0/64"))
        assertThat(b, `is`(a))
    }

    @Test
    fun differentSlash64PrefixesGetDifferentKeys() {
        val a = resolver.resolveKey("2001:db8:abcd:12::1")
        val b = resolver.resolveKey("2001:db8:abcd:13::1")
        assertThat(a == b, `is`(false))
    }

    @Test
    fun stripsZoneIdBeforeKeying() {
        val key = resolver.resolveKey("fe80::1%eth0")
        assertThat(key.endsWith("/64"), `is`(true))
        assertThat(key.contains('%'), `is`(false))
    }

    @Test
    fun blankOrInvalidBecomesUnknown() {
        assertThat(resolver.resolveKey(null), `is`(ClientIpKeyResolver.UNKNOWN_KEY))
        assertThat(resolver.resolveKey("   "), `is`(ClientIpKeyResolver.UNKNOWN_KEY))
        assertThat(resolver.resolveKey("not-an-ip"), `is`(ClientIpKeyResolver.UNKNOWN_KEY))
    }

    @Test
    fun ipv4MappedIpv6UsesIpv4Address() {
        assertThat(resolver.resolveKey("::ffff:203.0.113.50"), `is`("203.0.113.50"))
    }
}
