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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class RetrieveByTemplateTest {

    private lateinit var target: RetrieveByTemplate
    private lateinit var articleRepository: ArticleRepository
    private lateinit var articleFactory: ArticleFactory
    private lateinit var jsonFactory: JsonFactory

    @BeforeEach
    fun setup() {
        articleRepository = mock(ArticleRepository::class.java)
        articleFactory = ArticleFactory()
        jsonFactory = mock(JsonFactory::class.java)
        target = RetrieveByTemplate(articleRepository, articleFactory, jsonFactory)

        doReturn(SOME_JSON_OBJECT).`when`(jsonFactory).convertInfoboxPartOfArticleToJson("")
    }

    @Test
    fun namesUsesWorldCategoryGameWorldsNotGameworlds() {
        doReturn(listOf(SOME_WORLD_NAME)).`when`(articleRepository).getPageNamesFromCategory("Game Worlds")
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)

        val result = target.names(InfoboxTemplate.WORLD)

        assertThat(result, `is`(listOf(SOME_WORLD_NAME)))
        verify(articleRepository).getPageNamesFromCategory("Game Worlds")
        verify(articleRepository, never()).getPageNamesFromCategory("Gameworlds")
    }

    @Test
    fun namesFiltersListPages() {
        val names = listOf(SOME_IMBUEMENT_NAME, SOME_OTHER_IMBUEMENT_NAME, SOME_IMBUEMENT_LIST_NAME)
        doReturn(names).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.IMBUEMENT.categoryName)
        doReturn(listOf(SOME_IMBUEMENT_LIST_NAME)).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)

        val result = target.names(InfoboxTemplate.IMBUEMENT)

        assertThat(result, `is`(listOf(SOME_IMBUEMENT_NAME, SOME_OTHER_IMBUEMENT_NAME)))
    }

    @ParameterizedTest
    @EnumSource(
        InfoboxTemplate::class,
        names = ["ACHIEVEMENT", "ITEM", "OBJECT", "HUNT", "NPC", "CREATURE"]
    )
    fun pageNamesUsesTemplateCategoryAndDropsLists(template: InfoboxTemplate) {
        doReturn(listOf(SOME_NAME, SOME_OTHER_NAME, SOME_LIST_NAME))
            .`when`(articleRepository).getPageNamesFromCategory(template.categoryName)
        doReturn(listOf(SOME_LIST_NAME)).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)

        assertThat(target.pageNames(template), `is`(listOf(SOME_NAME, SOME_OTHER_NAME)))
        verify(articleRepository).getPageNamesFromCategory(template.categoryName)
    }

    @Test
    fun asJsonZeroResults() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.FAMILIAR.categoryName)
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)

        val result = target.asJson(InfoboxTemplate.FAMILIAR)

        assertThat(result, hasSize(0))
    }

    @Test
    fun articlesAsJSON_ZeroResults() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ACHIEVEMENT.categoryName)
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)

        val result = target.articlesAsJSON(InfoboxTemplate.ACHIEVEMENT)

        assertThat(result, hasSize(0))
    }

    @Test
    fun asJsonTwoResults() {
        val names = listOf(SOME_IMBUEMENT_NAME, SOME_OTHER_IMBUEMENT_NAME)
        val pages = mapOf(SOME_IMBUEMENT_NAME to SOME_ARTICLE_CONTENT, SOME_OTHER_IMBUEMENT_NAME to SOME_ARTICLE_CONTENT)

        doReturn(names).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.UPDATE.categoryName)
        doReturn(listOf(SOME_IMBUEMENT_LIST_NAME)).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(pages).`when`(articleRepository).getArticlesFromCategory(anyList())

        val result = target.asJson(InfoboxTemplate.UPDATE)

        assertThat(result, hasSize(2))
    }

    @Test
    fun articlesAsJSON_TwoResults() {
        val names = listOf(SOME_NAME, SOME_OTHER_NAME)
        val pages = mapOf(SOME_NAME to SOME_ARTICLE_CONTENT, SOME_OTHER_NAME to SOME_ARTICLE_CONTENT)

        doReturn(names).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ITEM.categoryName)
        doReturn(listOf(SOME_LIST_NAME)).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(pages).`when`(articleRepository).getArticlesFromCategory(anyList())

        val result = target.articlesAsJSON(InfoboxTemplate.ITEM)

        assertThat(result, hasSize(2))
    }

    @Test
    fun getJson() {
        doReturn("").`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Map<String, Any>? = target.getJson(SOME_PAGE_NAME)

        assertThat(result, `is`(SOME_JSON_OBJECT))
    }

    @Test
    fun articleAsJSON() {
        doReturn("").`when`(articleRepository).getArticle(SOME_PAGE_NAME)

        val result: Map<String, Any>? = target.articleAsJSON(SOME_PAGE_NAME)

        assertThat(result, `is`(SOME_JSON_OBJECT))
    }

    companion object {
        private const val SOME_PAGE_NAME = "Foobar"
        private const val SOME_ARTICLE_CONTENT = ""
        private val SOME_JSON_OBJECT = emptyMap<String, Any>()
        private const val SOME_NAME = "Sword"
        private const val SOME_OTHER_NAME = "Shield"
        private const val SOME_LIST_NAME = "ItemList"
        private const val SOME_IMBUEMENT_NAME = "Powerful Strike"
        private const val SOME_OTHER_IMBUEMENT_NAME = "Intricate Strike"
        private const val SOME_IMBUEMENT_LIST_NAME = "ImbuementList"
        private const val SOME_WORLD_NAME = "Antica"
    }
}
