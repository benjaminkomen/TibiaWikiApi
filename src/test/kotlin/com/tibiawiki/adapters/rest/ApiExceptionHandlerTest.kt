package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import com.tibiawiki.domain.objects.validation.ValidationResult
import com.tibiawiki.domain.objects.validation.ValidationSeverity
import com.tibiawiki.domain.wiki.ExpandTooLargeException
import com.tibiawiki.domain.wiki.WikiUnavailableException
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

class ApiExceptionHandlerTest {

    private val target = ApiExceptionHandler()

    @Test
    fun validationExceptionMapsTo400WithMessageAndResults() {
        val results = listOf(ValidationResult(ValidationSeverity.ERROR, WikiObject.NAME_REQUIRED))
        val response = target.handleValidation(ValidationException.fromResults(results))

        assertThat(response.statusCode, `is`(HttpStatus.BAD_REQUEST))
        assertThat(response.body!!.message, `is`(WikiObject.NAME_REQUIRED))
        assertThat(response.body!!.validationResults, `is`(results))
    }

    @Test
    fun validationExceptionWithMessageOnlyIncludesEmptyResults() {
        val response = target.handleValidation(ValidationException("Unable to edit wikiObject."))

        assertThat(response.statusCode, `is`(HttpStatus.BAD_REQUEST))
        assertThat(response.body!!.message, `is`("Unable to edit wikiObject."))
        assertThat(response.body!!.validationResults, `is`(emptyList()))
    }

    @Test
    fun missingArticleMapsTo404WithoutBody() {
        val response = target.handleNotFound(ArticleNotFoundException("Dragon"))

        assertThat(response.statusCode, `is`(HttpStatus.NOT_FOUND))
        assertThat(response.body, nullValue())
    }

    @Test
    fun unexpectedExceptionMapsTo500WithInternalError() {
        val logger = LoggerFactory.getLogger(ApiExceptionHandler::class.java) as Logger
        val appender = ListAppender<ILoggingEvent>()
        appender.start()
        logger.addAppender(appender)
        try {
            val response = target.handleUnexpected(IllegalStateException("boom"))

            assertThat(response.statusCode, `is`(HttpStatus.INTERNAL_SERVER_ERROR))
            assertThat(response.body!!["error"], `is`("internal"))
            assertThat(response.body!!.containsKey("message"), `is`(false))

            val event = appender.list.single()
            assertThat(event.level, `is`(Level.ERROR))
            assertThat(event.throwableProxy.className, `is`(IllegalStateException::class.java.name))
            assertThat(event.throwableProxy.message, `is`("boom"))
        } finally {
            logger.detachAppender(appender)
            appender.stop()
        }
    }

    @Test
    fun mapsWikiOutageTo503() {
        val result = target.handleWikiUnavailable(WikiUnavailableException("Fandom down"))
        assertThat(result.statusCode, `is`(HttpStatus.SERVICE_UNAVAILABLE))
        assertThat(result.headers.getFirst("Retry-After"), `is`("5"))
        assertThat(result.body!!["error"], `is`("wiki_unavailable"))
        assertThat(result.body!!["message"], `is`("Fandom down"))
    }

    @Test
    fun mapsExpandCapTo413() {
        val result = target.handleExpandTooLarge(ExpandTooLargeException(9000, 5000))
        assertThat(result.statusCode, `is`(HttpStatus.CONTENT_TOO_LARGE))
        assertThat(result.body!!["error"], `is`("expand_too_large"))
        assertThat(result.body!!["requested"], `is`(9000))
        assertThat(result.body!!["maxPages"], `is`(5000))
    }
}
