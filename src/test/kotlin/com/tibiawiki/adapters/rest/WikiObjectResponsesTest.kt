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

class WikiObjectResponsesTest {

    @Test
    fun list_returnsNamesWhenExpandIsNullOrFalse() {
        val names = listOf("foo", "bar")
        val expanded = Stream.of(JSONObject(mapOf("name" to "foo")))

        assertThat(WikiObjectResponses.list(null, expanded, names).body, `is`(names))
        assertThat(WikiObjectResponses.list(false, Stream.empty(), names).body, `is`(names))
    }

    @Test
    fun list_returnsMappedJsonWhenExpandIsTrue() {
        val json = JSONObject(mapOf("name" to "foo", "hp" to "100"))
        val result = WikiObjectResponses.list(true, Stream.of(json), listOf("foo"))

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        val body = (result.body as Stream<*>).toList()
        assertThat(body, `is`(listOf(json.toMap())))
    }

    @Test
    fun byName_okWhenPresent() {
        val json = JSONObject(mapOf("name" to "Dragon"))
        val result = WikiObjectResponses.byName(Optional.of(json))

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body, `is`(json.toString(2)))
    }

    @Test
    fun byName_notFoundWhenEmpty() {
        val result = WikiObjectResponses.byName(Optional.empty())

        assertThat(result.statusCode, `is`(HttpStatus.NOT_FOUND))
        assertThat(result.body, `is`(nullValue()))
    }

    @Test
    fun modify_okOnSuccess() {
        val wikiObject: WikiObject = WikiObjectFixtures.achievement()
        val result = WikiObjectResponses.modify(Try.success(wikiObject))

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body, `is`(wikiObject))
    }

    @Test
    fun modify_badRequestOnValidationException() {
        val result = WikiObjectResponses.modify(Try.failure(ValidationException("invalid")))

        assertThat(result.statusCode, `is`(HttpStatus.BAD_REQUEST))
    }

    @Test
    fun modify_internalErrorOnUnexpectedFailure() {
        val result = WikiObjectResponses.modify(Try.failure(IllegalStateException("boom")))

        assertThat(result.statusCode, `is`(HttpStatus.INTERNAL_SERVER_ERROR))
    }
}
