package com.tibiawiki.process

import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import org.springframework.stereotype.Component

@Component
class RetrieveCreatures(
    articleRepository: ArticleRepository,
    articleFactory: ArticleFactory,
    jsonFactory: JsonFactory
) : RetrieveAny(articleRepository, articleFactory, jsonFactory) {

    val creaturesList: List<String>
        get() {
            val category = articleRepository.getPageNamesFromCategory(InfoboxTemplate.CREATURE.categoryName)
            val listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS)
            return category.filter { page -> page !in listsCategory }
        }

    val creaturesJSON: List<WikiJson>
        get() = getArticlesFromInfoboxTemplateAsJSON(creaturesList)

    fun getCreatureJSON(pageName: String): WikiJson? {
        return getArticleAsJSON(pageName)
    }
}
