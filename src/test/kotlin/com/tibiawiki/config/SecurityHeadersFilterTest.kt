package com.tibiawiki.config

import jakarta.servlet.FilterChain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.core.Ordered
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class SecurityHeadersFilterTest {

    @Test
    fun setsBrowserSecurityHeadersOnEveryResponse() {
        val chain = mock(FilterChain::class.java)
        val request = MockHttpServletRequest("GET", "/api/corpses")
        val response = MockHttpServletResponse()

        SecurityHeadersFilter().doFilter(request, response, chain)

        verify(chain).doFilter(request, response)
        assertThat(
            response.getHeader(SecurityHeadersFilter.CONTENT_TYPE_OPTIONS),
            `is`(SecurityHeadersFilter.CONTENT_TYPE_OPTIONS_VALUE)
        )
        assertThat(
            response.getHeader(SecurityHeadersFilter.REFERRER_POLICY),
            `is`(SecurityHeadersFilter.REFERRER_POLICY_VALUE)
        )
        assertThat(
            response.getHeader(SecurityHeadersFilter.PERMISSIONS_POLICY),
            `is`(SecurityHeadersFilter.PERMISSIONS_POLICY_VALUE)
        )
    }

    @Test
    fun runsBeforeUnorderedWriteFilter() {
        assertThat(SecurityHeadersFilter().order < Ordered.LOWEST_PRECEDENCE, `is`(true))
    }
}
