package com.tibiawiki.domain.objects

import com.tibiawiki.config.JacksonConfiguration
import com.tibiawiki.domain.enums.Vocation
import com.tibiawiki.domain.factories.WikiObjectFactory
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.json.JSONObject
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper

class SpellVocationsTest {

    @Test
    fun vocations_parsesMonkFromWikiVoc() {
        val spell = Spell(
            name = "Light Healing",
            voc = "[[Paladin]]s, [[Druid]]s, [[Sorcerer]]s and [[Monk]]s"
        )

        assertThat(
            spell.vocations,
            contains(Vocation.PALADIN, Vocation.DRUID, Vocation.SORCERER, Vocation.MONK)
        )
    }

    @Test
    fun wikiObjectFactory_acceptsVocWithMonk() {
        val builder = JsonMapper.builder()
        JacksonConfiguration().jsonMapperBuilderCustomizer().customize(builder)
        val factory = WikiObjectFactory(builder.build())

        val json = JSONObject()
            .put("templateType", "Spell")
            .put("name", "Light Healing")
            .put("voc", "[[Monk]]s")

        val result = factory.createWikiObject(json)

        assertThat(result, instanceOf(Spell::class.java))
        val spell = result as Spell
        assertThat(spell.voc, `is`("[[Monk]]s"))
        assertThat(spell.vocations, contains(Vocation.MONK))
    }
}
