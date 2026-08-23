package com.tibiawiki.domain.enums

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class VocationTest {

    @Test
    fun parseVoc_nullOrBlank() {
        assertThat(Vocation.parseVoc(null), empty())
        assertThat(Vocation.parseVoc(""), empty())
        assertThat(Vocation.parseVoc("   "), empty())
    }

    @Test
    fun parseVoc_wikiLinksIncludingMonk() {
        assertThat(
            Vocation.parseVoc("[[Monk]]s"),
            contains(Vocation.MONK)
        )
        assertThat(
            Vocation.parseVoc("[[Paladin]]s, [[Druid]]s and [[Sorcerer]]s"),
            contains(Vocation.PALADIN, Vocation.DRUID, Vocation.SORCERER)
        )
        assertThat(
            Vocation.parseVoc("[[Knight]]s, [[Paladin]]s, [[Druid]]s, [[Sorcerer]]s and [[Monk]]s"),
            contains(Vocation.KNIGHT, Vocation.PALADIN, Vocation.DRUID, Vocation.SORCERER, Vocation.MONK)
        )
    }

    @Test
    fun parseVoc_pipedWikiLinkAndPromotionNames() {
        assertThat(Vocation.parseVoc("[[Monk|Monks]]"), contains(Vocation.MONK))
        assertThat(
            Vocation.parseVoc("[[Elite Knight]]s and [[Exalted Monk]]s"),
            contains(Vocation.KNIGHT, Vocation.MONK)
        )
    }

    @Test
    fun parseVoc_bareWords() {
        assertThat(
            Vocation.parseVoc("Knights and Paladins"),
            contains(Vocation.KNIGHT, Vocation.PALADIN)
        )
        assertThat(Vocation.parseVoc("monks"), contains(Vocation.MONK))
    }

    @Test
    fun parseVoc_allVocationsIncludesMonk() {
        assertThat(
            Vocation.parseVoc("All vocations."),
            `is`(listOf(Vocation.KNIGHT, Vocation.PALADIN, Vocation.DRUID, Vocation.SORCERER, Vocation.MONK))
        )
    }

    @Test
    fun parseVoc_ignoresUnknownTokens() {
        assertThat(Vocation.parseVoc("[[Unknown Vocation]]s"), empty())
    }
}
