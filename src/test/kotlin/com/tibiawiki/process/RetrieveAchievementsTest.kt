package com.tibiawiki.process

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock

class RetrieveAchievementsTest {

    private lateinit var target: RetrieveAchievements
    private lateinit var articleRepository: ArticleRepository
    private lateinit var articleFactory: ArticleFactory
    private lateinit var jsonFactory: JsonFactory

    @BeforeEach
    fun setup() {
        articleRepository = mock(ArticleRepository::class.java)
        articleFactory = ArticleFactory()
        jsonFactory = mock(JsonFactory::class.java)
        target = RetrieveAchievements(articleRepository, articleFactory, jsonFactory)

        doReturn(SOME_JSON_OBJECT).`when`(jsonFactory).convertInfoboxPartOfArticleToJson("")
    }

    @Test
    fun testGetAchievementsJSON_ZeroResults() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ACHIEVEMENT.categoryName)
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)

        val result = target.achievementsJSON.toList()

        assertThat(result, hasSize(0))
    }

    @Test
    fun testGetAchievementsJSON_TwoResults() {
        val names = listOf(SOME_ACHIEVEMENT_NAME, SOME_OTHER_ACHIEVEMENT_NAME)
        val pages = mapOf(SOME_ACHIEVEMENT_NAME to SOME_ARTICLE_CONTENT, SOME_OTHER_ACHIEVEMENT_NAME to SOME_ARTICLE_CONTENT)

        doReturn(names).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ACHIEVEMENT.categoryName)
        doReturn(listOf(SOME_LIST_NAME)).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(pages).`when`(articleRepository).getArticlesFromCategory(anyList())

        val result = target.achievementsJSON.toList()

        assertThat(result, hasSize(2))
    }

    @Test
    fun testGetAchievementJSON() {
        doReturn("").`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Map<String, Any>? = target.getAchievementJSON(SOME_PAGE_NAME)

        assertThat(result!!, `is`(SOME_JSON_OBJECT))
    }

    @Test
    fun testGetAchievementJSON_Missing() {
        doReturn(null).`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Map<String, Any>? = target.getAchievementJSON(SOME_PAGE_NAME)

        assertThat(result, nullValue())
    }

    companion object {
        private const val SOME_PAGE_NAME = "Foobar"
        private const val SOME_ARTICLE_CONTENT = ""
        private val SOME_JSON_OBJECT = emptyMap<String, Any>()
        private const val SOME_ACHIEVEMENT_NAME = "Goo Goo Dancer"
        private const val SOME_OTHER_ACHIEVEMENT_NAME = "Fire Devil"
        private const val SOME_LIST_NAME = "Achievements/DPL"
    }
}
