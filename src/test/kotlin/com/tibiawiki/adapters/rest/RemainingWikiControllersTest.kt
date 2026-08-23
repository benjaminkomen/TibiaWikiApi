package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.RetrieveWikiPages
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.WikiObjectFixtures
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveBuildings
import com.tibiawiki.process.RetrieveCharms
import com.tibiawiki.process.RetrieveCorpses
import com.tibiawiki.process.RetrieveEffects
import com.tibiawiki.process.RetrieveKeys
import com.tibiawiki.process.RetrieveLocations
import com.tibiawiki.process.RetrieveMissiles
import com.tibiawiki.process.RetrieveMounts
import com.tibiawiki.process.RetrieveNPCs
import com.tibiawiki.process.RetrieveObjects
import com.tibiawiki.process.RetrieveOutfits
import com.tibiawiki.process.RetrieveQuests
import com.tibiawiki.process.RetrieveSpells
import com.tibiawiki.process.RetrieveStreets
import io.vavr.control.Try
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.Optional
import java.util.stream.Stream

class RemainingWikiControllersTest {

    private lateinit var modifyAny: ModifyAny
    private val json = JSONObject().put("name", "Foo")
    private val names = listOf("Foo")

    @BeforeEach
    fun setup() {
        modifyAny = mock(ModifyAny::class.java)
    }

    @Test
    fun charms() {
        val retrieve = mock(RetrieveCharms::class.java)
        doReturn(names).`when`(retrieve).charmsList
        doReturn(Stream.of(json)).`when`(retrieve).charmsJSON
        doReturn(Optional.of(json)).`when`(retrieve).getCharmJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getCharmJSON("Missing")
        val body = WikiObjectFixtures.charm()
        stubModify(body)
        val c = CharmsController(retrieve, modifyAny)
        assertStandard(c.getCharms(false), c.getCharms(true), c.getCharmsByName("Foo"), { c.getCharmsByName("Missing") }, c.putCharm(body, "edit"))
    }

    @Test
    fun buildings() {
        val retrieve = mock(RetrieveBuildings::class.java)
        doReturn(names).`when`(retrieve).buildingsList
        doReturn(Stream.of(json)).`when`(retrieve).buildingsJSON
        doReturn(Optional.of(json)).`when`(retrieve).getBuildingJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getBuildingJSON("Missing")
        val body = WikiObjectFixtures.building()
        stubModify(body)
        val c = BuildingsController(retrieve, modifyAny)
        assertStandard(c.getBuildings(false), c.getBuildings(true), c.getBuildingsByName("Foo"), { c.getBuildingsByName("Missing") }, c.putBuilding(body, "edit"))
    }

    @Test
    fun corpses() {
        val retrieve = mock(RetrieveCorpses::class.java)
        doReturn(names).`when`(retrieve).corpsesList
        doReturn(Stream.of(json)).`when`(retrieve).corpsesJSON
        doReturn(Optional.of(json)).`when`(retrieve).getCorpseJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getCorpseJSON("Missing")
        val body = WikiObjectFixtures.corpse()
        stubModify(body)
        val c = CorpsesController(retrieve, modifyAny)
        assertStandard(c.getCorpses(false), c.getCorpses(true), c.getCorpsesByName("Foo"), { c.getCorpsesByName("Missing") }, c.putCorpse(body, "edit"))
    }

    @Test
    fun effects() {
        val retrieve = mock(RetrieveEffects::class.java)
        doReturn(names).`when`(retrieve).effectsList
        doReturn(Stream.of(json)).`when`(retrieve).effectsJSON
        doReturn(Optional.of(json)).`when`(retrieve).getEffectJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getEffectJSON("Missing")
        val body = WikiObjectFixtures.effect()
        stubModify(body)
        val c = EffectsController(retrieve, modifyAny)
        assertStandard(c.getEffects(false), c.getEffects(true), c.getEffectsByName("Foo"), { c.getEffectsByName("Missing") }, c.putEffect(body, "edit"))
    }

