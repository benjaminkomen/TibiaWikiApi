package com.tibiawiki.config

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.nullValue
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.cors.CorsConfiguration

class CORSResponseFilterTest {

    @Test
    fun defaultConfigurationAllowsGetFromUiOriginsWithoutCredentials() {
        val config = CORSResponseFilter().corsConfiguration()

        assertThat(config.allowCredentials, `is`(false))
        assertThat(config.allowedMethods, contains("GET", "HEAD", "OPTIONS"))
        assertThat(
            config.allowedOrigins,
            contains("https://tibiawiki.dev", "http://localhost:8080", "http://127.0.0.1:8080")
        )
        assertThat(config.allowedMethods!!.contains("PUT"), `is`(false))
        assertThat(config.allowedMethods!!.contains("DELETE"), `is`(false))
    }

    @Test
    fun starOriginUsesAllowedOriginsWithoutPatterns() {
        val config = CorsConfiguration()
        CORSResponseFilter.applyOrigins(config, "*")

        assertThat(config.allowedOrigins, contains("*"))
        assertThat(config.allowedOriginPatterns, `is`(nullValue()))
    }

    @Test
    fun wildcardEntriesUseOriginPatterns() {
        val config = CorsConfiguration()
        CORSResponseFilter.applyOrigins(config, "http://localhost:*")

        assertThat(config.allowedOriginPatterns, contains("http://localhost:*"))
        assertThat(config.allowedOrigins, `is`(nullValue()))
    }

    @Test
    fun unknownOriginGetIsServedWithoutCorsHeaders() {
        val config = CORSResponseFilter().corsConfiguration()
        val request = MockHttpServletRequest("GET", "/api/corpses")
        request.addHeader(HttpHeaders.ORIGIN, "https://evil.example")
        val response = MockHttpServletResponse()

        val allowed = AllowUnknownOriginGetProcessor().processRequest(config, request, response)

        assertThat(allowed, `is`(true))
        assertThat(response.status, `is`(HttpStatus.OK.value()))
        assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN), `is`(nullValue()))
    }

    @Test
    fun unknownOriginPreflightIsRejected() {
        val config = CORSResponseFilter().corsConfiguration()
        val request = MockHttpServletRequest("OPTIONS", "/api/corpses")
        request.addHeader(HttpHeaders.ORIGIN, "https://evil.example")
        request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
        val response = MockHttpServletResponse()

        val allowed = AllowUnknownOriginGetProcessor().processRequest(config, request, response)

        assertThat(allowed, `is`(false))
        assertThat(response.status, `is`(HttpStatus.FORBIDDEN.value()))
    }
}
