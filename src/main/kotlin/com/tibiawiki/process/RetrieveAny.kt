package com.tibiawiki.process

import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import org.springframework.stereotype.Component

@Component
abstract class RetrieveAny(
    protected val articleRepository: ArticleRepository,
    protected val articleFactory: ArticleFactory,
    protected val jsonFactory: JsonFactory
) {

    fun getArticleAsJSON(pageName: String): WikiJson? {
        val article = articleRepository.getArticle(pageName) ?: return null
        val infobox = articleFactory.extractInfoboxPartOfArticle(article)
        return jsonFactory.convertInfoboxPartOfArticleToJson(infobox)
    }

    fun getArticlesFromInfoboxTemplateAsJSON(pageNames: List<String>): List<WikiJson> {
        return articleRepository.getArticlesFromCategory(pageNames).entries
            .map { articleFactory.extractInfoboxPartOfArticle(it) }
            .map { jsonFactory.convertInfoboxPartOfArticleToJson(it) }
    }

    companion object {
        const val CATEGORY_LISTS = "Lists"
    }
}
