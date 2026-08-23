package com.tibiawiki.process

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import java.util.Optional

class RetrieveBuildingsTest {

    private lateinit var target: RetrieveBuildings
    private lateinit var articleRepository: ArticleRepository
    private lateinit var articleFactory: ArticleFactory
    private lateinit var jsonFactory: JsonFactory

    @BeforeEach
    fun setup() {
        articleRepository = mock(ArticleRepository::class.java)
        articleFactory = ArticleFactory()
        jsonFactory = mock(JsonFactory::class.java)
        target = RetrieveBuildings(articleRepository, articleFactory, jsonFactory)

        doReturn(SOME_JSON_OBJECT).`when`(jsonFactory).convertInfoboxPartOfArticleToJson("")
    }

    @Test
    fun testGetBuildingsJSON_ZeroResults() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.BUILDING.categoryName)
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)

        val result = target.buildingsJSON.toList()

        assertThat(result, hasSize(0))
    }

    @Test
    fun testGetBuildingsJSON_TwoResults() {
        val names = listOf(SOME_BUILDING_NAME, SOME_OTHER_BUILDING_NAME)
        val pages = mapOf(SOME_BUILDING_NAME to SOME_ARTICLE_CONTENT, SOME_OTHER_BUILDING_NAME to SOME_ARTICLE_CONTENT)

        doReturn(names).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.BUILDING.categoryName)
        doReturn(listOf(SOME_LIST_NAME)).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(pages).`when`(articleRepository).getArticlesFromCategory(anyList())

        val result = target.buildingsJSON.toList()

        assertThat(result, hasSize(2))
    }

    @Test
    fun testGetBuildingJSON() {
        doReturn("").`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Optional<Map<String, Any>> = target.getBuildingJSON(SOME_PAGE_NAME)

        assertThat(result.orElseThrow(), `is`(SOME_JSON_OBJECT))
    }

    companion object {
        private const val SOME_PAGE_NAME = "Foobar"
        private const val SOME_ARTICLE_CONTENT = ""
        private val SOME_JSON_OBJECT = emptyMap<String, Any>()
        private const val SOME_BUILDING_NAME = "Great Willow 4a"
        private const val SOME_OTHER_BUILDING_NAME = "Darashia 3, Flat 04"
        private const val SOME_LIST_NAME = "BuildingList"
    }
}
