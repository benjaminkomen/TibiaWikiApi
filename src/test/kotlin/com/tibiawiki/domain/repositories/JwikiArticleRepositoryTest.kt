package com.tibiawiki.domain.repositories

import io.github.fastily.jwiki.core.NS
import io.github.fastily.jwiki.core.Wiki
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock

class JwikiArticleRepositoryTest {

    private lateinit var target: JwikiArticleRepository
    private lateinit var wiki: Wiki

    @BeforeEach
    fun setup() {
        wiki = mock(Wiki::class.java)
        target = JwikiArticleRepository(wiki)
    }

    @Test
    fun testGetPageNamesFromCategory() {
        doReturn(arrayListOf("foo", "bar")).`when`(wiki).getCategoryMembers(SOME_CATEGORY_NAME, NS.MAIN)
        val result = target.getPageNamesFromCategory(SOME_CATEGORY_NAME)

        assertThat(result, notNullValue())
        assertThat(result[0], `is`("foo"))
        assertThat(result[1], `is`("bar"))
    }

    @Test
    fun testGetPageNamesUsingTemplate() {
        doReturn(arrayListOf("foo", "bar")).`when`(wiki).whatTranscludesHere(SOME_TEMPLATE_NAME, NS.MAIN)
        val result = target.getPageNamesUsingTemplate(SOME_TEMPLATE_NAME)

        assertThat(result, notNullValue())
        assertThat(result[0], `is`("foo"))
        assertThat(result[1], `is`("bar"))
    }

    @Test
    fun testGetArticle_Success() {
        doReturn("Foobar").`when`(wiki).getPageText(SOME_PAGE_NAME)
        assertThat(target.getArticle(SOME_PAGE_NAME), `is`("Foobar"))
    }

    @Test
    fun testGetArticle_NullWhenEmpty() {
        doReturn("").`when`(wiki).getPageText(SOME_PAGE_NAME)
        assertThat(target.getArticle(SOME_PAGE_NAME), `is`(nullValue()))
    }

    @Test
    fun testModifyArticle_DryRunBecauseDebugEnabled() {
        target.enableDebug()
        assertThat(target.modifyArticle(SOME_PAGE_NAME, "Foobar", "[bot] formatting pages in uniform way"), `is`(true))
    }

    @Test
    fun testModifyArticle_SuccessDebugDisabled() {
        target.disableDebug()
        doReturn(true).`when`(wiki).edit(anyString(), anyString(), anyString())
        assertThat(target.modifyArticle(SOME_PAGE_NAME, "Foobar", "[bot] formatting pages in uniform way"), `is`(true))
    }

    @Test
    fun testModifyArticle_FailureDebugDisabled() {
        target.disableDebug()
        doReturn(false).`when`(wiki).edit(anyString(), anyString(), anyString())
        assertThat(target.modifyArticle(SOME_PAGE_NAME, "Foobar", "[bot] formatting pages in uniform way"), `is`(false))
    }

    @Test
    fun testLoginSuccess() {
        doReturn(true).`when`(wiki).login(anyString(), anyString())
        assertThat(target.login(wiki), `is`(true))
    }

    @Test
    fun testLoginFailure() {
        doReturn(false).`when`(wiki).login(anyString(), anyString())
        assertThat(target.login(wiki), `is`(false))
    }

    companion object {
        private const val SOME_CATEGORY_NAME = "Achievements"
        private const val SOME_PAGE_NAME = "Goo Goo Dancer"
        private const val SOME_TEMPLATE_NAME = "Template:Infobox_Item"
    }
}
