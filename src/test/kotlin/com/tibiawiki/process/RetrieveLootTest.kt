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
    }

    @Test
    fun testGetLootList() {
        doReturn(listOf(SOME_LOOT_NAME, SOME_OTHER_LOOT_NAME)).`when`(articleRepository)
            .getPageNamesFromCategory(eq("Loot Statistics"), any(NS::class.java))

        assertThat(target.getLootList(), hasSize(2))
    }

    @Test
    fun testGetLootJSONObject_ZeroResults() {
        doReturn(emptyList<String>()).`when`(articleRepository)
            .getPageNamesFromCategory(eq("Loot Statistics"), any(NS::class.java))
        doReturn(emptyMap<String, String>()).`when`(articleRepository).getArticlesFromCategory(anyList())

        assertThat(target.getLootJSONObject().toList(), hasSize(0))
    }

    @Test
    fun testGetLootJSONObject_TwoResults() {
        stubLootCategory()
        doReturn("loot2").`when`(articleFactory).extractLootPartOfArticle(any<Map.Entry<String, String>>())
        doReturn(SOME_JSON_OBJECT).`when`(jsonFactory).convertLootPartOfArticleToJson(anyString(), anyString())

        assertThat(target.getLootJSONObject().toList(), hasSize(2))
    }

    @Test
    fun testGetLootJSONObject_ByName() {
        doReturn("article").`when`(articleRepository).getArticle(SOME_PAGE_NAME)
        doReturn("loot2").`when`(articleFactory).extractLootPartOfArticle(SOME_PAGE_NAME, "article")
        doReturn(SOME_JSON_OBJECT).`when`(jsonFactory).convertLootPartOfArticleToJson(SOME_PAGE_NAME, "loot2")

        val result: Optional<JSONObject> = target.getLootJSONObject(SOME_PAGE_NAME)

        assertThat(result.orElseThrow(), `is`(SOME_JSON_OBJECT))
    }

    @Test
    fun testGetLootJSONObject_MissingArticle() {
        doReturn(null).`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        assertThat(target.getLootJSONObject(SOME_PAGE_NAME).isPresent, `is`(false))
    }

    @Test
    fun testGetAllLootPartsJSON_TwoResults() {
        stubLootCategory()
        doReturn(mapOf("loot2" to "part")).`when`(articleFactory)
            .extractAllLootPartsOfArticle(any<Map.Entry<String, String>>())
        doReturn(SOME_JSON_OBJECT).`when`(jsonFactory)
            .convertAllLootPartsOfArticleToJson(anyString(), any())

        assertThat(target.getAllLootPartsJSON().toList(), hasSize(2))
    }

    @Test
    fun testGetAllLootPartsJSON_ByName() {
        doReturn("article").`when`(articleRepository).getArticle(SOME_PAGE_NAME)
        doReturn(mapOf("loot2" to "part")).`when`(articleFactory)
            .extractAllLootPartsOfArticle(SOME_PAGE_NAME, "article")
        doReturn(SOME_JSON_OBJECT).`when`(jsonFactory)
            .convertAllLootPartsOfArticleToJson(eq(SOME_PAGE_NAME), any())

        val result: Optional<JSONObject> = target.getAllLootPartsJSON(SOME_PAGE_NAME)

        assertThat(result.orElseThrow(), `is`(SOME_JSON_OBJECT))
    }

    @Test
    fun testGetAllLootPartsJSON_MissingArticle() {
        doReturn(null).`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        assertThat(target.getAllLootPartsJSON(SOME_PAGE_NAME).isPresent, `is`(false))
    }

    @Test
    fun testMakeLootNamespace() {
        assertThat(RetrieveLoot.makeLootNamespace(112), `is`(notNullValue()))
    }

    private fun stubLootCategory() {
        val names = listOf(SOME_LOOT_NAME, SOME_OTHER_LOOT_NAME)
        doReturn(names).`when`(articleRepository)
            .getPageNamesFromCategory(eq("Loot Statistics"), any(NS::class.java))
        doReturn(
            mapOf(SOME_LOOT_NAME to SOME_ARTICLE_CONTENT, SOME_OTHER_LOOT_NAME to SOME_ARTICLE_CONTENT)
        ).`when`(articleRepository).getArticlesFromCategory(anyList())
    }

    companion object {
        private const val SOME_PAGE_NAME = "Loot_Statistics:Amazon"
        private const val SOME_ARTICLE_CONTENT = "{{Loot2|name=Amazon}}"
        private val SOME_JSON_OBJECT = JSONObject(mapOf("name" to "Amazon"))
        private const val SOME_LOOT_NAME = "Loot:Amazon"
        private const val SOME_OTHER_LOOT_NAME = "Loot:Dragon"
    }
}
