package com.tibiawiki.domain.objects

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class WikiNamespaceTest {

    @Test
    fun mainIsNamespaceZero() {
        assertThat(WikiNamespace.MAIN.id, `is`(0))
        assertThat(WikiNamespace.MAIN.prefix, `is`(""))
    }

    @Test
    fun lootStatisticsIsNamespace112() {
        assertThat(WikiNamespace.LOOT_STATISTICS.id, `is`(112))
        assertThat(WikiNamespace.LOOT_STATISTICS.prefix, `is`("Loot Statistics"))
    }
}
