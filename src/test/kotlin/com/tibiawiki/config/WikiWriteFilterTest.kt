package com.tibiawiki.config

import jakarta.servlet.FilterChain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class WikiWriteFilterTest {

    @Test
    fun getRequestsAreNotFiltered() {
        val chain = mock(FilterChain::class.java)
        val request = MockHttpServletRequest("GET", "/api/creatures")
        val response = MockHttpServletResponse()

        WikiWriteFilter(false, "").doFilter(request, response, chain)

        verify(chain).doFilter(request, response)
        assertThat(response.status, `is`(HttpStatus.OK.value()))
    }

    @Test
    fun putOutsideApiIsNotFiltered() {
        val chain = mock(FilterChain::class.java)
        val request = MockHttpServletRequest("PUT", "/swagger-ui/index.html")
        val response = MockHttpServletResponse()

        WikiWriteFilter(false, "").doFilter(request, response, chain)

        verify(chain).doFilter(request, response)
    }

    @Test
    fun putIsForbiddenWhenWritesAreDisabled() {
        val chain = mock(FilterChain::class.java)
        val request = MockHttpServletRequest("PUT", "/api/creatures")
        val response = MockHttpServletResponse()

        WikiWriteFilter(false, "secret").doFilter(request, response, chain)

        verify(chain, never()).doFilter(request, response)
        assertThat(response.status, `is`(HttpStatus.FORBIDDEN.value()))
        assertThat(response.contentAsString.contains("Wiki writes are disabled"), `is`(true))
    }

    @Test
    fun putIsAllowedWhenWritesAreEnabledAndNoTokenIsConfigured() {
        val chain = mock(FilterChain::class.java)
        val request = MockHttpServletRequest("PUT", "/api/creatures")
        val response = MockHttpServletResponse()

        WikiWriteFilter(true, "").doFilter(request, response, chain)

        verify(chain).doFilter(request, response)
        assertThat(response.status, `is`(HttpStatus.OK.value()))
    }

    @Test
    fun putIsUnauthorizedWhenTokenIsMissing() {
        val chain = mock(FilterChain::class.java)
        val request = MockHttpServletRequest("PUT", "/api/items")
        val response = MockHttpServletResponse()

        WikiWriteFilter(true, "secret").doFilter(request, response, chain)

        verify(chain, never()).doFilter(request, response)
        assertThat(response.status, `is`(HttpStatus.UNAUTHORIZED.value()))
        assertThat(response.getHeader(HttpHeaders.WWW_AUTHENTICATE), `is`("Bearer"))
    }

    @Test
    fun putIsUnauthorizedWhenTokenDoesNotMatch() {
        val chain = mock(FilterChain::class.java)
        val request = MockHttpServletRequest("PUT", "/api/items")
        request.addHeader(WikiWriteFilter.TOKEN_HEADER, "wrong")
        val response = MockHttpServletResponse()

        WikiWriteFilter(true, "secret").doFilter(request, response, chain)

        verify(chain, never()).doFilter(request, response)
        assertThat(response.status, `is`(HttpStatus.UNAUTHORIZED.value()))
    }

    @Test
    fun putIsAllowedWhenWriteTokenHeaderMatches() {
        val chain = mock(FilterChain::class.java)
        val request = MockHttpServletRequest("PUT", "/api/books")
        request.addHeader(WikiWriteFilter.TOKEN_HEADER, "secret")
        val response = MockHttpServletResponse()

        WikiWriteFilter(true, "secret").doFilter(request, response, chain)

        verify(chain).doFilter(request, response)
    }

    @Test
    fun putIsAllowedWhenBearerTokenMatches() {
        val chain = mock(FilterChain::class.java)
        val request = MockHttpServletRequest("PUT", "/api/books")
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer secret")
        val response = MockHttpServletResponse()

        WikiWriteFilter(true, "secret").doFilter(request, response, chain)

        verify(chain).doFilter(request, response)
    }
}
