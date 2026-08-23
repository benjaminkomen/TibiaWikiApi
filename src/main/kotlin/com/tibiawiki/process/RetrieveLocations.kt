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
class RetrieveLocations(
    articleRepository: ArticleRepository,
    articleFactory: ArticleFactory,
    jsonFactory: JsonFactory
) : RetrieveAny(articleRepository, articleFactory, jsonFactory) {

    val locationsList: List<String>
        get() {
            val category = articleRepository.getPageNamesFromCategory(InfoboxTemplate.GEOGRAPHY.categoryName)
            val listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS)
            return category.filter { page -> page !in listsCategory }
        }

    val locationsJSON: Stream<JSONObject>
        get() = getArticlesFromInfoboxTemplateAsJSON(locationsList)

    fun getLocationJSON(pageName: String): Optional<JSONObject> {
        return getArticleAsJSON(pageName)
    }
}
