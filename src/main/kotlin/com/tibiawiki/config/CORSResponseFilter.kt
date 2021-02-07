package com.tibiawiki.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CORSResponseFilter {

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOrigins = listOf("*")
        config.allowedHeaders = listOf("X-Requested-With", "Content-Type")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}
