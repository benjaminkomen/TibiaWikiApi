package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import io.vavr.control.Try
import org.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.Optional
import java.util.stream.Stream

internal object WikiResourceResponses {

    fun list(expand: Boolean?, expanded: Stream<JSONObject>, names: List<String>): ResponseEntity<Any> {
        return ResponseEntity.ok()
            .body(
                if (expand == true) {
                    expanded.map<Any>(JSONObject::toMap)
                } else {
                    names
                }
            )
    }

    fun json(json: Optional<JSONObject>): ResponseEntity<String> {
        return json
            .map { ResponseEntity.ok().body(it.toString(2)) }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    fun modified(result: Try<WikiObject>): ResponseEntity<WikiObject> {
        return result
            .map { ResponseEntity.ok().body(it) }
            .recover<ValidationException>(ValidationException::class.java) { ResponseEntity.badRequest().build() }
            .recover { ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() }
            .get()
    }
}
