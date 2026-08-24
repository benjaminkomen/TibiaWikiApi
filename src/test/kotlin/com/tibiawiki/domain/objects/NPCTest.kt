package com.tibiawiki.domain.objects

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test

class NPCTest {

    @Test
    fun fieldOrderMatchesCurrentInfoboxNpc() {
        val fieldOrder = NPC().fieldOrder()

        assertThat(
            fieldOrder,
            hasItems(
                "subarea",
                "geolabel",
                "geolabel2",
                "geolabel3",
                "geolabel4",
                "geolabel5",
                "geolabel6",
                "geolabel7",
                "posx6",
                "posy6",
                "posz6",
                "posx7",
                "posy7",
                "posz7"
            )
        )
        assertThat(fieldOrder, not(hasItems("buys", "sells")))
    }
}
