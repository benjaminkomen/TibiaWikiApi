package com.tibiawiki.process

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import org.json.JSONObject
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.stream.Stream

@Component
class RetrieveCorpses(
    articleRepository: ArticleRepository,
    articleFactory: ArticleFactory,
    jsonFactory: JsonFactory
) : RetrieveAny(articleRepository, articleFactory, jsonFactory) {

    val corpsesList: List<String>
        get() {
            val category = articleRepository.getPageNamesFromCategory(InfoboxTemplate.CORPSE.categoryName)
            val listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS)
            return category.filter { page -> page !in listsCategory }
        }

    val corpsesJSON: Stream<JSONObject>
        get() = getArticlesFromInfoboxTemplateAsJSON(corpsesList)

    fun getCorpseJSON(pageName: String): Optional<JSONObject> {
        return getArticleAsJSON(pageName)
    }
}
