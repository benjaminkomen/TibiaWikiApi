package com.tibiawiki.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Browser security headers for this public GET API. Spring Security is not on
 * the classpath (no user-authn product); a small filter is enough.
 *
 * Ordered ahead of [WikiWriteFilter] so PUT 403/401 responses still get headers.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
class SecurityHeadersFilter : OncePerRequestFilter(), Ordered {

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE + 10

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        response.setHeader(CONTENT_TYPE_OPTIONS, CONTENT_TYPE_OPTIONS_VALUE)
        response.setHeader(REFERRER_POLICY, REFERRER_POLICY_VALUE)
        response.setHeader(PERMISSIONS_POLICY, PERMISSIONS_POLICY_VALUE)
        filterChain.doFilter(request, response)
    }

    companion object {
        const val CONTENT_TYPE_OPTIONS = "X-Content-Type-Options"
        const val CONTENT_TYPE_OPTIONS_VALUE = "nosniff"
        const val REFERRER_POLICY = "Referrer-Policy"
        const val REFERRER_POLICY_VALUE = "no-referrer"
        const val PERMISSIONS_POLICY = "Permissions-Policy"
        const val PERMISSIONS_POLICY_VALUE =
            "camera=(), microphone=(), geolocation=(), payment=(), usb=()"
    }
}
