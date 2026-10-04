package com.tibiawiki.config

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletRequestWrapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets
import java.time.Instant

/**
 * Soft per-client token-bucket throttle for GET/HEAD /api paths. Writes the 429
 * body directly (hot path) so it stays aligned with [ApiExceptionHandler]-style
 * `{ "error": ... }` JSON without going through advice.
 *
 * The client IP is the rightmost trusted `X-Forwarded-For` entry (see
 * [ClientIpKeyResolver.clientAddress]), read from the original container request.
 * Spring's `ForwardedHeaderFilter` (`server.forward-headers-strategy=framework`)
 * takes the leftmost, client-supplied entry for [HttpServletRequest.getRemoteAddr]
 * and also trusts a client `Forwarded` header, so a client could get a fresh
 * bucket on every request (#502). This filter does not use that value.
 *
 * Actuator, springdoc, and Swagger UI live outside `/api/` so they are not limited.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
@EnableConfigurationProperties(RateLimitProperties::class)
class RateLimitFilter(
    private val properties: RateLimitProperties,
    private val ipKeyResolver: ClientIpKeyResolver,
    private val rateLimitService: RateLimitService
) : OncePerRequestFilter(), Ordered {

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE + 20

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        if (!properties.enabled) {
            return true
        }
        val method = request.method
        if (method != HttpMethod.GET.name() && method != HttpMethod.HEAD.name()) {
            return true
        }
        val path = request.requestURI ?: return true
        return !path.startsWith(API_PREFIX)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val clientKey = resolveClientKey(request)
        val expand = isExpandRequest(request)
        val verdict = rateLimitService.tryConsume(clientKey, expand)
        setRateLimitHeaders(response, verdict)

        if (!verdict.allowed) {
            logRejected(request, clientKey, verdict)
            writeRateLimited(response, verdict.retryAfterSeconds)
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveClientKey(request: HttpServletRequest): String {
        val original = originalRequest(request)
        val forwardedFor = original.getHeaders(HEADER_X_FORWARDED_FOR)?.toList().orEmpty()
        val clientAddress = ipKeyResolver.clientAddress(
            original.remoteAddr,
            forwardedFor,
            properties.forwardedForTrustedHops
        )
        return ipKeyResolver.resolveKey(clientAddress)
    }

    /** Unwraps to the container request, which still has the raw forwarded headers and socket address. */
    private fun originalRequest(request: HttpServletRequest): HttpServletRequest {
        var current: ServletRequest = request
        while (current is ServletRequestWrapper) {
            current = current.request
        }
        return current as? HttpServletRequest ?: request
    }

    private fun isExpandRequest(request: HttpServletRequest): Boolean {
        val expand = request.getParameter("expand") ?: return false
        return expand.equals("true", ignoreCase = true)
    }

    private fun setRateLimitHeaders(response: HttpServletResponse, verdict: RateLimitService.Verdict) {
        response.setHeader(HEADER_LIMIT, verdict.limit.toString())
        response.setHeader(HEADER_REMAINING, verdict.remaining.toString())
        val resetEpoch = Instant.now().epochSecond +
            if (verdict.allowed) {
                0L
            } else {
                verdict.retryAfterSeconds.toLong().coerceAtLeast(0)
            }
        response.setHeader(HEADER_RESET, resetEpoch.toString())
    }

    private fun writeRateLimited(response: HttpServletResponse, retryAfterSeconds: Int) {
        val retry = retryAfterSeconds.coerceAtLeast(1).coerceAtMost(60)
        response.status = HttpStatus.TOO_MANY_REQUESTS.value()
        response.setHeader(HEADER_RETRY_AFTER, retry.toString())
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.writer.write(
            """{"error":"rate_limited","message":"Too many requests; slow down and retry","retryAfterSeconds":$retry}"""
        )
    }

    private fun logRejected(
        request: HttpServletRequest,
        clientKey: String,
        verdict: RateLimitService.Verdict
    ) {
        val userAgent = request.getHeader("User-Agent")?.take(USER_AGENT_MAX_LEN).orEmpty()
        LOG.warn(
            "event=rate_limited clientKey={} path={} method={} userAgent={} " +
                "retryAfterSeconds={} limit={} remaining={} scope={}",
            clientKey,
            request.requestURI,
            request.method,
            userAgent,
            verdict.retryAfterSeconds,
            verdict.limit,
            verdict.remaining,
            verdict.scope
        )
    }

    companion object {
        const val API_PREFIX = "/api/"
        const val HEADER_RETRY_AFTER = "Retry-After"
        const val HEADER_LIMIT = "X-RateLimit-Limit"
        const val HEADER_REMAINING = "X-RateLimit-Remaining"
        const val HEADER_RESET = "X-RateLimit-Reset"
        const val HEADER_X_FORWARDED_FOR = "X-Forwarded-For"

        private const val USER_AGENT_MAX_LEN = 80
        private val LOG = LoggerFactory.getLogger(RateLimitFilter::class.java)
    }
}
