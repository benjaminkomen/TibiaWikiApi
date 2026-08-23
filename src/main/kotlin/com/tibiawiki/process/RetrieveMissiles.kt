package com.tibiawiki.process

import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.stream.Stream

@Component
class RetrieveMissiles(
    articleRepository: ArticleRepository,
    articleFactory: ArticleFactory,
    jsonFactory: JsonFactory
) : RetrieveAny(articleRepository, articleFactory, jsonFactory) {

    val missilesList: List<String>
        get() {
            val category = articleRepository.getPageNamesFromCategory(InfoboxTemplate.MISSILE.categoryName)
            val listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS)
            return category.filter { page -> page !in listsCategory }
        }

    val missilesJSON: Stream<WikiJson>
        get() = getArticlesFromInfoboxTemplateAsJSON(missilesList)

    fun getMissileJSON(pageName: String): Optional<WikiJson> {
        return getArticleAsJSON(pageName)
    }
}
