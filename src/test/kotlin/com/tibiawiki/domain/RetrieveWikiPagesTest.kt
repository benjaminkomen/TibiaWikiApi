package com.tibiawiki.domain

import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock

class RetrieveWikiPagesTest {

    private lateinit var target: RetrieveWikiPages
    private lateinit var articleRepository: ArticleRepository
    private lateinit var articleFactory: ArticleFactory
    private lateinit var jsonFactory: JsonFactory

    @BeforeEach
    fun setup() {
        articleRepository = mock(ArticleRepository::class.java)
        articleFactory = mock(ArticleFactory::class.java)
        jsonFactory = mock(JsonFactory::class.java)
        target = RetrieveWikiPages(articleRepository, articleFactory, jsonFactory)
    }

    @Test
    fun getWikiPageJSON_returnsConvertedJson() {
        doReturn(SOME_ARTICLE).`when`(articleRepository).getArticle(SOME_PAGE_NAME)
        doReturn(SOME_INFOBOX).`when`(articleFactory).extractInfoboxPartOfArticle(SOME_ARTICLE)
        doReturn(SOME_JSON).`when`(jsonFactory).convertInfoboxPartOfArticleToJson(SOME_INFOBOX)

        val result = target.getWikiPageJSON(SOME_PAGE_NAME)

        assertThat(result, `is`(SOME_JSON))
    }

    @Test
    fun getWikiPageJSON_returnsNullWhenArticleMissing() {
        doReturn(null).`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result = target.getWikiPageJSON(SOME_PAGE_NAME)

        assertThat(result, nullValue())
    }

    companion object {
        private const val SOME_PAGE_NAME = "Dragon"
        private const val SOME_ARTICLE = "{{Infobox Creature|name=Dragon}}"
        private const val SOME_INFOBOX = "|name=Dragon"
        private val SOME_JSON = JSONObject().put("name", "Dragon")
    }
}
