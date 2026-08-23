package com.tibiawiki.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsUtils
import org.springframework.web.cors.DefaultCorsProcessor
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
        return CorsFilter(source).apply {
            setCorsProcessor(AllowUnknownOriginGetProcessor())
        }
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

/**
 * Spring's CorsFilter answers 403 when Origin is not allow-listed. This API is
 * public GET: browsers hide the body without ACAO, but non-browser clients
 * should still receive 200. Preflight from an unknown origin stays rejected.
 */
internal class AllowUnknownOriginGetProcessor : DefaultCorsProcessor() {
    override fun processRequest(
        config: CorsConfiguration?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Boolean {
        val origin = request.getHeader(HttpHeaders.ORIGIN)
        if (config != null &&
            origin != null &&
            !CorsUtils.isPreFlightRequest(request) &&
            config.checkOrigin(origin) == null
        ) {
            return true
        }
        return super.processRequest(config, request, response)
    }
}
