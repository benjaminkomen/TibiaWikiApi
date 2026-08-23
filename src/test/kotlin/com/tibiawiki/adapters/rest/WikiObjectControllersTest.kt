package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.RetrieveWikiPages
import com.tibiawiki.domain.objects.Charm
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.WikiObjectFixtures
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveBuildings
import com.tibiawiki.process.RetrieveCharms
import com.tibiawiki.process.RetrieveCorpses
import com.tibiawiki.process.RetrieveEffects
import com.tibiawiki.process.RetrieveHuntingPlaces
import com.tibiawiki.process.RetrieveKeys
import com.tibiawiki.process.RetrieveLocations
import com.tibiawiki.process.RetrieveLoot
import com.tibiawiki.process.RetrieveMissiles
import com.tibiawiki.process.RetrieveMounts
import com.tibiawiki.process.RetrieveNPCs
import com.tibiawiki.process.RetrieveObjects
import com.tibiawiki.process.RetrieveOutfits
import com.tibiawiki.process.RetrieveQuests
import com.tibiawiki.process.RetrieveSpells
import com.tibiawiki.process.RetrieveStreets
import io.vavr.control.Try
import jakarta.servlet.http.HttpServletRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.Optional
import java.util.stream.Stream

class WikiObjectControllersTest {

