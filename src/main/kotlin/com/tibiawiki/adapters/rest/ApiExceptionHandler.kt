package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.objects.validation.ValidationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * Single HTTP mapping for write validation, missing articles, and unexpected failures.
 * PUT may later be gated (#395); these mappings stay useful behind that flag.
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

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
    }
}
