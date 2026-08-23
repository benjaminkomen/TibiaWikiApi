package com.tibiawiki.domain.objects

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test

class SpellTest {

    @Test
    fun fieldOrderIncludesCurrentWikiFields() {
        val fieldOrder = Spell().fieldOrder()

        assertThat(
            fieldOrder,
            hasItems(
                "spellid",
                "libraryname",
                "librarytext",
                "basepower",
                "wheelspell",
                "passivespell",
                "cooldown2",
                "cooldown3",
                "secondarygroup"
            )
        )
    }

    @Test
    fun fieldOrderKeepsRetiredKeysForLeftoverPages() {
        val fieldOrder = Spell().fieldOrder()

        assertThat(
            fieldOrder,
            hasItems("zoltanonly", "specialspell", "conjurespell", "d-tha", "s-yal")
        )
        assertThat(fieldOrder, not(hasItems("templateType")))
    }
}
