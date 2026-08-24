package com.tibiawiki.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Gates public PUT so Cloud Run cannot mutate TibiaWiki unless writes are
 * opted in. [com.tibiawiki.process.ModifyAny] stays for a future bot.
 *
 * - `WIKI_WRITE_ENABLED=false` (default): 403
 * - enabled + `WIKI_WRITE_TOKEN` set: 401 unless Bearer or X-WIKI-Write-Token matches
 */
@Component
class WikiWriteFilter(
    @param:Value("\${wiki.write.enabled:false}")
    private val writeEnabled: Boolean,
    @param:Value("\${wiki.write.token:}")
    private val writeToken: String
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        if (request.method != HttpMethod.PUT.name()) {
            return true
        }
        val path = request.requestURI
        return path == null || !path.startsWith(API_PREFIX)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (!writeEnabled) {
            writeJson(response, HttpStatus.FORBIDDEN, """{"error":"Wiki writes are disabled"}""")
            return
        }
        if (writeToken.isNotBlank() && !tokenMatches(request)) {
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
            writeJson(response, HttpStatus.UNAUTHORIZED, """{"error":"Invalid or missing write token"}""")
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun tokenMatches(request: HttpServletRequest): Boolean {
        val provided = request.getHeader(TOKEN_HEADER)?.takeIf { it.isNotBlank() }
            ?: bearerToken(request)
            ?: return false
        return MessageDigest.isEqual(
            writeToken.toByteArray(StandardCharsets.UTF_8),
            provided.toByteArray(StandardCharsets.UTF_8)
        )
    }

    private fun bearerToken(request: HttpServletRequest): String? {
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        if (!authorization.regionMatches(0, BEARER_PREFIX, 0, BEARER_PREFIX.length, ignoreCase = true)) {
            return null
        }
        return authorization.substring(BEARER_PREFIX.length).takeIf { it.isNotBlank() }
    }

    private fun writeJson(response: HttpServletResponse, status: HttpStatus, body: String) {
        response.status = status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.writer.write(body)
    }

    companion object {
        const val TOKEN_HEADER = "X-WIKI-Write-Token"
        const val API_PREFIX = "/api/"
        private const val BEARER_PREFIX = "Bearer "
    }
}
