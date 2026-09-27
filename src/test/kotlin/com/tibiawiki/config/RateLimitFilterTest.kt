package com.tibiawiki.config

import jakarta.servlet.FilterChain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class RateLimitFilterTest {

    @Test
    fun disabledFilterPassesThroughWithoutHeaders() {
        val filter = filter(enabled = false, capacity = 1.0, refill = 0.0)
        val chain = mock(FilterChain::class.java)
        val request = MockHttpServletRequest("GET", "/api/creatures")
        request.remoteAddr = "203.0.113.1"
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, chain)

        verify(chain).doFilter(request, response)
        assertThat(response.getHeader(RateLimitFilter.HEADER_LIMIT), nullValue())
    }

    @Test
    fun postIsNotRateLimited() {
        val filter = filter(enabled = true, capacity = 1.0, refill = 0.0)
        val chain = mock(FilterChain::class.java)
        val request = MockHttpServletRequest("POST", "/api/creatures")
        request.remoteAddr = "203.0.113.1"
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, chain)

        verify(chain).doFilter(request, response)
    }

    @Test
    fun allowsThenReturns429WithRetryAfterAndJsonBody() {
        val filter = filter(enabled = true, capacity = 2.0, refill = 0.5)
        val chain = mock(FilterChain::class.java)
        val remote = "203.0.113.9"

        repeat(2) {
            val request = MockHttpServletRequest("GET", "/api/items")
            request.remoteAddr = remote
            val response = MockHttpServletResponse()
            filter.doFilter(request, response, chain)
            assertThat(response.status, `is`(HttpStatus.OK.value()))
            assertThat(response.getHeader(RateLimitFilter.HEADER_LIMIT), `is`("30"))
            assertThat(response.getHeader(RateLimitFilter.HEADER_REMAINING), notNullValue())
            assertThat(response.getHeader(RateLimitFilter.HEADER_RESET), notNullValue())
        }

        val limitedRequest = MockHttpServletRequest("GET", "/api/items")
        limitedRequest.remoteAddr = remote
        limitedRequest.addHeader("User-Agent", "Go-http-client/2.0")
        val limitedResponse = MockHttpServletResponse()
        filter.doFilter(limitedRequest, limitedResponse, chain)

        verify(chain, times(2)).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())
        verify(chain, never()).doFilter(limitedRequest, limitedResponse)
        assertThat(limitedResponse.status, `is`(HttpStatus.TOO_MANY_REQUESTS.value()))
        assertThat(limitedResponse.getHeader(RateLimitFilter.HEADER_RETRY_AFTER), `is`("2"))
        assertThat(limitedResponse.getHeader(RateLimitFilter.HEADER_REMAINING), `is`("0"))
        assertThat(limitedResponse.contentAsString, containsString("rate_limited"))
        val body = JSONObject(limitedResponse.contentAsString)
        assertThat(body.getString("error"), `is`("rate_limited"))
        assertThat(body.getString("message"), containsString("slow down"))
        assertThat(body.getInt("retryAfterSeconds"), `is`(2))
    }

    @Test
    fun ipv6NeighborsShareTheSameBucket() {
        val filter = filter(enabled = true, capacity = 1.0, refill = 0.0)
        val chain = mock(FilterChain::class.java)

        val first = MockHttpServletRequest("GET", "/api/creatures")
        first.remoteAddr = "2001:db8:abcd:12:1111:2222:3333:4444"
        val firstResponse = MockHttpServletResponse()
        filter.doFilter(first, firstResponse, chain)
        assertThat(firstResponse.status, `is`(HttpStatus.OK.value()))

        val second = MockHttpServletRequest("GET", "/api/creatures")
        second.remoteAddr = "2001:db8:abcd:12:aaaa:bbbb:cccc:dddd"
        val secondResponse = MockHttpServletResponse()
        filter.doFilter(second, secondResponse, chain)

        assertThat(secondResponse.status, `is`(HttpStatus.TOO_MANY_REQUESTS.value()))
        assertThat(secondResponse.getHeader(RateLimitFilter.HEADER_RETRY_AFTER), notNullValue())
    }

    @Test
    fun differentIpv4AddressesHaveIndependentBuckets() {
        val filter = filter(enabled = true, capacity = 1.0, refill = 0.0)
        val chain = mock(FilterChain::class.java)

        val a = MockHttpServletRequest("GET", "/api/creatures")
        a.remoteAddr = "203.0.113.1"
        filter.doFilter(a, MockHttpServletResponse(), chain)

        val b = MockHttpServletRequest("GET", "/api/creatures")
        b.remoteAddr = "203.0.113.2"
        val bResponse = MockHttpServletResponse()
        filter.doFilter(b, bResponse, chain)

        assertThat(bResponse.status, `is`(HttpStatus.OK.value()))
        verify(chain, times(2)).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())
    }

    @Test
    fun expandQueryHitsTighterExpandBucket() {
        val properties = RateLimitProperties().apply {
            enabled = true
            capacity = 20.0
            refillPerSecond = 0.5
            expandCapacity = 1.0
            expandRefillPerSecond = 0.0833
        }
        val filter = RateLimitFilter(properties, ClientIpKeyResolver(), RateLimitService(properties))
        val chain = mock(FilterChain::class.java)
        val remote = "198.51.100.7"

        val first = MockHttpServletRequest("GET", "/api/creatures")
        first.remoteAddr = remote
        first.setParameter("expand", "true")
        filter.doFilter(first, MockHttpServletResponse(), chain)

        val second = MockHttpServletRequest("GET", "/api/creatures")
        second.remoteAddr = remote
        second.setParameter("expand", "true")
        val secondResponse = MockHttpServletResponse()
        filter.doFilter(second, secondResponse, chain)

        assertThat(secondResponse.status, `is`(HttpStatus.TOO_MANY_REQUESTS.value()))
        assertThat(secondResponse.getHeader(RateLimitFilter.HEADER_LIMIT), `is`("5"))
    }

    private fun filter(enabled: Boolean, capacity: Double, refill: Double): RateLimitFilter {
        val properties = RateLimitProperties().apply {
            this.enabled = enabled
            this.capacity = capacity
            this.refillPerSecond = refill
        }
        return RateLimitFilter(properties, ClientIpKeyResolver(), RateLimitService(properties))
    }
}
