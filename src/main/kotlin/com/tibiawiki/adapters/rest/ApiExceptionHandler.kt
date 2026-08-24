package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.objects.validation.ValidationException
import com.tibiawiki.domain.wiki.ExpandTooLargeException
import com.tibiawiki.domain.wiki.WikiUnavailableException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * Single HTTP mapping for write validation, missing articles, Fandom outages,
 * expand caps, and unexpected failures.
 */
@RestControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ValidationException::class)
    fun handleValidation(ex: ValidationException): ResponseEntity<ValidationErrorResponse> {
        return ResponseEntity.badRequest().body(
            ValidationErrorResponse(
                message = ex.message.orEmpty(),
                validationResults = ex.validationResults
            )
        )
    }

    @ExceptionHandler(ArticleNotFoundException::class)
    fun handleNotFound(ex: ArticleNotFoundException): ResponseEntity<Void> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(WikiUnavailableException::class)
    fun handleWikiUnavailable(e: WikiUnavailableException): ResponseEntity<Map<String, Any>> {
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
    fun handleExpandTooLarge(e: ExpandTooLargeException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE)
            .body(
                mapOf(
                    "error" to "expand_too_large",
                    "message" to (e.message ?: "expand request exceeds the configured page cap"),
                    "requested" to e.requested,
                    "maxPages" to e.max
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<Map<String, String>> {
        LOG.error("Unhandled exception", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "internal"))
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ApiExceptionHandler::class.java)
    }
}
