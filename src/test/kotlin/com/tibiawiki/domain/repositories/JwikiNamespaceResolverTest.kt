package com.tibiawiki.domain.repositories

import com.tibiawiki.domain.objects.WikiNamespace
import io.github.fastily.jwiki.core.NS
import io.github.fastily.jwiki.core.Wiki
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class JwikiNamespaceResolverTest {

    private lateinit var wiki: Wiki

    @BeforeEach
    fun setup() {
        wiki = mock(Wiki::class.java)
    }

    @Test
    fun resolveMainWithoutCallingWiki() {
        val resolved = JwikiNamespaceResolver.resolve(wiki, WikiNamespace.MAIN)

        assertThat(resolved, sameInstance(NS.MAIN))
        verify(wiki, never()).getNS("")
    }

    @Test
    fun resolveLootByCanonicalPrefix() {
        doReturn(NS.CATEGORY).`when`(wiki).getNS("Loot Statistics")

        val resolved = JwikiNamespaceResolver.resolve(wiki, WikiNamespace.LOOT_STATISTICS)

        assertThat(resolved, `is`(NS.CATEGORY))
        verify(wiki).getNS("Loot Statistics")
    }

    @Test
    fun resolveLootFallsBackToUnderscorePrefix() {
        doReturn(null).`when`(wiki).getNS("Loot Statistics")
        doReturn(NS.CATEGORY).`when`(wiki).getNS("Loot_Statistics")

        val resolved = JwikiNamespaceResolver.resolve(wiki, WikiNamespace.LOOT_STATISTICS)

        assertThat(resolved, `is`(NS.CATEGORY))
        verify(wiki).getNS("Loot_Statistics")
    }

    @Test
    fun resolveUnknownPrefixThrows() {
        doReturn(null).`when`(wiki).getNS("Loot Statistics")
        doReturn(null).`when`(wiki).getNS("Loot_Statistics")

        val thrown = assertThrows(JwikiNamespaceResolver.UnknownWikiNamespaceException::class.java) {
            JwikiNamespaceResolver.resolve(wiki, WikiNamespace.LOOT_STATISTICS)
        }

        assertThat(thrown.message, containsString("Loot Statistics"))
        assertThat(thrown.message, containsString("112"))
    }

    @Test
    fun resolveCustomNamespaceWithoutPrefixThrows() {
        val thrown = assertThrows(JwikiNamespaceResolver.UnknownWikiNamespaceException::class.java) {
            JwikiNamespaceResolver.resolve(wiki, WikiNamespace(id = 99, prefix = ""))
        }

        assertThat(thrown.message, containsString("99"))
    }
}
