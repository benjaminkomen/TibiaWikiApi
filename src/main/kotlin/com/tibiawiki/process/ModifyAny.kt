package com.tibiawiki.process

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.factories.WikiObjectFactory
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import com.tibiawiki.domain.repositories.ArticleRepository
import io.vavr.control.Try
import org.springframework.stereotype.Component

/**
 * 1. Validate modified WikiObject
 * 2. Get current wikipage
 * 3. Replace infobox part of template with newly made infobox part of template
 * 4. Edit the wiki (via a repository)
 */
@Component
class ModifyAny(
    private val wikiObjectFactory: WikiObjectFactory,
    private val jsonFactory: JsonFactory,
    private val articleFactory: ArticleFactory,
    private val articleRepository: ArticleRepository
) {

    fun modify(wikiObject: WikiObject, editSummary: String?): Try<WikiObject> {
        val title = wikiObject.articleTitle()
        return validate(wikiObject)
            .flatMap { obj ->
                val originalWikiObject = articleRepository.getArticle(title)
                    ?: return@flatMap Try.failure(ArticleNotFoundException(title))
                Try.success(obj)
                    .map { wikiObj -> wikiObjectFactory.createJSONObject(wikiObj, wikiObj.getTemplateType()) }
                    .map { json -> jsonFactory.convertJsonToInfoboxPartOfArticle(json, wikiObject.fieldOrder()) }
                    .map { s -> articleFactory.insertInfoboxPartOfArticle(originalWikiObject, s) }
                    .flatMap { s ->
                        if (s.isEmpty) {
                            Try.failure(IllegalArgumentException("Could not find required text in article"))
                        } else {
                            Try.success(s.get())
                        }
                    }
                    .map { s -> articleRepository.modifyArticle(title, s, editSummary) }
                    .flatMap { b ->
                        if (b) {
                            Try.success(wikiObject)
                        } else {
                            Try.failure(ValidationException("Unable to edit wikiObject."))
                        }
                    }
            }
    }

    private fun validate(wikiObject: WikiObject): Try<WikiObject> {
        val validationResults = wikiObject.validate()
        return if (validationResults.isEmpty()) {
            Try.success(wikiObject)
        } else {
            Try.failure(ValidationException.fromResults(validationResults))
        }
    }
}
