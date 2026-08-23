package com.tibiawiki.process

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import java.util.Optional

class RetrieveBooksTest {

    private lateinit var target: RetrieveBooks
    private lateinit var articleRepository: ArticleRepository
    private lateinit var articleFactory: ArticleFactory
    private lateinit var jsonFactory: JsonFactory

    @BeforeEach
    fun setup() {
        articleRepository = mock(ArticleRepository::class.java)
        articleFactory = mock(ArticleFactory::class.java)
        jsonFactory = mock(JsonFactory::class.java)
        target = RetrieveBooks(articleRepository, articleFactory, jsonFactory)

        doReturn(SOME_ARTICLE_CONTENT).`when`(articleFactory).extractInfoboxPartOfArticle(any(String::class.java))
        doReturn(SOME_JSON_OBJECT).`when`(jsonFactory).convertInfoboxPartOfArticleToJson(any(String::class.java))
    }

    @Test
    fun testGetBooksJSON_ZeroResults() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.BOOK.categoryName)
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)

        val result = target.booksJSON.toList()

        assertThat(result, hasSize(0))
    }

    @Test
    fun testGetBooksJSON_TwoResults() {
        val names = listOf(SOME_BOOK_NAME, SOME_OTHER_BOOK_NAME)
        val pages = mapOf(SOME_BOOK_NAME to SOME_ARTICLE_CONTENT, SOME_OTHER_BOOK_NAME to SOME_ARTICLE_CONTENT)

        doReturn(names).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.BOOK.categoryName)
        doReturn(listOf(SOME_LIST_NAME)).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(pages).`when`(articleRepository).getArticlesFromCategory(anyList())

        val result = target.booksJSON.toList()

        assertThat(result, hasSize(2))
    }

    @Test
    fun testGetBookJSON() {
        doReturn("").`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Optional<JSONObject> = target.getBookJSON(SOME_PAGE_NAME)

        assertThat(result.orElseThrow(), `is`(SOME_JSON_OBJECT))
    }

    companion object {
        private const val SOME_PAGE_NAME = "Foobar"
        private const val SOME_ARTICLE_CONTENT = ""
        private val SOME_JSON_OBJECT = JSONObject()
        private const val SOME_BOOK_NAME = "Dungeon Survival Guide (Book)"
        private const val SOME_OTHER_BOOK_NAME = "Explorer Journal of Wilmot Dustheart (Book)"
        private const val SOME_LIST_NAME = "BookList"
    }
}
