package com.tibiawiki.process

import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import io.github.fastily.jwiki.core.NS
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import java.util.Optional

class RetrieveLootTest {

    private lateinit var target: RetrieveLoot
    private lateinit var articleRepository: ArticleRepository
    private lateinit var articleFactory: ArticleFactory
    private lateinit var jsonFactory: JsonFactory

    @BeforeEach
    fun setup() {
        articleRepository = mock(ArticleRepository::class.java)
        articleFactory = mock(ArticleFactory::class.java)
        jsonFactory = mock(JsonFactory::class.java)
        target = RetrieveLoot(articleRepository, articleFactory, jsonFactory)

        doReturn(SOME_LOOT_PART).`when`(articleFactory).extractLootPartOfArticle(any())
        doReturn(SOME_LOOT_PART).`when`(articleFactory).extractLootPartOfArticle(anyString(), anyString())
        doReturn(SOME_LOOT_PARTS).`when`(articleFactory).extractAllLootPartsOfArticle(any())
        doReturn(SOME_LOOT_PARTS).`when`(articleFactory).extractAllLootPartsOfArticle(anyString(), anyString())
        doReturn(SOME_JSON_OBJECT).`when`(jsonFactory).convertLootPartOfArticleToJson(anyString(), anyString())
        doReturn(SOME_JSON_OBJECT).`when`(jsonFactory).convertAllLootPartsOfArticleToJson(anyString(), anyMap())
    }

    @Test
    fun testGetLootJSON_ZeroResults() {
        doReturn(emptyList<String>()).`when`(articleRepository)
            .getPageNamesFromCategory(eq("Loot Statistics"), any(NS::class.java))

        val result = target.getLootJSONObject().toList()

        assertThat(result, hasSize(0))
    }

    @Test
    fun testGetLootJSON_TwoResults() {
        val names = listOf(SOME_NAME, SOME_OTHER_NAME)
        val pages = mapOf(SOME_NAME to SOME_ARTICLE_CONTENT, SOME_OTHER_NAME to SOME_ARTICLE_CONTENT)

        doReturn(names).`when`(articleRepository)
            .getPageNamesFromCategory(eq("Loot Statistics"), any(NS::class.java))
        doReturn(pages).`when`(articleRepository).getArticlesFromCategory(anyList())

        val result = target.getLootJSONObject().toList()

        assertThat(result, hasSize(2))
    }

    @Test
    fun testGetLootJSONObjectByName() {
        doReturn(SOME_ARTICLE_CONTENT).`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Optional<JSONObject> = target.getLootJSONObject(SOME_PAGE_NAME)

        assertThat(result.orElseThrow(), `is`(SOME_JSON_OBJECT))
    }

    @Test
    fun testGetAllLootPartsJSON_TwoResults() {
        val names = listOf(SOME_NAME, SOME_OTHER_NAME)
        val pages = mapOf(SOME_NAME to SOME_ARTICLE_CONTENT, SOME_OTHER_NAME to SOME_ARTICLE_CONTENT)

        doReturn(names).`when`(articleRepository)
            .getPageNamesFromCategory(eq("Loot Statistics"), any(NS::class.java))
        doReturn(pages).`when`(articleRepository).getArticlesFromCategory(anyList())

        val result = target.getAllLootPartsJSON().toList()

        assertThat(result, hasSize(2))
    }

    @Test
    fun testGetAllLootPartsJSONByName() {
        doReturn(SOME_ARTICLE_CONTENT).`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Optional<JSONObject> = target.getAllLootPartsJSON(SOME_PAGE_NAME)

        assertThat(result.orElseThrow(), `is`(SOME_JSON_OBJECT))
    }

    @Test
    fun testGetLootList() {
        doReturn(listOf(SOME_NAME)).`when`(articleRepository)
            .getPageNamesFromCategory(eq("Loot Statistics"), any(NS::class.java))

        assertThat(target.getLootList(), `is`(listOf(SOME_NAME)))
    }

    @Test
    fun testMakeLootNamespace() {
        val namespace = RetrieveLoot.makeLootNamespace(112)

        assertThat(namespace, notNullValue())
    }

    companion object {
        private const val SOME_PAGE_NAME = "Loot_Statistics:Amazon"
        private const val SOME_ARTICLE_CONTENT = "{{Loot2\n|name=Amazon\n}}"
        private const val SOME_LOOT_PART = "|name=Amazon"
        private val SOME_LOOT_PARTS = mapOf("loot2" to SOME_LOOT_PART)
        private val SOME_JSON_OBJECT = JSONObject()
        private const val SOME_NAME = "Loot_Statistics:Amazon"
        private const val SOME_OTHER_NAME = "Loot_Statistics:Dragon"
    }
}
