package com.tibiawiki.domain.objects

import com.tibiawiki.config.JacksonConfiguration
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.factories.WikiObjectFactory
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper

class CreatureTest {

    private lateinit var wikiObjectFactory: WikiObjectFactory
    private lateinit var jsonFactory: JsonFactory

    @BeforeEach
    fun setup() {
        val builder = JsonMapper.builder()
        JacksonConfiguration().jsonMapperBuilderCustomizer().customize(builder)
        wikiObjectFactory = WikiObjectFactory(builder.build())
        jsonFactory = JsonFactory()
    }

    @Test
    fun fieldOrderIncludesMitigationBossCooldownAndMaxBattleLength() {
        val fieldOrder = Creature().fieldOrder()

        assertThat(fieldOrder, `is`(EXPECTED_FIELD_ORDER))
        assertThat(fieldOrder.indexOf("mitigation"), `is`(fieldOrder.indexOf("armor") + 1))
        assertThat(fieldOrder.indexOf("cooldown"), `is`(fieldOrder.indexOf("bosstiaryclass") + 1))
        assertThat(fieldOrder.indexOf("maxbattlelength"), `is`(fieldOrder.indexOf("maxdmg") + 1))
    }

    @Test
    fun jsonFactoryGetSurfacesNewWikiParameters() {
        val json = jsonFactory.convertInfoboxPartOfArticleToJson(LIVE_SHAPE_INFOBOX)

        assertThat(json["mitigation"], `is`("2.45"))
        assertThat(json["cooldown"], `is`("20"))
        assertThat(json["maxbattlelength"], `is`("15"))
    }

    @Test
    fun typedMappingReadsWikiParametersUsedOnLivePages() {
        val wikiObject = wikiObjectFactory.createWikiObject(liveShapeCreatureJson())

        assertThat(wikiObject, instanceOf(Creature::class.java))
        val creature = wikiObject as Creature
        assertThat(creature.name, `is`("Ferumbras"))
        assertThat(creature.mitigation, `is`("2.45"))
        assertThat(creature.cooldown, `is`("20"))
        assertThat(creature.maxbattlelength, `is`("15"))
    }

    @Test
    fun fieldOrderWriteBackEmitsNewWikiParameters() {
        val creature = WikiObjectFixtures.bossCreature()
        val json = wikiObjectFactory.createJSONObject(creature, creature.getTemplateType())

        assertThat(json["mitigation"], `is`("2.45"))
        assertThat(json["cooldown"], `is`("20"))
        assertThat(json["maxbattlelength"], `is`("15"))

        val infobox = jsonFactory.convertJsonToInfoboxPartOfArticle(json, creature.fieldOrder())

        val keys = Regex("""\| (\S+)""").findAll(infobox).map { it.groupValues[1] }.toList()

        assertThat(infobox, containsString("| mitigation"))
        assertThat(infobox, containsString("= 2.45"))
        assertThat(infobox, containsString("| cooldown"))
        assertThat(infobox, containsString("= 20"))
        assertThat(infobox, containsString("| maxbattlelength"))
        assertThat(infobox, containsString("= 15"))
        assertThat(keys.indexOf("mitigation") < keys.indexOf("summon"), `is`(true))
        assertThat(keys.indexOf("cooldown") < keys.indexOf("isarenaboss"), `is`(true))
        assertThat(keys.indexOf("maxdmg") < keys.indexOf("maxbattlelength"), `is`(true))
    }

    private fun liveShapeCreatureJson(): Map<String, Any> {
        return mapOf(
            "templateType" to "Creature",
            "name" to "Ferumbras",
            "mitigation" to "2.45",
            "cooldown" to "20",
            "maxbattlelength" to "15"
        )
    }

    companion object {
        private val LIVE_SHAPE_INFOBOX = """
            {{Infobox Creature|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name           = Ferumbras
            | mitigation     = 2.45
            | cooldown       = 20
            | maxbattlelength = 15
            }}
        """.trimIndent()

        private val EXPECTED_FIELD_ORDER = listOf(
            "name", "article", "actualname", "plural", "hp", "exp", "armor", "mitigation", "summon", "convince",
            "illusionable", "creatureclass", "primarytype", "secondarytype", "bestiaryclass", "bestiarylevel",
            "occurrence", "attacktype", "usespells", "spawntype", "isboss", "bosstiaryclass", "cooldown",
            "isarenaboss", "isevent", "abilities", "usedelements", "maxdmg", "maxbattlelength", "lightradius",
            "lightcolor", "pushable", "pushobjects", "walksaround", "walksthrough", "paraimmune", "senseinvis",
            "physicalDmgMod", "earthDmgMod", "fireDmgMod", "deathDmgMod", "energyDmgMod", "holyDmgMod", "iceDmgMod",
            "hpDrainDmgMod", "drownDmgMod", "healMod", "bestiaryname", "bestiarytext", "sounds", "implemented",
            "race_id", "notes", "behaviour", "runsat", "speed", "strategy", "location", "loot", "history", "status"
        )
    }
}
