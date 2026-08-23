package com.tibiawiki.config

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.nullValue
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
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
}
