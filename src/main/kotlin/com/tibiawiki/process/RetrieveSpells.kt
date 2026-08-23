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
class RetrieveSpells(
    articleRepository: ArticleRepository,
    articleFactory: ArticleFactory,
    jsonFactory: JsonFactory
) : RetrieveAny(articleRepository, articleFactory, jsonFactory) {

    val spellsList: List<String>
        get() {
            val category = articleRepository.getPageNamesFromCategory(InfoboxTemplate.SPELL.categoryName)
            val listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS)
            return category.filter { page -> page !in listsCategory }
        }

    val spellsJSON: Stream<WikiJson>
        get() = getArticlesFromInfoboxTemplateAsJSON(spellsList)

    fun getSpellJSON(pageName: String): Optional<WikiJson> {
        return getArticleAsJSON(pageName)
    }
}
