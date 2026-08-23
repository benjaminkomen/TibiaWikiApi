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

class RetrieveEffectsTest {

    private lateinit var target: RetrieveEffects
    private lateinit var articleRepository: ArticleRepository
    private lateinit var articleFactory: ArticleFactory
    private lateinit var jsonFactory: JsonFactory

    @BeforeEach
    fun setup() {
        articleRepository = mock(ArticleRepository::class.java)
        articleFactory = ArticleFactory()
        jsonFactory = mock(JsonFactory::class.java)
        target = RetrieveEffects(articleRepository, articleFactory, jsonFactory)

        doReturn(SOME_JSON_OBJECT).`when`(jsonFactory).convertInfoboxPartOfArticleToJson("")
    }

    @Test
    fun testGetEffectsJSON_ZeroResults() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.EFFECT.categoryName)
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)

        val result = target.effectsJSON.toList()

        assertThat(result, hasSize(0))
    }

    @Test
    fun testGetEffectsJSON_TwoResults() {
        val names = listOf(SOME_EFFECT_NAME, SOME_OTHER_EFFECT_NAME)
        val pages = mapOf(SOME_EFFECT_NAME to SOME_ARTICLE_CONTENT, SOME_OTHER_EFFECT_NAME to SOME_ARTICLE_CONTENT)

        doReturn(names).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.EFFECT.categoryName)
        doReturn(listOf(SOME_LIST_NAME)).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(pages).`when`(articleRepository).getArticlesFromCategory(anyList())

        val result = target.effectsJSON.toList()

        assertThat(result, hasSize(2))
    }

    @Test
    fun testGetEffectJSON() {
        doReturn("").`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Optional<Map<String, Any>> = target.getEffectJSON(SOME_PAGE_NAME)

        assertThat(result.orElseThrow(), `is`(SOME_JSON_OBJECT))
    }

    companion object {
        private const val SOME_PAGE_NAME = "Foobar"
        private const val SOME_ARTICLE_CONTENT = ""
        private val SOME_JSON_OBJECT = emptyMap<String, Any>()
        private const val SOME_EFFECT_NAME = "Yellow Poison Effect"
        private const val SOME_OTHER_EFFECT_NAME = "Ice Missile Effect"
        private const val SOME_LIST_NAME = "EffectList"
    }
}
