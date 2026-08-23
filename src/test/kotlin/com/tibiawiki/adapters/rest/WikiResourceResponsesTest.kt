package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.WikiObjectFixtures
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
    fun list_returnsNamesWhenExpandIsNull() {
        val names = listOf("foo", "bar")
        val result = WikiResourceResponses.list(null, Stream.of(JSONObject()), names)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body, `is`(names))
    }

    @Test
    fun list_returnsNamesWhenExpandIsFalse() {
        val names = listOf("foo")
        val result = WikiResourceResponses.list(false, Stream.of(JSONObject()), names)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body, `is`(names))
    }

    @Test
    fun list_returnsExpandedMapsWhenExpandIsTrue() {
        val json = JSONObject().put("name", "foo")
        val result = WikiResourceResponses.list(true, Stream.of(json), listOf("foo"))

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        val body = (result.body as Stream<*>).toList()
        assertThat(body.size, `is`(1))
        assertThat((body[0] as Map<*, *>)["name"], `is`("foo"))
    }

    @Test
    fun json_returnsPrettyBodyWhenPresent() {
        val json = JSONObject().put("name", "foo")
        val result = WikiResourceResponses.json(Optional.of(json))

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body, `is`(json.toString(2)))
    }

    @Test
    fun json_returnsNotFoundWhenEmpty() {
        val result = WikiResourceResponses.json(Optional.empty())

        assertThat(result.statusCode, `is`(HttpStatus.NOT_FOUND))
        assertThat(result.body, nullValue())
    }

    @Test
    fun modified_returnsOkOnSuccess() {
        val wikiObject: WikiObject = WikiObjectFixtures.book()
        val result = WikiResourceResponses.modified(Try.success(wikiObject))

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body, `is`(wikiObject))
    }

    @Test
    fun modified_returnsBadRequestOnValidationException() {
        val result = WikiResourceResponses.modified(Try.failure(ValidationException("invalid")))

        assertThat(result.statusCode, `is`(HttpStatus.BAD_REQUEST))
    }

    @Test
    fun modified_returnsInternalServerErrorOnOtherFailure() {
        val result = WikiResourceResponses.modified(Try.failure(IllegalStateException("boom")))

        assertThat(result.statusCode, `is`(HttpStatus.INTERNAL_SERVER_ERROR))
    }
}
