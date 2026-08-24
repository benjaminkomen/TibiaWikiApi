package com.tibiawiki.process

import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import org.springframework.stereotype.Service

/**
 * Shared read path for every infobox category. Loot stays on [RetrieveLoot]
 * because it uses a custom namespace and different article extractors.
 *
 * [pageNames] / [articlesAsJSON] / [articleAsJSON] are the generic names.
 * [names] / [asJson] / [getJson] remain as aliases for dedicated fansite
 * and CipSoft-member controllers until those PUT collections are folded.
 */
@Service
class RetrieveByTemplate(
    articleRepository: ArticleRepository,
    articleFactory: ArticleFactory,
    jsonFactory: JsonFactory
) : RetrieveAny(articleRepository, articleFactory, jsonFactory) {

    fun pageNames(template: InfoboxTemplate): List<String> {
        val category = articleRepository.getPageNamesFromCategory(template.categoryName)
        val listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS)
        return category.filter { page -> page !in listsCategory }
    }

    fun articlesAsJSON(template: InfoboxTemplate): List<WikiJson> {
        return getArticlesFromInfoboxTemplateAsJSON(pageNames(template))
    }

    fun articleAsJSON(pageName: String): WikiJson? {
        return getArticleAsJSON(pageName)
    }

    fun names(template: InfoboxTemplate): List<String> = pageNames(template)

    fun asJson(template: InfoboxTemplate): List<WikiJson> = articlesAsJSON(template)

    fun getJson(pageName: String): WikiJson? = articleAsJSON(pageName)
}
