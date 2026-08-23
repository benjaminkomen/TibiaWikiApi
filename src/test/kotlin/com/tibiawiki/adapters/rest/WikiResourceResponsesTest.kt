package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import io.vavr.control.Try
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.Optional
import java.util.stream.Stream

class WikiResourceResponsesTest {

    @Test
    fun listReturnsNamesWhenExpandIsNullOrFalse() {
        val names = listOf("foo", "bar")

        val withoutExpand = WikiResourceResponses.list(null, { Stream.of(JSONObject()) }, { names })
        val collapsed = WikiResourceResponses.list(false, { Stream.of(JSONObject()) }, { names })

        assertThat(withoutExpand.statusCode, `is`(HttpStatus.OK))
        assertThat(withoutExpand.body, `is`(names))
        assertThat(collapsed.body, `is`(names))
    }

    @Test
    fun listReturnsMappedJsonWhenExpandIsTrue() {
        val json = JSONObject().put("name", "Dragon")

        val result = WikiResourceResponses.list(true, { Stream.of(json) }, { listOf("unused") })

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        @Suppress("UNCHECKED_CAST")
        val body = (result.body as Iterable<Map<String, Any>>).toList()
        assertThat(body[0]["name"], `is`("Dragon"))
    }

    @Test
    fun jsonOrNotFoundReturnsPrettyJsonOr404() {
        val found = WikiResourceResponses.jsonOrNotFound(Optional.of(JSONObject().put("name", "Book")))
        val missing = WikiResourceResponses.jsonOrNotFound(Optional.empty())

        assertThat(found.statusCode, `is`(HttpStatus.OK))
        assertThat(found.body!!.contains("Book"), `is`(true))
        assertThat(missing.statusCode, `is`(HttpStatus.NOT_FOUND))
        assertThat(missing.body, nullValue())
    }

    @Test
    fun modifyMapsSuccessValidationFailureAndUnexpectedFailure() {
        val wikiObject = WikiObject.WikiObjectImpl()

        val ok = WikiResourceResponses.modify(Try.success(wikiObject))
        val badRequest = WikiResourceResponses.modify(Try.failure(ValidationException("invalid")))
        val serverError = WikiResourceResponses.modify(Try.failure(IllegalStateException("boom")))

        assertThat(ok.statusCode, `is`(HttpStatus.OK))
        assertThat(ok.body, `is`(wikiObject))
        assertThat(badRequest.statusCode, `is`(HttpStatus.BAD_REQUEST))
        assertThat(serverError.statusCode, `is`(HttpStatus.INTERNAL_SERVER_ERROR))
    }
}
