package com.tibiawiki.process

import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import org.springframework.stereotype.Component

/**
 * Shared list/expand/detail retrieval for wiki categories that follow the
 * standard InfoboxTemplate + Category:Lists filter. Used by the issue #408
 * collection endpoints so a later generic Retrieve (#394) can absorb them
 * without changing HTTP contracts.
 */
@Component
class RetrieveByTemplate(
    articleRepository: ArticleRepository,
    articleFactory: ArticleFactory,
    jsonFactory: JsonFactory
) : RetrieveAny(articleRepository, articleFactory, jsonFactory) {

    fun names(template: InfoboxTemplate): List<String> {
        val category = articleRepository.getPageNamesFromCategory(template.categoryName)
        val listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS)
        return category.filter { page -> page !in listsCategory }
    }

    fun asJson(template: InfoboxTemplate): List<WikiJson> {
        return getArticlesFromInfoboxTemplateAsJSON(names(template))
    }

    fun getJson(pageName: String): WikiJson? {
        return getArticleAsJSON(pageName)
    }
}
