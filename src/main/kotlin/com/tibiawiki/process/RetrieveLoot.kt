package com.tibiawiki.process

import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.objects.WikiNamespace
import com.tibiawiki.domain.repositories.ArticleRepository
import org.springframework.stereotype.Component

@Component
class RetrieveLoot(
    articleRepository: ArticleRepository,
    articleFactory: ArticleFactory,
    jsonFactory: JsonFactory
) : RetrieveAny(articleRepository, articleFactory, jsonFactory) {

    fun getLootList(): List<String> {
        return articleRepository.getPageNamesFromCategory(
            LOOT_STATISTICS_CATEGORY_NAME,
            WikiNamespace.LOOT_STATISTICS
        )
    }

    fun getLootJSONObject(): List<WikiJson> {
        return getArticlesFromLoot2TemplateAsJSONObject(getLootList())
    }

    fun getLootJSONObject(pageName: String): WikiJson? {
        return getLootArticleAsJSON(pageName)
    }

    fun getAllLootPartsJSON(): List<WikiJson> {
        return getArticlesFromAllLootTemplatesAsJSON(getLootList())
    }

    fun getAllLootPartsJSON(pageName: String): WikiJson? {
        return getAllLootPartsAsJSON(pageName)
    }

    private fun getArticlesFromLoot2TemplateAsJSONObject(pageNames: List<String>): List<WikiJson> {
        return articleRepository.getArticlesFromCategory(pageNames).entries
            .map { e ->
                val lootPartOfArticle = articleFactory.extractLootPartOfArticle(e)
                jsonFactory.convertLootPartOfArticleToJson(e.key, lootPartOfArticle)
            }
    }

    private fun getArticlesFromAllLootTemplatesAsJSON(pageNames: List<String>): List<WikiJson> {
        return articleRepository.getArticlesFromCategory(pageNames).entries
            .map { e ->
                val lootPartOfArticle = articleFactory.extractAllLootPartsOfArticle(e)
                jsonFactory.convertAllLootPartsOfArticleToJson(e.key, lootPartOfArticle)
            }
    }

    private fun getLootArticleAsJSON(pageName: String): WikiJson? {
        val articleContent = articleRepository.getArticle(pageName) ?: return null
        val lootPartOfArticle = articleFactory.extractLootPartOfArticle(pageName, articleContent)
        return jsonFactory.convertLootPartOfArticleToJson(pageName, lootPartOfArticle)
    }

    private fun getAllLootPartsAsJSON(pageName: String): WikiJson? {
        val articleContent = articleRepository.getArticle(pageName) ?: return null
        val lootPartsOfArticle = articleFactory.extractAllLootPartsOfArticle(pageName, articleContent)
        return jsonFactory.convertAllLootPartsOfArticleToJson(pageName, lootPartsOfArticle)
    }

    companion object {
        private const val LOOT_STATISTICS_CATEGORY_NAME = "Loot Statistics"
    }
}