    @Test
    fun keys() {
        val retrieve = mock(RetrieveKeys::class.java)
        doReturn(names).`when`(retrieve).keysList
        doReturn(Stream.of(json)).`when`(retrieve).keysJSON
        doReturn(Optional.of(json)).`when`(retrieve).getKeyJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getKeyJSON("Missing")
        val body = WikiObjectFixtures.key()
        stubModify(body)
        val c = KeysController(retrieve, modifyAny)
        assertStandard(c.getKeys(false), c.getKeys(true), c.getKeysByName("Foo"), { c.getKeysByName("Missing") }, c.putKey(body, "edit"))
    }

    @Test
    fun locations() {
        val retrieve = mock(RetrieveLocations::class.java)
        doReturn(names).`when`(retrieve).locationsList
        doReturn(Stream.of(json)).`when`(retrieve).locationsJSON
        doReturn(Optional.of(json)).`when`(retrieve).getLocationJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getLocationJSON("Missing")
        val body = WikiObjectFixtures.location()
        stubModify(body)
        val c = LocationsController(retrieve, modifyAny)
        assertStandard(c.getLocations(false), c.getLocations(true), c.getLocationsByName("Foo"), { c.getLocationsByName("Missing") }, c.putLocation(body, "edit"))
    }

    @Test
    fun missiles() {
        val retrieve = mock(RetrieveMissiles::class.java)
        doReturn(names).`when`(retrieve).missilesList
        doReturn(Stream.of(json)).`when`(retrieve).missilesJSON
        doReturn(Optional.of(json)).`when`(retrieve).getMissileJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getMissileJSON("Missing")
        val body = WikiObjectFixtures.missile()
        stubModify(body)
        val c = MissilesController(retrieve, modifyAny)
        assertStandard(c.getMissiles(false), c.getMissiles(true), c.getMissilesByName("Foo"), { c.getMissilesByName("Missing") }, c.putMissile(body, "edit"))
    }

    @Test
    fun mounts() {
        val retrieve = mock(RetrieveMounts::class.java)
        doReturn(names).`when`(retrieve).mountsList
        doReturn(Stream.of(json)).`when`(retrieve).mountsJSON
        doReturn(Optional.of(json)).`when`(retrieve).getMountJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getMountJSON("Missing")
        val body = WikiObjectFixtures.mount()
        stubModify(body)
        val c = MountsController(retrieve, modifyAny)
        assertStandard(c.getMounts(false), c.getMounts(true), c.getMountsByName("Foo"), { c.getMountsByName("Missing") }, c.putMount(body, "edit"))
    }

    @Test
    fun npcs() {
        val retrieve = mock(RetrieveNPCs::class.java)
        doReturn(names).`when`(retrieve).getNPCsList()
        doReturn(Stream.of(json)).`when`(retrieve).getNPCsJSON()
        doReturn(Optional.of(json)).`when`(retrieve).getNPCJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getNPCJSON("Missing")
        val body = WikiObjectFixtures.npc()
        stubModify(body)
        val c = NPCsController(retrieve, modifyAny)
        assertStandard(c.getNPCs(false), c.getNPCs(true), c.getNPCsByName("Foo"), { c.getNPCsByName("Missing") }, c.putNPC(body, "edit"))
    }

    @Test
    fun objects() {
        val retrieve = mock(RetrieveObjects::class.java)
        doReturn(names).`when`(retrieve).objectsList
        doReturn(Stream.of(json)).`when`(retrieve).objectsJSON
        doReturn(Optional.of(json)).`when`(retrieve).getObjectJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getObjectJSON("Missing")
        val body = WikiObjectFixtures.tibiaObject()
        stubModify(body)
        val c = ObjectsController(retrieve, modifyAny)
        assertStandard(c.getObjects(false), c.getObjects(true), c.getObjectsByName("Foo"), { c.getObjectsByName("Missing") }, c.putObject(body, "edit"))
    }

