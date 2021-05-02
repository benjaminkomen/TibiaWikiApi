package com.tibiawiki.domain

import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.infobox.ArticleMapper
import com.tibiawiki.domain.mediawiki.ArticleRepository
import org.json.JSONObject
import org.springframework.stereotype.Service

@Service
class RetrieveAnyService(
    private val articleRepository: ArticleRepository,
    private val jsonFactory: JsonFactory,
) {

    fun getArticleAsJSON(pageName: String): JSONObject? {
        return articleRepository.getArticle(pageName)
            ?.let { ArticleMapper.extractInfoboxPartOfArticle(it) }
            ?.let { jsonFactory.convertInfoboxPartOfArticleToJson(it) }
    }

    fun getArticlesFromInfoboxTemplateAsJSON(pageNames: List<String>): List<JSONObject> {
        return pageNames
            .flatMap { articleRepository.getArticlesFromCategory(it).entries }
            .map { ArticleMapper.extractInfoboxPartOfArticle(Pair(it.key, it.value)) }
            .map { jsonFactory.convertInfoboxPartOfArticleToJson(it) }
    }

    companion object {
        const val CATEGORY_LISTS = "Lists"
    }
}
