package com.tibiawiki.domain

import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import org.json.JSONObject
import org.springframework.stereotype.Service

@Service
class RetrieveWikiPages(
    private val articleRepository: ArticleRepository,
    private val articleFactory: ArticleFactory,
    private val jsonFactory: JsonFactory,
) {

    fun getWikiPageJSON(pageName: String): JSONObject? {
        return articleRepository.getArticle(pageName)
            ?.let { articleFactory.extractInfoboxPartOfArticle(it) }
            ?.let { jsonFactory.convertInfoboxPartOfArticleToJson(it) }
    }
}
