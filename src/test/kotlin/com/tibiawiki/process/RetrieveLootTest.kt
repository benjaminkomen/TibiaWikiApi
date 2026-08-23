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
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import java.util.Optional

class RetrieveLootTest {

    private lateinit var target: RetrieveLoot
    private lateinit var articleRepository: ArticleRepository
    private val lootNamespace: NS = RetrieveLoot.makeLootNamespace(112)

    @BeforeEach
    fun setup() {
        articleRepository = mock(ArticleRepository::class.java)
        target = RetrieveLoot(articleRepository, ArticleFactory(), JsonFactory())
    }

    @Test
    fun testGetLootList() {
        doReturn(listOf(SOME_LOOT_NAME, SOME_OTHER_LOOT_NAME))
            .`when`(articleRepository)
            .getPageNamesFromCategory("Loot Statistics", lootNamespace)

        val result = target.getLootList()

        assertThat(result, hasSize(2))
        assertThat(result[0], `is`(SOME_LOOT_NAME))
    }

    @Test
    fun testGetLootJSONObject_ZeroResults() {
        doReturn(emptyList<String>())
            .`when`(articleRepository)
            .getPageNamesFromCategory("Loot Statistics", lootNamespace)

        val result = target.getLootJSONObject().toList()

        assertThat(result, hasSize(0))
    }

    @Test
    fun testGetLootJSONObject_TwoResults() {
        stubLootPages()

        val result = target.getLootJSONObject().toList()

        assertThat(result, hasSize(2))
        assertThat(result[0].getString("name"), `is`("Amazon"))
        assertThat(result[0].getString("kills"), `is`("22009"))
    }

    @Test
    fun testGetLootJSONObjectByName() {
        doReturn(LOOT_AMAZON_TEXT).`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Optional<JSONObject> = target.getLootJSONObject(SOME_PAGE_NAME)

        assertThat(result.orElseThrow().getString("name"), `is`("Amazon"))
        assertThat(result.get().getString("pageName"), `is`(SOME_PAGE_NAME))
    }

    @Test
    fun testGetLootJSONObjectByName_Missing() {
        doReturn(null).`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result = target.getLootJSONObject(SOME_PAGE_NAME)

        assertThat(result.isEmpty, `is`(true))
    }

    @Test
    fun testGetAllLootPartsJSON_TwoResults() {
        stubLootPages()

        val result = target.getAllLootPartsJSON().toList()

        assertThat(result, hasSize(2))
        assertThat(result[0].getJSONObject("loot2").getString("name"), `is`("Amazon"))
    }

    @Test
    fun testGetAllLootPartsJSONByName() {
        doReturn(LOOT_AMAZON_TEXT).`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Optional<JSONObject> = target.getAllLootPartsJSON(SOME_PAGE_NAME)

        assertThat(result.orElseThrow().getJSONObject("loot2").getString("name"), `is`("Amazon"))
        assertThat(result.get().getJSONObject("loot2").getString("pageName"), `is`(SOME_PAGE_NAME))
    }

    @Test
    fun testGetAllLootPartsJSONByName_Missing() {
        doReturn(null).`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result = target.getAllLootPartsJSON(SOME_PAGE_NAME)

        assertThat(result.isEmpty, `is`(true))
    }

    @Test
    fun testMakeLootNamespace() {
        val namespace = RetrieveLoot.makeLootNamespace(112)

        assertThat(namespace, notNullValue())
    }

    private fun stubLootPages() {
        val names = listOf(SOME_LOOT_NAME, SOME_OTHER_LOOT_NAME)
        val pages = mapOf(
            SOME_LOOT_NAME to LOOT_AMAZON_TEXT,
            SOME_OTHER_LOOT_NAME to LOOT_AMAZON_TEXT
        )
        doReturn(names)
            .`when`(articleRepository)
            .getPageNamesFromCategory("Loot Statistics", lootNamespace)
        doReturn(pages).`when`(articleRepository).getArticlesFromCategory(names)
    }

    companion object {
        private const val SOME_PAGE_NAME = "Loot_Statistics:Amazon"
        private const val SOME_LOOT_NAME = "Loot:Amazon"
        private const val SOME_OTHER_LOOT_NAME = "Loot:Dragon"
        private val LOOT_AMAZON_TEXT =
            """
            {{Loot2
            |version=8.6
            |kills=22009
            |name=Amazon
            |Empty, times:253
            }}
            """.trimIndent()
    }
}
