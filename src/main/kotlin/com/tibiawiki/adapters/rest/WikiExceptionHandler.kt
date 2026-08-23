package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.wiki.ExpandTooLargeException
import com.tibiawiki.domain.wiki.WikiUnavailableException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class WikiExceptionHandler {

    @ExceptionHandler(WikiUnavailableException::class)
    fun wikiUnavailable(e: WikiUnavailableException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .header("Retry-After", "5")
            .body(
                mapOf(
                    "error" to "wiki_unavailable",
                    "message" to (e.message ?: "Fandom is unavailable")
                )
            )
    }

    @ExceptionHandler(ExpandTooLargeException::class)
    fun expandTooLarge(e: ExpandTooLargeException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
            .body(
                mapOf(
                    "error" to "expand_too_large",
                    "message" to (e.message ?: "expand request exceeds the configured page cap"),
                    "requested" to e.requested,
                    "maxPages" to e.max
                )
            )
    }
}
