package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import io.vavr.control.Try
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import java.util.Optional
import java.util.stream.Stream

class WikiResourceResponsesTest {

    @Test
    fun listReturnsNamesWhenExpandIsNullOrFalse() {
        val names = listOf("foo", "bar")

        val withoutExpand = WikiResourceResponses.list(null, { Stream.of(emptyMap()) }, { names })
        val collapsed = WikiResourceResponses.list(false, { Stream.of(emptyMap()) }, { names })

        assertThat(withoutExpand.statusCode, `is`(HttpStatus.OK))
        assertThat(withoutExpand.body, `is`(names))
        assertThat(collapsed.body, `is`(names))
    }

    @Test
    fun listReturnsMappedJsonWhenExpandIsTrue() {
        val json = mapOf("name" to "Dragon")

        val result = WikiResourceResponses.list(true, { Stream.of(json) }, { listOf("unused") })

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        @Suppress("UNCHECKED_CAST")
        val body = (result.body as Iterable<Map<String, Any>>).toList()
        assertThat(body[0]["name"], `is`("Dragon"))
    }

    @Test
    fun jsonOrNotFoundReturnsWikiJson() {
        val found = WikiResourceResponses.jsonOrNotFound(Optional.of(mapOf("name" to "Book")))

        assertThat(found.statusCode, `is`(HttpStatus.OK))
        assertThat(found.body!!["name"], `is`("Book"))
    }

    @Test
    fun jsonOrNotFoundThrowsWhenMissingOrEmpty() {
        assertThrows<ArticleNotFoundException> {
            WikiResourceResponses.jsonOrNotFound(Optional.empty())
        }
        assertThrows<ArticleNotFoundException> {
            WikiResourceResponses.jsonOrNotFound(emptyMap())
        }
    }

    @Test
    fun modifyMapsSuccessAndRethrowsValidationOrNotFound() {
        val wikiObject = WikiObject.WikiObjectImpl()

        val ok = WikiResourceResponses.modify(Try.success(wikiObject))
        assertThat(ok.statusCode, `is`(HttpStatus.OK))
        assertThat(ok.body, `is`(wikiObject))

        assertThrows<ValidationException> {
            WikiResourceResponses.modify(Try.failure(ValidationException("invalid")))
        }
        assertThrows<ArticleNotFoundException> {
            WikiResourceResponses.modify(Try.failure(ArticleNotFoundException("Missing")))
        }

        val serverError = WikiResourceResponses.modify(Try.failure(IllegalStateException("boom")))
        assertThat(serverError.statusCode, `is`(HttpStatus.INTERNAL_SERVER_ERROR))
    }
}
