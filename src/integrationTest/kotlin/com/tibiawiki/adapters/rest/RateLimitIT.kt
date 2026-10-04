package com.tibiawiki.adapters.rest

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.json.JSONObject
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Fixtures-profile verification that RATE_LIMIT_ENABLED with a tiny capacity
 * allows then returns 429 + Retry-After on GET /api paths, while actuator stays open.
 *
 * The app keeps `server.forward-headers-strategy=framework`, so these requests go
 * through Spring's real `ForwardedHeaderFilter`. Forwarded-header tests send what
 * Cloud Run's front end forwards: client-supplied entries first, then the client
 * IP that the platform appends (issue #502).
 */
@Tag("fixtures")
@ExtendWith(SpringExtension::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "rate-limit.enabled=true",
        "rate-limit.capacity=2",
        "rate-limit.refill-per-second=0",
        "rate-limit.expand-capacity=2",
        "rate-limit.expand-refill-per-second=0"
    ]
)
@ActiveProfiles("fixtures")
@AutoConfigureTestRestTemplate
class RateLimitIT(
    @Autowired private val restTemplate: TestRestTemplate
) {

    @Test
    fun apiAllowsBurstThenReturns429WithRetryAfter() {
        val first = restTemplate.getForEntity("/api/creatures/Dragon", String::class.java)
        assertThat(first.statusCode, `is`(HttpStatus.OK))
        assertThat(first.headers.getFirst("X-RateLimit-Limit"), notNullValue())

        val second = restTemplate.getForEntity("/api/creatures/Dragon", String::class.java)
        assertThat(second.statusCode, `is`(HttpStatus.OK))

        val limited = restTemplate.getForEntity("/api/creatures/Dragon", String::class.java)
        assertThat(limited.statusCode, `is`(HttpStatus.TOO_MANY_REQUESTS))
        assertThat(limited.headers.getFirst("Retry-After"), notNullValue())
        assertThat(limited.headers.getFirst("X-RateLimit-Remaining"), `is`("0"))
        val body = JSONObject(limited.body)
        assertThat(body.getString("error"), `is`("rate_limited"))
        assertThat(body.getString("message"), containsString("slow down"))
    }

    @Test
    fun actuatorHealthIsNotRateLimited() {
        repeat(5) {
            val health = restTemplate.getForEntity("/actuator/health", String::class.java)
            assertThat(health.statusCode, `is`(HttpStatus.OK))
        }
    }

    @Test
    fun spoofedLeftmostForwardedForDoesNotGetFreshBuckets() {
        val statuses = (1..3).map { i ->
            getWithHeaders("X-Forwarded-For" to "192.0.2.$i, 198.51.100.20").statusCode
        }

        assertThat(statuses, `is`(listOf(HttpStatus.OK, HttpStatus.OK, HttpStatus.TOO_MANY_REQUESTS)))
    }

    @Test
    fun spoofedForwardedHeaderDoesNotGetFreshBuckets() {
        val statuses = (1..3).map { i ->
            getWithHeaders(
                "Forwarded" to "for=192.0.2.${10 + i}",
                "X-Forwarded-For" to "198.51.100.21"
            ).statusCode
        }

        assertThat(statuses, `is`(listOf(HttpStatus.OK, HttpStatus.OK, HttpStatus.TOO_MANY_REQUESTS)))
    }

    @Test
    fun differentPlatformClientIpsHaveIndependentBuckets() {
        repeat(2) {
            getWithHeaders("X-Forwarded-For" to "198.51.100.30")
        }

        val other = getWithHeaders("X-Forwarded-For" to "198.51.100.31")

        assertThat(other.statusCode, `is`(HttpStatus.OK))
    }

    @Test
    fun ipv6AddressesInOneSlash64ShareBucketThroughForwardedFor() {
        val statuses = listOf(
            "2001:db8:abcd:12:1111:2222:3333:4444",
            "2001:0db8:abcd:0012:aaaa:bbbb:cccc:dddd",
            "2001:db8:abcd:12::1"
        ).map { address -> getWithHeaders("X-Forwarded-For" to address).statusCode }

        assertThat(statuses, `is`(listOf(HttpStatus.OK, HttpStatus.OK, HttpStatus.TOO_MANY_REQUESTS)))
    }

    private fun getWithHeaders(vararg headers: Pair<String, String>): ResponseEntity<String> {
        val httpHeaders = HttpHeaders()
        headers.forEach { (name, value) -> httpHeaders.add(name, value) }
        return restTemplate.exchange(
            "/api/creatures/Dragon",
            HttpMethod.GET,
            HttpEntity<Void>(httpHeaders),
            String::class.java
        )
    }
}
