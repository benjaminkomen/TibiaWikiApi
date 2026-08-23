package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import io.vavr.control.Try
import org.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.Optional
import java.util.stream.Stream

/**
 * Shared HTTP mapping for the near-identical wiki list/detail/modify endpoints.
 * 400/404/500 status mapping lives in [ApiExceptionHandler]; helpers throw or unwrap.
 */
object WikiResourceResponses {

    fun list(
        expand: Boolean?,
        expanded: () -> Stream<JSONObject>,
        names: () -> List<String>
    ): ResponseEntity<Any> {
        val body = if (expand == true) {
            expanded().map { it.toMap() }.toList()
        } else {
            names()
        }
        return ResponseEntity.ok().body(body)
    }

    fun jsonOrNotFound(json: Optional<JSONObject>): ResponseEntity<String> {
        return jsonOrNotFound(json.orElse(null))
    }

    fun jsonOrNotFound(json: JSONObject?): ResponseEntity<String> {
        if (json == null || json.isEmpty) {
            throw ArticleNotFoundException()
        }
        return ResponseEntity.ok().body(json.toString(2))
    }

    fun modify(result: Try<WikiObject>): ResponseEntity<WikiObject> {
        if (result.isSuccess) {
            return ResponseEntity.ok().body(result.get())
        }
        when (val cause = result.cause) {
            is ValidationException -> throw cause
            is ArticleNotFoundException -> throw cause
            else -> return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}
