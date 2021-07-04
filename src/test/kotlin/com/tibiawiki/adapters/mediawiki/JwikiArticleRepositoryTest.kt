package com.tibiawiki.adapters.mediawiki

import org.fastily.jwiki.core.NS
import org.fastily.jwiki.core.Wiki
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock

internal class JwikiArticleRepositoryTest {

    companion object {
        private const val SOME_CATEGORY_NAME = "Achievements"
        private const val SOME_PAGE_NAME = "Goo Goo Dancer"
        private const val SOME_TEMPLATE_NAME = "Template:Infobox_Item"
    }

    private var wiki: Wiki = mock(Wiki::class.java)
    private var target: JwikiArticleRepository = JwikiArticleRepository(wiki)

    @Test
    fun testGetPageNamesFromCategory() {
        val someList = arrayListOf("foo", "bar")
        doReturn(someList).`when`(wiki).getCategoryMembers(SOME_CATEGORY_NAME, NS.MAIN)
        val result = target.getPageNamesFromCategory(SOME_CATEGORY_NAME)
        assertNotNull(result)
        assertEquals("foo", result[0])
        assertEquals("bar", result[1])
    }

    @Test
    fun testGetPageNamesUsingTemplate() {
        val someList = arrayListOf("foo", "bar")
        doReturn(someList).`when`(wiki).whatTranscludesHere(SOME_TEMPLATE_NAME, NS.MAIN)
        val result = target.getPageNamesUsingTemplate(SOME_TEMPLATE_NAME)
        assertNotNull(result)
        assertEquals("foo", result[0])
        assertEquals("bar", result[1])
    }

    @Test
    fun testGetArticle_Success() {
        doReturn("Foobar").`when`(wiki).getPageText(SOME_PAGE_NAME)
        val result = target.getArticle(SOME_PAGE_NAME)
        assertEquals("Foobar", result)
    }

    @Test
    fun testGetArticle_NullWhenEmpty() {
        doReturn("").`when`(wiki).getPageText(SOME_PAGE_NAME)
        val result = target.getArticle(SOME_PAGE_NAME)
        assertNull(result)
    }

    @Test
    fun testModifyArticle_DryRunBecauseDebugEnabled() {
        target.enableDebug()
        val result = target.modifyArticle(SOME_PAGE_NAME, "Foobar", "[bot] formatting pages in uniform way")
        assertTrue(result)
    }

    @Test
    fun testModifyArticle_SuccessDebugDisabled() {
        target.disableDebug()
        doReturn(true).`when`(wiki)
            .edit(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        val result = target.modifyArticle(SOME_PAGE_NAME, "Foobar", "[bot] formatting pages in uniform way")
        assertTrue(result)
    }

    @Test
    fun testModifyArticle_FailureDebugDisabled() {
        target.disableDebug()
        doReturn(false).`when`(wiki)
            .edit(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        val result = target.modifyArticle(SOME_PAGE_NAME, "Foobar", "[bot] formatting pages in uniform way")
        assertFalse(result)
    }

    @Test
    fun testLoginSuccess() {
        doReturn(true).`when`(wiki).login(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        val result = target.login(wiki)
        assertTrue(result)
    }

    @Test
    fun testLoginFailure() {
        doReturn(false).`when`(wiki).login(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        val result = target.login(wiki)
        assertFalse(result)
    }
}
