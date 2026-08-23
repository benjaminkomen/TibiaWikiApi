package com.tibiawiki.domain.objects

import com.tibiawiki.config.JacksonConfiguration
import com.tibiawiki.domain.enums.WeaponType
import com.tibiawiki.domain.enums.YesNo
import com.tibiawiki.domain.factories.WikiObjectFactory
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper

class TibiaObjectMappingTest {

    private lateinit var wikiObjectFactory: WikiObjectFactory

    @BeforeEach
    fun setup() {
        val builder = JsonMapper.builder()
        JacksonConfiguration().jsonMapperBuilderCustomizer().customize(builder)
        wikiObjectFactory = WikiObjectFactory(builder.build())
    }

    @Test
    fun mapsLiveInfoboxObjectParametersAndWeaponTypes() {
        val wikiJson = mapOf(
            "templateType" to "Object",
            "objectclass" to "Weapons",
            "weapontype" to "Wand",
            "slot" to "two-handed",
            "augments" to "{{Augments|Critical Extra Damage +4%}}",
            "task_item" to "yes",
            "wrappable" to "yes",
            "cooldown" to "2",
            "basepower" to "56"
        )

        val result = wikiObjectFactory.createWikiObject(wikiJson)

        assertThat(result, instanceOf(TibiaObject::class.java))
        val tibiaObject = result as TibiaObject
        assertThat(tibiaObject.weapontype, `is`(WeaponType.Wand))
        assertThat(tibiaObject.slot, `is`("two-handed"))
        assertThat(tibiaObject.augments, `is`("{{Augments|Critical Extra Damage +4%}}"))
        assertThat(tibiaObject.taskItem, `is`(YesNo.YES_LOWERCASE))
        assertThat(tibiaObject.wrappable, `is`(YesNo.YES_LOWERCASE))
        assertThat(tibiaObject.cooldown, `is`(2))
        assertThat(tibiaObject.basepower, `is`(56))
    }

    @Test
    fun mapsWikiWeaponTypesWandRodFist() {
        assertThat(mapWeaponType("Wand"), `is`(WeaponType.Wand))
        assertThat(mapWeaponType("Rod"), `is`(WeaponType.Rod))
        assertThat(mapWeaponType("Fist"), `is`(WeaponType.Fist))
        assertThat(mapWeaponType("Sword"), `is`(WeaponType.Sword))
        assertThat(mapWeaponType("Distance"), `is`(WeaponType.Distance))
    }

    @Test
    fun fieldOrderIncludesLiveObjectParameters() {
        val fieldOrder = TibiaObject(objectclass = "Weapons").fieldOrder()

        assertThat(
            fieldOrder,
            hasItems("augments", "task_item", "wrappable", "cooldown", "basepower", "slot", "weapontype")
        )
        assertThat(fieldOrder.indexOf("slot") > fieldOrder.indexOf("weapontype"), `is`(true))
        assertThat(fieldOrder.indexOf("basepower") > fieldOrder.indexOf("manacost"), `is`(true))
        assertThat(fieldOrder.indexOf("augments") > fieldOrder.indexOf("attrib"), `is`(true))
        assertThat(fieldOrder.indexOf("task_item") > fieldOrder.indexOf("marketable"), `is`(true))
        assertThat(fieldOrder.indexOf("wrappable") > fieldOrder.indexOf("hangable"), `is`(true))
        assertThat(fieldOrder.indexOf("cooldown") > fieldOrder.indexOf("duration"), `is`(true))
    }

    @Test
    fun writeBackUsesWikiKeysForLiveObjectParameters() {
        val tibiaObject = TibiaObject(
            objectclass = "Weapons",
            weapontype = WeaponType.Rod,
            slot = "two-handed",
            augments = "{{Augments|Life Leech +1.2%}}",
            taskItem = YesNo.YES_LOWERCASE,
            wrappable = YesNo.NO_LOWERCASE,
            cooldown = 60,
            basepower = 42
        )

        val json = wikiObjectFactory.createJSONObject(tibiaObject, tibiaObject.getTemplateType())

        assertThat(json.get("weapontype"), `is`("Rod"))
        assertThat(json.get("slot"), `is`("two-handed"))
        assertThat(json.get("augments"), `is`("{{Augments|Life Leech +1.2%}}"))
        assertThat(json.get("task_item"), `is`("yes"))
        assertThat(json.get("wrappable"), `is`("no"))
        assertThat(json.get("cooldown"), `is`(60))
        assertThat(json.get("basepower"), `is`(42))
    }

    private fun mapWeaponType(weapontype: String): WeaponType? {
        val wikiJson = mapOf(
            "templateType" to "Object",
            "objectclass" to "Weapons",
            "weapontype" to weapontype
        )
        return (wikiObjectFactory.createWikiObject(wikiJson) as TibiaObject).weapontype
    }
}
