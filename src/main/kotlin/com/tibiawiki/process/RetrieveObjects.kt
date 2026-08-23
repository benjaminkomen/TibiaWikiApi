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
class RetrieveObjects(
    articleRepository: ArticleRepository,
    articleFactory: ArticleFactory,
    jsonFactory: JsonFactory
) : RetrieveAny(articleRepository, articleFactory, jsonFactory) {

    val objectsList: List<String>
        get() {
            val category = articleRepository.getPageNamesFromCategory(InfoboxTemplate.OBJECT.categoryName)
            val listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS)
            return category.filter { page -> page !in listsCategory }
        }

    val objectsJSON: Stream<WikiJson>
        get() = getArticlesFromInfoboxTemplateAsJSON(objectsList)

    fun getObjectJSON(pageName: String): Optional<WikiJson> {
        return getArticleAsJSON(pageName)
    }
}
