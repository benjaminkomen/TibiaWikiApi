package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import io.vavr.control.Try
import org.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.Optional
import java.util.stream.Stream

/**
 * Shared list / by-name / modify HTTP mapping used by the wiki-object REST controllers.
 */
object WikiObjectResponses {

    fun list(
        expand: Boolean?,
        expanded: Stream<JSONObject>,
        names: List<String>
    ): ResponseEntity<Any> {
        return ResponseEntity.ok().body(
            if (expand == true) {
                expanded.map<Any>(JSONObject::toMap)
            } else {
                names
            }
        )
    }

    fun byName(json: Optional<JSONObject>): ResponseEntity<String> {
        return json
            .map { a -> ResponseEntity.ok().body(a.toString(2)) }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    fun modify(result: Try<WikiObject>): ResponseEntity<WikiObject> {
        return result
            .map { a -> ResponseEntity.ok().body(a) }
            .recover(ValidationException::class.java) { ResponseEntity.badRequest().build() }
            .recover { ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() }
            .get()
    }
}
