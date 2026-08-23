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
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import java.util.Optional

class RetrieveLootTest {

    private lateinit var target: RetrieveLoot
    private lateinit var articleRepository: ArticleRepository

    @BeforeEach
    fun setup() {
        articleRepository = mock(ArticleRepository::class.java)
        target = RetrieveLoot(articleRepository, ArticleFactory(), JsonFactory())
    }

    @Test
    fun testGetLootJSON_ZeroResults() {
        stubLootCategory(emptyList())

        val result = target.getLootJSONObject().toList()

        assertThat(result, hasSize(0))
    }

    @Test
    fun testGetLootJSON_TwoResults() {
        val names = listOf(SOME_NAME, SOME_OTHER_NAME)
        val pages = mapOf(SOME_NAME to SOME_ARTICLE_CONTENT, SOME_OTHER_NAME to SOME_ARTICLE_CONTENT)
        stubLootCategory(names)
        doReturn(pages).`when`(articleRepository).getArticlesFromCategory(anyList())

        val result = target.getLootJSONObject().toList()

        assertThat(result, hasSize(2))
        assertThat(result[0].getString("name"), `is`("Amazon"))
    }

    @Test
    fun testGetLootJSONObjectByName() {
        doReturn(SOME_ARTICLE_CONTENT).`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Optional<JSONObject> = target.getLootJSONObject(SOME_PAGE_NAME)

        assertThat(result.orElseThrow().getString("name"), `is`("Amazon"))
    }

    @Test
    fun testGetAllLootPartsJSON_TwoResults() {
        val names = listOf(SOME_NAME, SOME_OTHER_NAME)
        val pages = mapOf(SOME_NAME to SOME_ARTICLE_CONTENT, SOME_OTHER_NAME to SOME_ARTICLE_CONTENT)
        stubLootCategory(names)
        doReturn(pages).`when`(articleRepository).getArticlesFromCategory(anyList())

        val result = target.getAllLootPartsJSON().toList()

        assertThat(result, hasSize(2))
        assertThat(result[0].has("loot2"), `is`(true))
    }

    @Test
    fun testGetAllLootPartsJSONByName() {
        doReturn(SOME_ARTICLE_CONTENT).`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Optional<JSONObject> = target.getAllLootPartsJSON(SOME_PAGE_NAME)

        assertThat(result.orElseThrow().has("loot2"), `is`(true))
    }

    @Test
    fun testGetLootList() {
        stubLootCategory(listOf(SOME_NAME))

        assertThat(target.getLootList(), `is`(listOf(SOME_NAME)))
    }

    @Test
    fun testMakeLootNamespace() {
        val namespace = RetrieveLoot.makeLootNamespace(112)

        assertThat(namespace, notNullValue())
    }

    private fun stubLootCategory(names: List<String>) {
        doReturn(names).`when`(articleRepository)
            .getPageNamesFromCategory(eqValue("Loot Statistics"), anyValue(NS::class.java))
    }

    companion object {
        private const val SOME_PAGE_NAME = "Loot_Statistics:Amazon"
        private const val SOME_ARTICLE_CONTENT = "{{Loot2\n|version=8.6\n|kills=1\n|name=Amazon\n}}"
        private const val SOME_NAME = "Loot_Statistics:Amazon"
        private const val SOME_OTHER_NAME = "Loot_Statistics:Dragon"

        private fun eqValue(value: String): String {
            eq(value)
            return value
        }

        @Suppress("UNCHECKED_CAST")
        private fun <T> anyValue(type: Class<T>): T {
            any(type)
            return null as T
        }
    }
}
