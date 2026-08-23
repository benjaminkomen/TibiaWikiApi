package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.RetrieveWikiPages
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
import org.hamcrest.Matchers.`is`
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import java.util.Optional
import java.util.stream.Stream

class RemainingWikiControllersTest {

    @Test
    fun buildingsController_coversListGetAndPut() {
        val retrieve = mock(RetrieveBuildings::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = BuildingsController(retrieve, modifyAny)
        val names = listOf("Depot", "House")
        doReturn(names).`when`(retrieve).buildingsList
        doReturn(Stream.of(JSONObject().put("name", "Depot"))).`when`(retrieve).buildingsJSON
        doReturn(Optional.of(JSONObject().put("name", "Depot"))).`when`(retrieve).getBuildingJSON("Depot")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getBuildingJSON("Missing")
        val building = WikiObjectFixtures.building()
        doReturn(Try.success(building)).`when`(modifyAny).modify(building, "edit")

        assertThat(controller.getBuildings(false).body, `is`(names))
        assertThat(controller.getBuildings(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getBuildingsByName("Depot").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getBuildingsByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
        assertThat(controller.putBuilding(building, "edit").statusCode, `is`(HttpStatus.OK))
    }

    @Test
    fun charmsController_coversListAndGet() {
        val retrieve = mock(RetrieveCharms::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = CharmsController(retrieve, modifyAny)
        doReturn(listOf("Dodge")).`when`(retrieve).charmsList
        doReturn(Stream.of(JSONObject().put("name", "Dodge"))).`when`(retrieve).charmsJSON
        doReturn(Optional.of(JSONObject().put("name", "Dodge"))).`when`(retrieve).getCharmJSON("Dodge")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getCharmJSON("Missing")

        assertThat(controller.getCharms(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getCharms(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getCharmsByName("Dodge").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getCharmsByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun corpsesController_coversListAndGet() {
        val retrieve = mock(RetrieveCorpses::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = CorpsesController(retrieve, modifyAny)
        doReturn(listOf("Dead Rat")).`when`(retrieve).corpsesList
        doReturn(Stream.of(JSONObject().put("name", "Dead Rat"))).`when`(retrieve).corpsesJSON
        doReturn(Optional.of(JSONObject().put("name", "Dead Rat"))).`when`(retrieve).getCorpseJSON("Dead Rat")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getCorpseJSON("Missing")

        assertThat(controller.getCorpses(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getCorpses(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getCorpsesByName("Dead Rat").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getCorpsesByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun effectsController_coversListAndGet() {
        val retrieve = mock(RetrieveEffects::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = EffectsController(retrieve, modifyAny)
        doReturn(listOf("Blue Electricity Effect")).`when`(retrieve).effectsList
        doReturn(Stream.of(JSONObject().put("name", "Blue Electricity Effect"))).`when`(retrieve).effectsJSON
        doReturn(Optional.of(JSONObject().put("name", "Blue Electricity Effect"))).`when`(retrieve).getEffectJSON("Blue Electricity Effect")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getEffectJSON("Missing")

        assertThat(controller.getEffects(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getEffects(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getEffectsByName("Blue Electricity Effect").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getEffectsByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun keysController_coversListAndGet() {
        val retrieve = mock(RetrieveKeys::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = KeysController(retrieve, modifyAny)
        doReturn(listOf("Key 4055")).`when`(retrieve).keysList
        doReturn(Stream.of(JSONObject().put("name", "Key 4055"))).`when`(retrieve).keysJSON
        doReturn(Optional.of(JSONObject().put("name", "Key 4055"))).`when`(retrieve).getKeyJSON("Key 4055")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getKeyJSON("Missing")

        assertThat(controller.getKeys(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getKeys(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getKeysByName("Key 4055").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getKeysByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun locationsController_coversListAndGet() {
        val retrieve = mock(RetrieveLocations::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = LocationsController(retrieve, modifyAny)
        doReturn(listOf("Thais")).`when`(retrieve).locationsList
        doReturn(Stream.of(JSONObject().put("name", "Thais"))).`when`(retrieve).locationsJSON
        doReturn(Optional.of(JSONObject().put("name", "Thais"))).`when`(retrieve).getLocationJSON("Thais")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getLocationJSON("Missing")

        assertThat(controller.getLocations(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getLocations(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getLocationsByName("Thais").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getLocationsByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun missilesController_coversListAndGet() {
        val retrieve = mock(RetrieveMissiles::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = MissilesController(retrieve, modifyAny)
        doReturn(listOf("Throwing Cake")).`when`(retrieve).missilesList
        doReturn(Stream.of(JSONObject().put("name", "Throwing Cake"))).`when`(retrieve).missilesJSON
        doReturn(Optional.of(JSONObject().put("name", "Throwing Cake"))).`when`(retrieve).getMissileJSON("Throwing Cake")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getMissileJSON("Missing")

        assertThat(controller.getMissiles(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getMissiles(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getMissilesByName("Throwing Cake").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getMissilesByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun mountsController_coversListAndGet() {
        val retrieve = mock(RetrieveMounts::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = MountsController(retrieve, modifyAny)
        doReturn(listOf("Widow Queen")).`when`(retrieve).mountsList
        doReturn(Stream.of(JSONObject().put("name", "Widow Queen"))).`when`(retrieve).mountsJSON
        doReturn(Optional.of(JSONObject().put("name", "Widow Queen"))).`when`(retrieve).getMountJSON("Widow Queen")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getMountJSON("Missing")

        assertThat(controller.getMounts(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getMounts(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getMountsByName("Widow Queen").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getMountsByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun npcsController_coversListAndGet() {
        val retrieve = mock(RetrieveNPCs::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = NPCsController(retrieve, modifyAny)
        doReturn(listOf("Sam")).`when`(retrieve).getNPCsList()
        doReturn(Stream.of(JSONObject().put("name", "Sam"))).`when`(retrieve).getNPCsJSON()
        doReturn(Optional.of(JSONObject().put("name", "Sam"))).`when`(retrieve).getNPCJSON("Sam")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getNPCJSON("Missing")

        assertThat(controller.getNPCs(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getNPCs(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getNPCsByName("Sam").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getNPCsByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun objectsController_coversListAndGet() {
        val retrieve = mock(RetrieveObjects::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = ObjectsController(retrieve, modifyAny)
        doReturn(listOf("Blueberry Bush")).`when`(retrieve).objectsList
        doReturn(Stream.of(JSONObject().put("name", "Blueberry Bush"))).`when`(retrieve).objectsJSON
        doReturn(Optional.of(JSONObject().put("name", "Blueberry Bush"))).`when`(retrieve).getObjectJSON("Blueberry Bush")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getObjectJSON("Missing")

        assertThat(controller.getObjects(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getObjects(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getObjectsByName("Blueberry Bush").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getObjectsByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun outfitsController_coversListAndGet() {
        val retrieve = mock(RetrieveOutfits::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = OutfitsController(retrieve, modifyAny)
        doReturn(listOf("Pirate Outfits")).`when`(retrieve).outfitsList
        doReturn(Stream.of(JSONObject().put("name", "Pirate Outfits"))).`when`(retrieve).outfitsJSON
        doReturn(Optional.of(JSONObject().put("name", "Pirate Outfits"))).`when`(retrieve).getOutfitJSON("Pirate Outfits")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getOutfitJSON("Missing")

        assertThat(controller.getOutfits(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getOutfits(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getOutfitsByName("Pirate Outfits").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getOutfitsByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun questsController_coversListAndGet() {
        val retrieve = mock(RetrieveQuests::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = QuestsController(retrieve, modifyAny)
        doReturn(listOf("Demon Helmet Quest")).`when`(retrieve).questsList
        doReturn(Stream.of(JSONObject().put("name", "Demon Helmet Quest"))).`when`(retrieve).questsJSON
        doReturn(Optional.of(JSONObject().put("name", "Demon Helmet Quest"))).`when`(retrieve).getQuestJSON("Demon Helmet Quest")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getQuestJSON("Missing")

        assertThat(controller.getQuests(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getQuests(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getQuestsByName("Demon Helmet Quest").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getQuestsByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun spellsController_coversListAndGet() {
        val retrieve = mock(RetrieveSpells::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = SpellsController(retrieve, modifyAny)
        doReturn(listOf("Light Healing")).`when`(retrieve).spellsList
        doReturn(Stream.of(JSONObject().put("name", "Light Healing"))).`when`(retrieve).spellsJSON
        doReturn(Optional.of(JSONObject().put("name", "Light Healing"))).`when`(retrieve).getSpellJSON("Light Healing")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getSpellJSON("Missing")

        assertThat(controller.getSpells(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getSpells(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getSpellsByName("Light Healing").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getSpellsByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun streetsController_coversListAndGet() {
        val retrieve = mock(RetrieveStreets::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val controller = StreetsController(retrieve, modifyAny)
        doReturn(listOf("Main Street")).`when`(retrieve).streetsList
        doReturn(Stream.of(JSONObject().put("name", "Main Street"))).`when`(retrieve).streetsJSON
        doReturn(Optional.of(JSONObject().put("name", "Main Street"))).`when`(retrieve).getStreetJSON("Main Street")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getStreetJSON("Missing")

        assertThat(controller.getStreets(false).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getStreets(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getStreetsByName("Main Street").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getStreetsByName("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun wikiPageController_coversFoundEmptyAndMissing() {
        val retrieve = mock(RetrieveWikiPages::class.java)
        val controller = WikiPageController(retrieve)
        doReturn(JSONObject().put("name", "Dragon")).`when`(retrieve).getWikiPageJSON("Dragon")
        doReturn(JSONObject()).`when`(retrieve).getWikiPageJSON("Empty")
        doReturn(null).`when`(retrieve).getWikiPageJSON("Missing")

        assertThat(controller.getWikiPageByTitle("Dragon").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getWikiPageByTitle("Empty").statusCode, `is`(HttpStatus.NOT_FOUND))
        assertThat(controller.getWikiPageByTitle("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
    }
}
