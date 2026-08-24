package com.tibiawiki.process

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.factories.WikiObjectFactory
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import com.tibiawiki.domain.repositories.ArticleRepository
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

    fun modify(wikiObject: WikiObject, editSummary: String?): ModifyResult {
        val validationFailure = validate(wikiObject)
        if (validationFailure != null) {
            return validationFailure
        }

        val title = wikiObject.articleTitle()
        val originalWikiObject = articleRepository.getArticle(title)
            ?: return ModifyResult.Failure(ArticleNotFoundException(title))
        val json = wikiObjectFactory.createJSONObject(wikiObject, wikiObject.getTemplateType())
        val infobox = jsonFactory.convertJsonToInfoboxPartOfArticle(json, wikiObject.fieldOrder())
        val updatedArticle = articleFactory.insertInfoboxPartOfArticle(originalWikiObject, infobox).orElse(null)
            ?: return ModifyResult.Failure(IllegalArgumentException("Could not find required text in article"))

        val edited = articleRepository.modifyArticle(title, updatedArticle, editSummary)
        return if (edited) {
            ModifyResult.Success(wikiObject)
        } else {
            ModifyResult.Failure(ValidationException("Unable to edit wikiObject."))
        }
    }

    private fun validate(wikiObject: WikiObject): ModifyResult.Failure? {
        val validationResults = wikiObject.validate()
        return if (validationResults.isEmpty()) {
            null
        } else {
            ModifyResult.Failure(ValidationException.fromResults(validationResults))
        }
    }
}
