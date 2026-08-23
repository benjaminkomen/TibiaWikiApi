package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import com.tibiawiki.domain.objects.validation.ValidationResult
import com.tibiawiki.domain.objects.validation.ValidationSeverity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
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
    fun unexpectedExceptionMapsTo500WithoutBody() {
        val response = target.handleUnexpected(IllegalStateException("boom"))

        assertThat(response.statusCode, `is`(HttpStatus.INTERNAL_SERVER_ERROR))
        assertThat(response.body, nullValue())
    }
}
