package com.tibiawiki.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CORSResponseFilter(
    @param:Value("\${wiki.cors.allowed-origins:https://tibiawiki.dev,http://localhost:8080,http://127.0.0.1:8080}")
    private val allowedOrigins: String = "https://tibiawiki.dev,http://localhost:8080,http://127.0.0.1:8080"
) {

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration())
        return CorsFilter(source)
    }

    fun corsConfiguration(): CorsConfiguration {
        val config = CorsConfiguration()
        // No cookies on this API. Keep credentials off so GET can use "*" or
        // specific UI origins without Spring 6 rejecting the combination.
        config.allowCredentials = false
        applyOrigins(config, allowedOrigins)
        config.allowedHeaders = listOf("X-Requested-With", "Content-Type")
        config.allowedMethods = listOf("GET", "HEAD", "OPTIONS")
        return config
    }

    companion object {
        internal fun applyOrigins(config: CorsConfiguration, raw: String) {
            val origins = raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            if (origins.contains("*")) {
                config.allowedOrigins = listOf("*")
                return
            }
            if (origins.any { it.contains("*") }) {
                config.allowedOriginPatterns = origins
            } else {
                config.allowedOrigins = origins
            }
        }
    }
}
