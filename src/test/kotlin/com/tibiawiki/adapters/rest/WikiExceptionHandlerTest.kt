package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.wiki.ExpandTooLargeException
import com.tibiawiki.domain.wiki.WikiUnavailableException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class WikiExceptionHandlerTest {

    private val handler = WikiExceptionHandler()

    @Test
    fun mapsWikiOutageTo503() {
        val result = handler.wikiUnavailable(WikiUnavailableException("Fandom down"))
        assertThat(result.statusCode, `is`(HttpStatus.SERVICE_UNAVAILABLE))
        assertThat(result.headers.getFirst("Retry-After"), `is`("5"))
        assertThat(result.body!!["error"], `is`("wiki_unavailable"))
        assertThat(result.body!!["message"], `is`("Fandom down"))
    }

    @Test
    fun mapsExpandCapTo413() {
        val result = handler.expandTooLarge(ExpandTooLargeException(9000, 5000))
        assertThat(result.statusCode, `is`(HttpStatus.PAYLOAD_TOO_LARGE))
        assertThat(result.body!!["error"], `is`("expand_too_large"))
        assertThat(result.body!!["requested"], `is`(9000))
        assertThat(result.body!!["maxPages"], `is`(5000))
    }
}
