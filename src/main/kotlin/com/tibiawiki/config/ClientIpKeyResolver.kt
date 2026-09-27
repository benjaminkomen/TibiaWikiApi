package com.tibiawiki.config

import org.springframework.stereotype.Component
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.util.Locale

/**
 * Builds a rate-limit key from a client address. IPv4 uses the full address;
 * IPv6 collapses to the `/64` prefix so privacy-address rotation within one
 * household/ISP assignment shares one bucket.
 */
@Component
class ClientIpKeyResolver {

    fun resolveKey(remoteAddr: String?): String {
        val trimmed = remoteAddr?.trim()?.takeIf { it.isNotEmpty() } ?: return UNKNOWN_KEY
        val withoutZone = trimmed.substringBefore('%')
        return try {
            when (val address = InetAddress.getByName(withoutZone)) {
                is Inet4Address -> address.hostAddress
                is Inet6Address -> keyForIpv6(address)
                else -> UNKNOWN_KEY
            }
        } catch (_: Exception) {
            UNKNOWN_KEY
        }
    }

    private fun keyForIpv6(address: Inet6Address): String {
        if (address.isIPv4CompatibleAddress || isIpv4Mapped(address)) {
            val v4 = extractIpv4(address) ?: return UNKNOWN_KEY
            return v4.hostAddress
        }
        val bytes = address.address
        val masked = ByteArray(IPV6_LENGTH)
        System.arraycopy(bytes, 0, masked, 0, IPV6_PREFIX_BYTES)
        val prefix = InetAddress.getByAddress(masked).hostAddress.lowercase(Locale.ROOT)
        return "$prefix/64"
    }

    private fun isIpv4Mapped(address: Inet6Address): Boolean {
        val bytes = address.address
        for (i in 0 until 10) {
            if (bytes[i].toInt() != 0) {
                return false
            }
        }
        return bytes[10] == 0xff.toByte() && bytes[11] == 0xff.toByte()
    }

    private fun extractIpv4(address: Inet6Address): Inet4Address? {
        val bytes = address.address
        val v4 = byteArrayOf(bytes[12], bytes[13], bytes[14], bytes[15])
        return try {
            InetAddress.getByAddress(v4) as? Inet4Address
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        const val UNKNOWN_KEY = "unknown"
        private const val IPV6_LENGTH = 16
        private const val IPV6_PREFIX_BYTES = 8
    }
}