    @Test
    fun outfits() {
        val retrieve = mock(RetrieveOutfits::class.java)
        doReturn(names).`when`(retrieve).outfitsList
        doReturn(Stream.of(json)).`when`(retrieve).outfitsJSON
        doReturn(Optional.of(json)).`when`(retrieve).getOutfitJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getOutfitJSON("Missing")
        val body = WikiObjectFixtures.outfit()
        stubModify(body)
        val c = OutfitsController(retrieve, modifyAny)
        assertStandard(c.getOutfits(false), c.getOutfits(true), c.getOutfitsByName("Foo"), { c.getOutfitsByName("Missing") }, c.putOutfit(body, "edit"))
    }

    @Test
    fun quests() {
        val retrieve = mock(RetrieveQuests::class.java)
        doReturn(names).`when`(retrieve).questsList
        doReturn(Stream.of(json)).`when`(retrieve).questsJSON
        doReturn(Optional.of(json)).`when`(retrieve).getQuestJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getQuestJSON("Missing")
        val body = WikiObjectFixtures.quest()
        stubModify(body)
        val c = QuestsController(retrieve, modifyAny)
        assertStandard(c.getQuests(false), c.getQuests(true), c.getQuestsByName("Foo"), { c.getQuestsByName("Missing") }, c.putQuest(body, "edit"))
    }

    @Test
    fun spells() {
        val retrieve = mock(RetrieveSpells::class.java)
        doReturn(names).`when`(retrieve).spellsList
        doReturn(Stream.of(json)).`when`(retrieve).spellsJSON
        doReturn(Optional.of(json)).`when`(retrieve).getSpellJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getSpellJSON("Missing")
        val body = WikiObjectFixtures.spell()
        stubModify(body)
        val c = SpellsController(retrieve, modifyAny)
        assertStandard(c.getSpells(false), c.getSpells(true), c.getSpellsByName("Foo"), { c.getSpellsByName("Missing") }, c.putSpell(body, "edit"))
    }

    @Test
    fun streets() {
        val retrieve = mock(RetrieveStreets::class.java)
        doReturn(names).`when`(retrieve).streetsList
        doReturn(Stream.of(json)).`when`(retrieve).streetsJSON
        doReturn(Optional.of(json)).`when`(retrieve).getStreetJSON("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getStreetJSON("Missing")
        val body = WikiObjectFixtures.street()
        stubModify(body)
        val c = StreetsController(retrieve, modifyAny)
        assertStandard(c.getStreets(false), c.getStreets(true), c.getStreetsByName("Foo"), { c.getStreetsByName("Missing") }, c.putStreet(body, "edit"))
    }

    @Test
    fun wikiPage() {
        val retrieve = mock(RetrieveWikiPages::class.java)
        doReturn(json).`when`(retrieve).getWikiPageJSON("Foo")
        doReturn(null).`when`(retrieve).getWikiPageJSON("Missing")
        doReturn(JSONObject()).`when`(retrieve).getWikiPageJSON("Empty")
        val c = WikiPageController(retrieve)

        assertThat(c.getWikiPageByTitle("Foo").statusCode, `is`(HttpStatus.OK))
        assertThrows<ArticleNotFoundException> { c.getWikiPageByTitle("Missing") }
        assertThrows<ArticleNotFoundException> { c.getWikiPageByTitle("Empty") }
    }

    private fun stubModify(wikiObject: WikiObject) {
        doReturn(Try.success(wikiObject)).`when`(modifyAny).modify(wikiObject, "edit")
    }

    private fun assertStandard(
        list: ResponseEntity<Any>,
        expanded: ResponseEntity<Any>,
        found: ResponseEntity<String>,
        missing: () -> ResponseEntity<String>,
        put: ResponseEntity<WikiObject>
    ) {
        assertThat(list.statusCode, `is`(HttpStatus.OK))
        assertThat(list.body, `is`(names))
        assertThat(expanded.statusCode, `is`(HttpStatus.OK))
        @Suppress("UNCHECKED_CAST")
        assertThat((expanded.body as Iterable<Any>).toList(), hasSize(1))
        assertThat(found.statusCode, `is`(HttpStatus.OK))
        assertThrows<ArticleNotFoundException> { missing() }
        assertThat(put.statusCode, `is`(HttpStatus.OK))
    }
}