    @Test
    fun buildings() {
        val retrieve = mock(RetrieveBuildings::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.building()
        doReturn(NAMES).`when`(retrieve).buildingsList
        doReturn(Stream.of(JSON)).`when`(retrieve).buildingsJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getBuildingJSON("Theatre Avenue 8b")
        stubModify(modify, wikiObject)
        val controller = BuildingsController(retrieve, modify)
        assertContract(
            { controller.getBuildings(it) },
            { controller.getBuildingsByName("Theatre Avenue 8b") },
            { controller.putBuilding(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun charms() {
        val retrieve = mock(RetrieveCharms::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = Charm(Charm.Type.Offensive, 100, "Dodge attacks")
        doReturn(NAMES).`when`(retrieve).charmsList
        doReturn(Stream.of(JSON)).`when`(retrieve).charmsJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getCharmJSON("Dodge")
        stubModify(modify, wikiObject)
        val controller = CharmsController(retrieve, modify)
        assertContract(
            { controller.getCharms(it) },
            { controller.getCharmsByName("Dodge") },
            { controller.putCharm(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun corpses() {
        val retrieve = mock(RetrieveCorpses::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.corpse()
        doReturn(NAMES).`when`(retrieve).corpsesList
        doReturn(Stream.of(JSON)).`when`(retrieve).corpsesJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getCorpseJSON("Dead Rat")
        stubModify(modify, wikiObject)
        val controller = CorpsesController(retrieve, modify)
        assertContract(
            { controller.getCorpses(it) },
            { controller.getCorpsesByName("Dead Rat") },
            { controller.putCorpse(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun effects() {
        val retrieve = mock(RetrieveEffects::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.effect()
        doReturn(NAMES).`when`(retrieve).effectsList
        doReturn(Stream.of(JSON)).`when`(retrieve).effectsJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getEffectJSON("Fire")
        stubModify(modify, wikiObject)
        val controller = EffectsController(retrieve, modify)
        assertContract(
            { controller.getEffects(it) },
            { controller.getEffectsByName("Fire") },
            { controller.putEffect(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun keys() {
        val retrieve = mock(RetrieveKeys::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.key()
        doReturn(NAMES).`when`(retrieve).keysList
        doReturn(Stream.of(JSON)).`when`(retrieve).keysJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getKeyJSON("Key 3940")
        stubModify(modify, wikiObject)
        val controller = KeysController(retrieve, modify)
        assertContract(
            { controller.getKeys(it) },
            { controller.getKeysByName("Key 3940") },
            { controller.putKey(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun locations() {
        val retrieve = mock(RetrieveLocations::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.location()
        doReturn(NAMES).`when`(retrieve).locationsList
        doReturn(Stream.of(JSON)).`when`(retrieve).locationsJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getLocationJSON("Thais")
        stubModify(modify, wikiObject)
        val controller = LocationsController(retrieve, modify)
        assertContract(
            { controller.getLocations(it) },
            { controller.getLocationsByName("Thais") },
            { controller.putLocation(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun missiles() {
        val retrieve = mock(RetrieveMissiles::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.missile()
        doReturn(NAMES).`when`(retrieve).missilesList
        doReturn(Stream.of(JSON)).`when`(retrieve).missilesJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getMissileJSON("Spear")
        stubModify(modify, wikiObject)
        val controller = MissilesController(retrieve, modify)
        assertContract(
            { controller.getMissiles(it) },
            { controller.getMissilesByName("Spear") },
            { controller.putMissile(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun mounts() {
        val retrieve = mock(RetrieveMounts::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.mount()
        doReturn(NAMES).`when`(retrieve).mountsList
        doReturn(Stream.of(JSON)).`when`(retrieve).mountsJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getMountJSON("Widow Queen")
        stubModify(modify, wikiObject)
        val controller = MountsController(retrieve, modify)
        assertContract(
            { controller.getMounts(it) },
            { controller.getMountsByName("Widow Queen") },
            { controller.putMount(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun npcs() {
        val retrieve = mock(RetrieveNPCs::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.npc()
        doReturn(NAMES).`when`(retrieve).getNPCsList()
        doReturn(Stream.of(JSON)).`when`(retrieve).getNPCsJSON()
        doReturn(Optional.of(JSON)).`when`(retrieve).getNPCJSON("Sam")
        stubModify(modify, wikiObject)
        val controller = NPCsController(retrieve, modify)
        assertContract(
            { controller.getNPCs(it) },
            { controller.getNPCsByName("Sam") },
            { controller.putNPC(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun objects() {
        val retrieve = mock(RetrieveObjects::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.tibiaObject()
        doReturn(NAMES).`when`(retrieve).objectsList
        doReturn(Stream.of(JSON)).`when`(retrieve).objectsJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getObjectJSON("Blueberry Bush")
        stubModify(modify, wikiObject)
        val controller = ObjectsController(retrieve, modify)
        assertContract(
            { controller.getObjects(it) },
            { controller.getObjectsByName("Blueberry Bush") },
            { controller.putObject(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun outfits() {
        val retrieve = mock(RetrieveOutfits::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.outfit()
        doReturn(NAMES).`when`(retrieve).outfitsList
        doReturn(Stream.of(JSON)).`when`(retrieve).outfitsJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getOutfitJSON("Citizen")
        stubModify(modify, wikiObject)
        val controller = OutfitsController(retrieve, modify)
        assertContract(
            { controller.getOutfits(it) },
            { controller.getOutfitsByName("Citizen") },
            { controller.putOutfit(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun quests() {
        val retrieve = mock(RetrieveQuests::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.quest()
        doReturn(NAMES).`when`(retrieve).questsList
        doReturn(Stream.of(JSON)).`when`(retrieve).questsJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getQuestJSON("The Annihilator Quest")
        stubModify(modify, wikiObject)
        val controller = QuestsController(retrieve, modify)
        assertContract(
            { controller.getQuests(it) },
            { controller.getQuestsByName("The Annihilator Quest") },
            { controller.putQuest(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun spells() {
        val retrieve = mock(RetrieveSpells::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.spell()
        doReturn(NAMES).`when`(retrieve).spellsList
        doReturn(Stream.of(JSON)).`when`(retrieve).spellsJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getSpellJSON("Light Healing")
        stubModify(modify, wikiObject)
        val controller = SpellsController(retrieve, modify)
        assertContract(
            { controller.getSpells(it) },
            { controller.getSpellsByName("Light Healing") },
            { controller.putSpell(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun streets() {
        val retrieve = mock(RetrieveStreets::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.street()
        doReturn(NAMES).`when`(retrieve).streetsList
        doReturn(Stream.of(JSON)).`when`(retrieve).streetsJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getStreetJSON("Theatre Avenue")
        stubModify(modify, wikiObject)
        val controller = StreetsController(retrieve, modify)
        assertContract(
            { controller.getStreets(it) },
            { controller.getStreetsByName("Theatre Avenue") },
            { controller.putStreet(wikiObject, SUMMARY) }
        )
    }

    @Test
    fun lootV1() {
        val retrieve = mock(RetrieveLoot::class.java)
        doReturn(NAMES).`when`(retrieve).getLootList()
        doReturn(Stream.of(JSON)).`when`(retrieve).getLootJSONObject()
        doReturn(Optional.of(JSON)).`when`(retrieve).getLootJSONObject("Loot_Statistics:Amazon")
        val controller = LootStatisticsController(retrieve)
        assertThat(controller.getLoot(false).body, `is`(NAMES))
        assertThat(controller.getLootByName("Amazon").statusCode, `is`(HttpStatus.OK))
    }

    @Test
    fun lootV2() {
        val retrieve = mock(RetrieveLoot::class.java)
        doReturn(NAMES).`when`(retrieve).getLootList()
        doReturn(Stream.of(JSON)).`when`(retrieve).getAllLootPartsJSON()
        doReturn(Optional.of(JSON)).`when`(retrieve).getAllLootPartsJSON("Loot_Statistics:Amazon")
        val controller = LootStatisticsV2Controller(retrieve)
        assertThat(controller.getLoot(true).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getLootByName("Amazon").statusCode, `is`(HttpStatus.OK))
    }

    @Test
    fun huntingPlaces() {
        val retrieve = mock(RetrieveHuntingPlaces::class.java)
        val modify = mock(ModifyAny::class.java)
        val wikiObject = WikiObjectFixtures.huntingPlace()
        doReturn(NAMES).`when`(retrieve).huntingPlacesList
        doReturn(Stream.of(JSON)).`when`(retrieve).huntingPlacesJSON
        doReturn(Optional.of(JSON)).`when`(retrieve).getHuntingPlaceJSON("Tiquanda/Bandit Caves")
        stubModify(modify, wikiObject)
        val request = mock(HttpServletRequest::class.java)
        doReturn("/api/huntingplaces/Tiquanda/Bandit%20Caves").`when`(request).requestURI
        val controller = HuntingPlacesController(retrieve, modify)
        assertThat(controller.getHuntingPlaces(false).body, `is`(NAMES))
        assertThat(controller.getHuntingPlacesByName(request).statusCode, `is`(HttpStatus.OK))
        assertThat(controller.putHuntingPlace(wikiObject, SUMMARY).statusCode, `is`(HttpStatus.OK))
    }

    @Test
    fun wikiPage() {
        val retrieve = mock(RetrieveWikiPages::class.java)
        doReturn(JSON).`when`(retrieve).getWikiPageJSON("Dragon")
        doReturn(null).`when`(retrieve).getWikiPageJSON("Missing")
        doReturn(JSONObject()).`when`(retrieve).getWikiPageJSON("Empty")
        val controller = WikiPageController(retrieve)
        assertThat(controller.getWikiPageByTitle("Dragon").statusCode, `is`(HttpStatus.OK))
        assertThat(controller.getWikiPageByTitle("Missing").statusCode, `is`(HttpStatus.NOT_FOUND))
        assertThat(controller.getWikiPageByTitle("Empty").statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    private fun stubModify(modify: ModifyAny, wikiObject: WikiObject) {
        doReturn(Try.success(wikiObject)).`when`(modify).modify(wikiObject, SUMMARY)
    }

    private fun assertContract(
        getList: (Boolean?) -> ResponseEntity<Any>,
        getByName: () -> ResponseEntity<String>,
        put: () -> ResponseEntity<WikiObject>
    ) {
        assertThat(getList(false).statusCode, `is`(HttpStatus.OK))
        assertThat(getList(false).body, `is`(NAMES))
        assertThat(getList(true).statusCode, `is`(HttpStatus.OK))
        assertThat(getByName().statusCode, `is`(HttpStatus.OK))
        assertThat(put().statusCode, `is`(HttpStatus.OK))
    }

    companion object {
        private val NAMES = listOf("foo", "bar")
        private val JSON = JSONObject(mapOf("name" to "foo"))
        private const val SUMMARY = "[bot] test"
    }
}
