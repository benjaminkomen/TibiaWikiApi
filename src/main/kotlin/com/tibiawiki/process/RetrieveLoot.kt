package com.tibiawiki.process

import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.objects.WikiNamespace
import com.tibiawiki.domain.repositories.ArticleRepository
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.stream.Stream

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

    fun getLootJSONObject(): Stream<WikiJson> {
        return getArticlesFromLoot2TemplateAsJSONObject(getLootList())
    }

    fun getLootJSONObject(pageName: String): Optional<WikiJson> {
        return getLootArticleAsJSON(pageName)
    }

    fun getAllLootPartsJSON(): Stream<WikiJson> {
        return getArticlesFromAllLootTemplatesAsJSON(getLootList())
    }

    fun getAllLootPartsJSON(pageName: String): Optional<WikiJson> {
        return getAllLootPartsAsJSON(pageName)
    }

    private fun getArticlesFromLoot2TemplateAsJSONObject(pageNames: List<String>): Stream<WikiJson> {
        return Stream.of(pageNames)
            .flatMap { lst -> articleRepository.getArticlesFromCategory(lst).entries.stream() }
            .map { e ->
                val lootPartOfArticle = articleFactory.extractLootPartOfArticle(e)
                jsonFactory.convertLootPartOfArticleToJson(e.key, lootPartOfArticle)
            }
    }

    private fun getArticlesFromAllLootTemplatesAsJSON(pageNames: List<String>): Stream<WikiJson> {
        return Stream.of(pageNames)
            .flatMap { lst -> articleRepository.getArticlesFromCategory(lst).entries.stream() }
            .map { e ->
                val lootPartOfArticle = articleFactory.extractAllLootPartsOfArticle(e)
                jsonFactory.convertAllLootPartsOfArticleToJson(e.key, lootPartOfArticle)
            }
    }

    private fun getLootArticleAsJSON(pageName: String): Optional<WikiJson> {
        return Optional.ofNullable(articleRepository.getArticle(pageName))
            .map { articleContent -> articleFactory.extractLootPartOfArticle(pageName, articleContent) }
            .map { lootPartOfArticle -> jsonFactory.convertLootPartOfArticleToJson(pageName, lootPartOfArticle) }
    }

    private fun getAllLootPartsAsJSON(pageName: String): Optional<WikiJson> {
        return Optional.ofNullable(articleRepository.getArticle(pageName))
            .map { articleContent -> articleFactory.extractAllLootPartsOfArticle(pageName, articleContent) }
            .map { lootPartsOfArticle -> jsonFactory.convertAllLootPartsOfArticleToJson(pageName, lootPartsOfArticle) }
    }

    companion object {
        private const val LOOT_STATISTICS_CATEGORY_NAME = "Loot Statistics"
    }
}
