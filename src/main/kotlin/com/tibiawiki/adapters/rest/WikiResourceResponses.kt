package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import com.tibiawiki.process.ModifyResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Shared HTTP mapping for the near-identical wiki list/detail/modify endpoints.
 * 400/404/500 status mapping lives in [ApiExceptionHandler]; helpers throw or unwrap.
 * List-expand and detail share [WikiJson] (wiki keys).
 */
object WikiResourceResponses {

    fun list(
        expand: Boolean?,
        expanded: () -> List<WikiJson>,
        names: () -> List<String>
    ): ResponseEntity<Any> {
        val body = if (expand == true) {
            expanded()
        } else {
            names()
        }
        return ResponseEntity.ok().body(body)
    }

    fun jsonOrNotFound(json: WikiJson?): ResponseEntity<WikiJson> {
        if (json == null || json.isEmpty()) {
            throw ArticleNotFoundException()
        }
        return ResponseEntity.ok().body(json)
    }

    fun modify(result: ModifyResult): ResponseEntity<WikiObject> {
        return when (result) {
            is ModifyResult.Success -> ResponseEntity.ok().body(result.wikiObject)
            is ModifyResult.Failure -> {
                when (val cause = result.cause) {
                    is ValidationException -> throw cause
                    is ArticleNotFoundException -> throw cause
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
                }
            }
        }
    }
}
