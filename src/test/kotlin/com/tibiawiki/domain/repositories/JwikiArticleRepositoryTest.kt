package com.tibiawiki.domain.repositories

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.tibiawiki.config.WikiClientProperties
import com.tibiawiki.domain.objects.WikiNamespace
import com.tibiawiki.domain.wiki.ExpandTooLargeException
import com.tibiawiki.domain.wiki.WikiCallSupport
import com.tibiawiki.domain.wiki.WikiFactory
import com.tibiawiki.domain.wiki.WikiResponseCache
import io.github.fastily.jwiki.core.NS
import io.github.fastily.jwiki.core.Wiki
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.slf4j.LoggerFactory

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
    fun testGetPageNamesFromCategoryWithLootNamespace() {
        doReturn(NS.CATEGORY).`when`(wiki).getNS("Loot Statistics")
        doReturn(arrayListOf("Loot Statistics:Amazon")).`when`(wiki)
            .getCategoryMembers(SOME_LOOT_CATEGORY_NAME, NS.CATEGORY)

        val result = target.getPageNamesFromCategory(SOME_LOOT_CATEGORY_NAME, WikiNamespace.LOOT_STATISTICS)

        assertThat(result, `is`(listOf("Loot Statistics:Amazon")))
        verify(wiki).getNS("Loot Statistics")
        verify(wiki).getCategoryMembers(SOME_LOOT_CATEGORY_NAME, NS.CATEGORY)
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
    fun modifyArticleDoesNotLogWikitextAtInfo() {
        val logger = LoggerFactory.getLogger(JwikiArticleRepository::class.java) as Logger
        val appender = ListAppender<ILoggingEvent>()
        appender.start()
        logger.addAppender(appender)
        val previousLevel = logger.level
        logger.level = Level.INFO
        try {
            target.disableDebug()
            doReturn(true).`when`(wiki).edit(anyString(), anyString(), anyString())
            val wikitext = "{{Infobox Creature|secret=do-not-log-this-wikitext}}"
            target.modifyArticle(SOME_PAGE_NAME, wikitext, "[bot] formatting pages in uniform way")

            val messages = appender.list
                .filter { it.level == Level.INFO }
                .map { it.formattedMessage }
            assertThat(messages.any { it.contains(wikitext) }, `is`(false))
            assertThat(messages.any { it.contains("secret=do-not-log-this-wikitext") }, `is`(false))
            assertThat(messages.any { it.contains(SOME_PAGE_NAME) }, `is`(true))
            assertThat(messages.any { it.contains("${wikitext.length} characters") }, `is`(true))
        } finally {
            logger.level = previousLevel
            logger.detachAppender(appender)
            appender.stop()
        }
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

    @Test
    fun constructorDoesNotInitializeWiki() {
        val factory = mock(WikiFactory::class.java)
        JwikiArticleRepository(
            WikiClientProperties(),
            factory,
            WikiResponseCache.disabled(),
            WikiCallSupport.direct()
        )
        verify(factory, never()).get()
    }

    @Test
    fun firstReadInitializesWikiLazily() {
        val factory = mock(WikiFactory::class.java)
        doReturn(wiki).`when`(factory).get()
        doReturn("Hi").`when`(wiki).getPageText(SOME_PAGE_NAME)
        val repo = JwikiArticleRepository(
            WikiClientProperties(),
            factory,
            WikiResponseCache.disabled(),
            WikiCallSupport.direct()
        )
        verify(factory, never()).get()
        assertThat(repo.getArticle(SOME_PAGE_NAME), `is`("Hi"))
        verify(factory, times(1)).get()
    }

    @Test
    fun categoryMembersAreCached() {
        val repo = resilientRepo()
        doReturn(arrayListOf("foo")).`when`(wiki).getCategoryMembers(SOME_CATEGORY_NAME, NS.MAIN)
        assertThat(repo.getPageNamesFromCategory(SOME_CATEGORY_NAME), `is`(listOf("foo")))
        assertThat(repo.getPageNamesFromCategory(SOME_CATEGORY_NAME), `is`(listOf("foo")))
        verify(wiki, times(1)).getCategoryMembers(SOME_CATEGORY_NAME, NS.MAIN)
    }

    @Test
    fun articleWikitextIsCached() {
        val repo = resilientRepo()
        doReturn("Foobar").`when`(wiki).getPageText(SOME_PAGE_NAME)
        assertThat(repo.getArticle(SOME_PAGE_NAME), `is`("Foobar"))
        assertThat(repo.getArticle(SOME_PAGE_NAME), `is`("Foobar"))
        verify(wiki, times(1)).getPageText(SOME_PAGE_NAME)
    }

    @Test
    fun expandRejectedWhenOverMaxPages() {
        val properties = WikiClientProperties().apply { expand.maxPages = 2 }
        val repo = JwikiArticleRepository(
            properties,
            WikiFactory.fixed(wiki),
            WikiResponseCache.disabled(),
            WikiCallSupport.direct()
        )
        val thrown = assertThrows<ExpandTooLargeException> {
            repo.getArticlesFromCategory(listOf("a", "b", "c"))
        }
        assertThat(thrown.requested, `is`(3))
        assertThat(thrown.max, `is`(2))
    }

    @Test
    fun expandUsesCachedArticleTextWithoutBulkFetch() {
        val repo = resilientRepo()
        doReturn("Foobar").`when`(wiki).getPageText(SOME_PAGE_NAME)
        repo.getArticle(SOME_PAGE_NAME)
        val expanded = repo.getArticlesFromCategory(listOf(SOME_PAGE_NAME))
        assertThat(expanded[SOME_PAGE_NAME], `is`("Foobar"))
    }

    private fun resilientRepo(): JwikiArticleRepository {
        return JwikiArticleRepository(
            WikiClientProperties(),
            WikiFactory.fixed(wiki),
            WikiResponseCache(WikiClientProperties()),
            WikiCallSupport.direct()
        )
    }

    companion object {
        private const val SOME_CATEGORY_NAME = "Achievements"
        private const val SOME_LOOT_CATEGORY_NAME = "Loot Statistics"
        private const val SOME_PAGE_NAME = "Goo Goo Dancer"
        private const val SOME_TEMPLATE_NAME = "Template:Infobox_Item"
    }
}
