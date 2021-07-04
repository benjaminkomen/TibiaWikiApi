package com.tibiawiki.domain

import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.infobox.ArticleMapper
import com.tibiawiki.domain.mediawiki.ArticleRepository
import org.json.JSONObject
import org.springframework.stereotype.Service

@Service
class RetrieveWikiPages(
    private val articleRepository: ArticleRepository,
    private val jsonFactory: JsonFactory,
) {

    fun getWikiPageJSON(pageName: String): JSONObject? {
        return articleRepository.getArticle(pageName)
            ?.let { ArticleMapper.extractInfoboxPartOfArticle(it) }
            ?.let { jsonFactory.convertInfoboxPartOfArticleToJson(it) }
    }
}
