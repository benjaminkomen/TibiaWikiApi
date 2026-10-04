package com.tibiawiki.config

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
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

    @Test
    fun compressedAndExpandedFormsInOneSlash64ShareKey() {
        val keys = listOf(
            "2804:7f0:84a2:57a8:17b:399d:f0d8:4db9",
            "2804:07f0:84a2:57a8:a07e:5ce9:46db:233c",
            "2804:7f0:84a2:57a8::1",
            "2804:07F0:84A2:57A8:0000:0000:0000:0001",
            "[2804:7f0:84a2:57a8::2]"
        ).map { resolver.resolveKey(it) }

        assertThat(keys.toSet(), `is`(setOf("2804:7f0:84a2:57a8:0:0:0:0/64")))
    }

    @Test
    fun hostnamesAreNotResolved() {
        assertThat(resolver.resolveKey("localhost"), `is`(ClientIpKeyResolver.UNKNOWN_KEY))
        assertThat(resolver.resolveKey("example.com"), `is`(ClientIpKeyResolver.UNKNOWN_KEY))
    }

    @Test
    fun clientAddressUsesRightmostForwardedForEntry() {
        val address = resolver.clientAddress("169.254.1.1", listOf("192.0.2.1, 192.0.2.2, 198.51.100.20"), 1)
        assertThat(address, `is`("198.51.100.20"))
    }

    @Test
    fun clientAddressJoinsRepeatedForwardedForHeadersInOrder() {
        val address = resolver.clientAddress("169.254.1.1", listOf("192.0.2.1", "192.0.2.2,198.51.100.20"), 1)
        assertThat(address, `is`("198.51.100.20"))
    }

    @Test
    fun clientAddressSkipsTrustedProxyHops() {
        val address = resolver.clientAddress("169.254.1.1", listOf("192.0.2.1, 198.51.100.20, 203.0.113.99"), 2)
        assertThat(address, `is`("198.51.100.20"))
    }

    @Test
    fun clientAddressUsesLeftmostEntryWhenHeaderIsShorterThanHops() {
        val address = resolver.clientAddress("169.254.1.1", listOf("198.51.100.20"), 2)
        assertThat(address, `is`("198.51.100.20"))
    }

    @Test
    fun clientAddressFallsBackToSocketAddress() {
        assertThat(resolver.clientAddress("203.0.113.5", emptyList(), 1), `is`("203.0.113.5"))
        assertThat(resolver.clientAddress("203.0.113.5", listOf(" , "), 1), `is`("203.0.113.5"))
        assertThat(resolver.clientAddress("203.0.113.5", listOf("198.51.100.20"), 0), `is`("203.0.113.5"))
        assertThat(resolver.clientAddress(null, emptyList(), 1), nullValue())
    }
}
