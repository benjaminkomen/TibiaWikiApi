package com.tibiawiki.domain.factories

import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.ObjectMapper
import com.tibiawiki.domain.objects.Achievement
import com.tibiawiki.domain.objects.Book
import com.tibiawiki.domain.objects.Building
import com.tibiawiki.domain.objects.Corpse
import com.tibiawiki.domain.objects.Creature
import com.tibiawiki.domain.objects.Effect
import com.tibiawiki.domain.objects.HuntingPlace
import com.tibiawiki.domain.objects.Key
import com.tibiawiki.domain.objects.Location
import com.tibiawiki.domain.objects.Missile
import com.tibiawiki.domain.objects.Mount
import com.tibiawiki.domain.objects.NPC
import com.tibiawiki.domain.objects.Outfit
import com.tibiawiki.domain.objects.Quest
import com.tibiawiki.domain.objects.Spell
import com.tibiawiki.domain.objects.Street
import com.tibiawiki.domain.objects.TibiaObject
import com.tibiawiki.domain.objects.WikiObjectFixtures
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class JsonFactoryTest {

    private lateinit var target: JsonFactory
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        target = JsonFactory()
        objectMapper = JsonMapper.builder().build()
    }

    @Test
    fun testConvertInfoboxPartOfArticleToJson_NullOrEmpty() {
        assertThat(target.convertInfoboxPartOfArticleToJson(null), instanceOf(JSONObject::class.java))
        assertThat(target.convertInfoboxPartOfArticleToJson(""), instanceOf(JSONObject::class.java))
    }

    @Test
    fun testConvertInfoboxPartOfArticleToJson_InfoboxAchievement() {
        val result = target.convertInfoboxPartOfArticleToJson(INFOBOX_ACHIEVEMENT_TEXT)

        assertThat(result.get("templateType"), `is`("Achievement"))
        assertThat(result.get("grade"), `is`("1"))
        assertThat(result.get("name"), `is`("Goo Goo Dancer"))
        assertThat(result.get("description"), `is`("Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!"))
        assertThat(result.get("spoiler"), `is`("Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s."))
        assertThat(result.get("premium"), `is`("yes"))
        assertThat(result.get("points"), `is`("1"))
        assertThat(result.get("secret"), `is`("yes"))
        assertThat(result.get("implemented"), `is`("9.6"))
        assertThat(result.get("achievementid"), `is`("319"))
        assertThat(result.get("relatedpages"), `is`("[[Muck Remover]], [[Mucus Plug]]"))
    }

    @Test
    fun testConvertInfoboxPartOfArticleToJson_InfoboxHunt() {
        val result = target.convertInfoboxPartOfArticleToJson(INFOBOX_HUNT_TEXT)

        assertThat(result.get("templateType"), `is`("Hunt"))
        assertThat(result.get("name"), `is`("Hero Cave"))
        assertThat(result.get("image"), `is`("Hero"))
        assertThat(result.get("implemented"), `is`("6.4"))
        assertThat(result.get("city"), `is`("Edron"))
        assertThat(result.get("location"), `is`("North of [[Edron]], [https://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here]."))
        assertThat(result.get("vocation"), `is`("All vocations."))
        assertThat(result.get("lvlknights"), `is`("70"))
        assertThat(result.get("lvlpaladins"), `is`("60"))
        assertThat(result.get("lvlmages"), `is`("50"))
        assertThat(result.get("skknights"), `is`("75"))
        assertThat(result.get("skpaladins"), `is`("80"))
        assertThat(result.get("defknights"), `is`("75"))
        assertThat(result.get("lowerlevels"), instanceOf(JSONArray::class.java))
        assertThat(((result.get("lowerlevels") as JSONArray).get(0) as JSONObject).get("areaname"), `is`("Demons"))
        assertThat(((result.get("lowerlevels") as JSONArray).get(0) as JSONObject).get("lvlknights"), `is`("130"))
        assertThat(((result.get("lowerlevels") as JSONArray).get(0) as JSONObject).get("lvlpaladins"), `is`("130"))
        assertThat(((result.get("lowerlevels") as JSONArray).get(0) as JSONObject).get("lvlmages"), `is`("130"))
        assertThat(result.get("exp"), `is`("Good"))
        assertThat(result.get("loot"), `is`("Good"))
        assertThat(result.get("bestloot"), `is`("Reins"))
        assertThat(result.get("map"), `is`("Hero Cave 3.png"))
        assertThat(result.get("map2"), `is`("Hero Cave 6.png"))
    }

    @Test
    fun testConvertLootPartOfArticleToJson_NullOrEmpty() {
        assertThat(target.convertLootPartOfArticleToJson("", null), instanceOf(JSONObject::class.java))
        assertThat(target.convertLootPartOfArticleToJson("", ""), instanceOf(JSONObject::class.java))
    }

    @Test
    fun testGetTemplateType_NullOrEmpty() {
        assertThat(target.getTemplateType(null), `is`("Unknown"))
        assertThat(target.getTemplateType(""), `is`("Unknown"))
    }

    @Test
    fun testGetTemplateType_Succes() {
        assertThat(target.getTemplateType(INFOBOX_TEXT_SPACE), `is`("Achievement"))
        assertThat(target.getTemplateType(INFOBOX_TEXT_UNDERSCORE), `is`("Hunt"))
    }

    @Test
    fun testGetTemplateType_Failure() {
        assertThat(target.getTemplateType(INFOBOX_TEXT_WRONG), `is`("Unknown"))
    }

    @Test
    fun testEnhanceJsonObject_FailureNoName() {
        val someJsonObject = JSONObject(emptyMap<String, Any>())
        assertThat(target.enhanceJsonObject(someJsonObject), `is`(someJsonObject))
    }

    @Test
    fun testEnhanceJsonObject_Failure_Sounds() {
        val inputJsonObject = JSONObject(mapOf("name" to "Dragon", "templateType" to "Creature", "sounds" to "FCHHHHH, GROOAAARRR"))
        val result = target.enhanceJsonObject(inputJsonObject)
        assertThat((result.get("sounds") as JSONArray).length(), `is`(0))
    }

    @Test
    fun testEnhanceJsonObject_Failure_Empty_SoundsList() {
        val inputJsonObject = JSONObject(mapOf("name" to "Dragon", "templateType" to "Creature", "sounds" to "{{Sound List}}"))
        val result = target.enhanceJsonObject(inputJsonObject)
        assertThat((result.get("sounds") as JSONArray).length(), `is`(0))
    }

    @Test
    fun testEnhanceJsonObject_Succes_Sounds() {
        val inputJsonObject = JSONObject(mapOf("name" to "Dragon", "templateType" to "Creature", "sounds" to "{{Sound List|FCHHHHH|GROOAAARRR}}"))
        val result = target.enhanceJsonObject(inputJsonObject)
        assertThat((result.get("sounds") as JSONArray).get(0), `is`("FCHHHHH"))
        assertThat((result.get("sounds") as JSONArray).get(1), `is`("GROOAAARRR"))
    }

    @Test
    fun testEnhanceJsonObject_Spawntype_Empty() {
        val inputJsonObject = JSONObject(mapOf("name" to "Demon", "templateType" to "Creature", "spawntype" to " "))
        val result = target.enhanceJsonObject(inputJsonObject)
        assertThat((result.get("spawntype") as JSONArray).length(), `is`(0))
    }

    @Test
    fun testEnhanceJsonObject_Spawntype_Succes() {
        val inputJsonObject = JSONObject(mapOf("name" to "Demon", "templateType" to "Creature", "spawntype" to "Regular, Raid"))
        val result = target.enhanceJsonObject(inputJsonObject)
        assertThat((result.get("spawntype") as JSONArray).get(0), `is`("Regular"))
        assertThat((result.get("spawntype") as JSONArray).get(1), `is`("Raid"))
    }

    @Test
    fun testEnhanceJsonObject_Loot_ItemNameWithNumbers() {
        val inputJsonObject = JSONObject(
            mapOf(
                "loot" to """
                    {{Loot Table
                    |{{Loot Item|7197 Theons|common}}
                    |{{Loot Item|60|Platinum Coin|common}}
                    |{{Loot Item|Amber with a Bug|common}}
                    |{{Loot Item|Brass Button}}
                    }}
                """.trimIndent().trimStart()
            )
        )
        val result = target.enhanceJsonObject(inputJsonObject)
        assertEquals("7197 Theons", (result.get("loot") as JSONArray).getJSONObject(0).get("itemName"))
        assertEquals("common", (result.get("loot") as JSONArray).getJSONObject(0).get("rarity"))

        assertEquals("Platinum Coin", (result.get("loot") as JSONArray).getJSONObject(1).get("itemName"))
        assertEquals("60", (result.get("loot") as JSONArray).getJSONObject(1).get("amount"))
        assertEquals("common", (result.get("loot") as JSONArray).getJSONObject(1).get("rarity"))

        assertEquals("Amber with a Bug", (result.get("loot") as JSONArray).getJSONObject(2).get("itemName"))
        assertEquals("common", (result.get("loot") as JSONArray).getJSONObject(2).get("rarity"))

        assertEquals("Brass Button", (result.get("loot") as JSONArray).getJSONObject(3).get("itemName"))
    }

    @Test
    fun testDetermineArticleName_EmptyOrNull() {
        assertThat(target.determineArticleName(null, null), `is`("Unknown"))
        assertThat(target.determineArticleName(JSONObject(), ""), `is`("Unknown"))
    }

    @Test
    fun testDetermineArticleName_Book() {
        val input = JSONObject(mapOf("pagename" to "Foobar"))

        assertThat(target.determineArticleName(input, JsonFactory.TEMPLATE_TYPE_BOOK), `is`("Foobar"))
    }

    @Test
    fun testDetermineArticleName_Location() {
        val input = JSONObject()

        assertThat(target.determineArticleName(input, JsonFactory.TEMPLATE_TYPE_LOCATION), `is`("Unknown"))
    }

    @Test
    fun testDetermineArticleName_Key() {
        val input = JSONObject(mapOf("number" to "1234"))

        assertThat(target.determineArticleName(input, JsonFactory.TEMPLATE_TYPE_KEY), `is`("Key 1234"))
    }

    @Test
    fun testDetermineArticleName_Achievement() {
        val input = JSONObject(mapOf("name" to "Foobar"))

        assertThat(target.determineArticleName(input, JsonFactory.TEMPLATE_TYPE_ACHIEVEMENT), `is`("Foobar"))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Empty() {
        assertThat(target.convertJsonToInfoboxPartOfArticle(null, emptyList()), `is`(""))
        assertThat(target.convertJsonToInfoboxPartOfArticle(JSONObject(), emptyList()), `is`(""))

        val jsonWithNoTemplateType = makeAchievementJson(makeAchievement())
        jsonWithNoTemplateType.remove("templateType")
        assertThat(target.convertJsonToInfoboxPartOfArticle(jsonWithNoTemplateType, emptyList()), `is`(""))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Achievement() {
        val achievement = makeAchievement()
        val result = target.convertJsonToInfoboxPartOfArticle(makeAchievementJson(achievement), achievement.fieldOrder())
        assertThat(result, `is`(INFOBOX_ACHIEVEMENT_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Book() {
        val book = makeBook()
        val result = target.convertJsonToInfoboxPartOfArticle(makeBookJson(book), book.fieldOrder())
        assertThat(result, `is`(INFOBOX_BOOK_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Building() {
        val building = makeBuilding()
        val result = target.convertJsonToInfoboxPartOfArticle(makeBuildingJson(building), building.fieldOrder())
        assertThat(result, `is`(INFOBOX_BUILDING_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Corpse() {
        val corpse = makeCorpse()
        val result = target.convertJsonToInfoboxPartOfArticle(makeCorpseJson(corpse), corpse.fieldOrder())
        assertThat(result, `is`(INFOBOX_CORPSE_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Creature() {
        val creature = makeCreature()
        val result = target.convertJsonToInfoboxPartOfArticle(makeCreatureJson(creature), creature.fieldOrder())
        assertThat(result, `is`(INFOBOX_CREATURE_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Effect() {
        val effect = makeEffect()
        val result = target.convertJsonToInfoboxPartOfArticle(makeEffectJson(effect), effect.fieldOrder())
        assertThat(result, `is`(INFOBOX_EFFECT_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_CreatureWithEmptyLootTable() {
        val creature = makeCreatureWithEmptyLootTable()
        val result = target.convertJsonToInfoboxPartOfArticle(makeCreatureJson(creature), creature.fieldOrder())
        assertThat(result, `is`(INFOBOX_CREATURE_EMPTY_LOOT_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Item() {
        val item = makeItem()
        val result = target.convertJsonToInfoboxPartOfArticle(makeItemJson(item), item.fieldOrder())
        assertThat(result, `is`(INFOBOX_ITEM_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Key() {
        val key = makeKey()
        val result = target.convertJsonToInfoboxPartOfArticle(makeKeyJson(key), key.fieldOrder())
        assertThat(result, `is`(INFOBOX_KEY_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Missile() {
        val missile = makeMissile()
        val result = target.convertJsonToInfoboxPartOfArticle(makeMissileJson(missile), missile.fieldOrder())
        assertThat(result, `is`(INFOBOX_MISSILE_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Mount() {
        val mount = makeMount()
        val result = target.convertJsonToInfoboxPartOfArticle(makeMountJson(mount), mount.fieldOrder())
        assertThat(result, `is`(INFOBOX_MOUNT_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Object() {
        val tibiaObject = makeTibiaObject()
        val result = target.convertJsonToInfoboxPartOfArticle(makeTibiaObjectJson(tibiaObject), tibiaObject.fieldOrder())
        assertThat(result, `is`(INFOBOX_OBJECT_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Outfit() {
        val outfit = makeOutfit()
        val result = target.convertJsonToInfoboxPartOfArticle(makeOutfitJson(outfit), outfit.fieldOrder())
        assertThat(result, `is`(INFOBOX_OUTFIT_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Quest() {
        val quest = makeQuest()
        val result = target.convertJsonToInfoboxPartOfArticle(makeQuestJson(quest), quest.fieldOrder())
        assertThat(result, `is`(INFOBOX_QUEST_TEXT))
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Spell() {
        val spell = makeSpell()
        val result = target.convertJsonToInfoboxPartOfArticle(makeSpellJson(spell), spell.fieldOrder())
        assertThat(result, `is`(INFOBOX_SPELL_TEXT))
    }

    @Test
    fun testConvertLootPartOfArticleToJson_Loot2Bear() {
        val result = target.convertLootPartOfArticleToJson("Loot Statistics:Bear", LOOT_BEAR_TEXT)

        assertThat(result.get("version"), `is`("8.6"))
        assertThat(result.get("kills"), `is`("52807"))
        assertThat(result.get("name"), `is`("Bear"))

        val loot = result.get("loot")
        assertThat(loot, instanceOf(JSONArray::class.java))

        val lootArray = loot as JSONArray
        assertThat((lootArray.get(0) as JSONObject).get("itemName"), `is`("Empty"))
        assertThat((lootArray.get(0) as JSONObject).get("times"), `is`("24777"))

        assertThat((lootArray.get(1) as JSONObject).get("itemName"), `is`("Ham"))
        assertThat((lootArray.get(1) as JSONObject).get("times"), `is`("10581"))

        assertThat((lootArray.get(2) as JSONObject).get("itemName"), `is`("Bear Paw"))
        assertThat((lootArray.get(2) as JSONObject).get("times"), `is`("1043"))
        assertThat((lootArray.get(2) as JSONObject).get("amount"), `is`("1"))
        assertThat((lootArray.get(2) as JSONObject).get("total"), `is`("1043"))

        assertThat((lootArray.get(3) as JSONObject).get("itemName"), `is`("Honeycomb"))
        assertThat((lootArray.get(3) as JSONObject).get("times"), `is`("250"))
        assertThat((lootArray.get(3) as JSONObject).get("amount"), `is`("1"))
        assertThat((lootArray.get(3) as JSONObject).get("total"), `is`("249"))

        assertThat((lootArray.get(4) as JSONObject).get("itemName"), `is`("Meat"))
        assertThat((lootArray.get(4) as JSONObject).get("times"), `is`("21065"))
    }

    private fun makeAchievement(): Achievement {
        return WikiObjectFixtures.achievement()
    }

    private fun makeAchievementJson(achievement: Achievement): JSONObject {
        return JSONObject(objectMapper.convertValue(achievement, Map::class.java)).put("templateType", "Achievement")
    }

    private fun makeBook(): Book {
        return WikiObjectFixtures.book()
    }

    private fun makeBookJson(book: Book): JSONObject {
        return JSONObject(objectMapper.convertValue(book, Map::class.java)).put("templateType", "Book")
    }

    private fun makeBuilding(): Building {
        return WikiObjectFixtures.building()
    }

    private fun makeBuildingJson(building: Building): JSONObject {
        return JSONObject(objectMapper.convertValue(building, Map::class.java)).put("templateType", "Building")
    }

    private fun makeCorpse(): Corpse {
        return WikiObjectFixtures.corpse()
    }

    private fun makeCorpseJson(corpse: Corpse): JSONObject {
        return JSONObject(objectMapper.convertValue(corpse, Map::class.java)).put("templateType", "Corpse")
    }

    private fun makeCreatureJson(creature: Creature): JSONObject {
        return JSONObject(objectMapper.convertValue(creature, Map::class.java)).put("templateType", "Creature")
    }

    private fun makeEffect(): Effect {
        return WikiObjectFixtures.effect()
    }

    private fun makeEffectJson(effect: Effect): JSONObject {
        return JSONObject(objectMapper.convertValue(effect, Map::class.java)).put("templateType", "Effect")
    }

    private fun makeHuntingPlaceJson(huntingPlace: HuntingPlace): JSONObject {
        return JSONObject(objectMapper.convertValue(huntingPlace, Map::class.java)).put("templateType", "Hunt")
    }

    private fun makeItemJson(item: TibiaObject): JSONObject {
        return JSONObject(objectMapper.convertValue(item, Map::class.java)).put("templateType", "Object")
    }

    private fun makeKey(): Key {
        return WikiObjectFixtures.key()
    }

    private fun makeKeyJson(key: Key): JSONObject {
        return JSONObject(objectMapper.convertValue(key, Map::class.java)).put("templateType", "Key")
    }

    private fun makeLocation(): Location {
        return WikiObjectFixtures.location()
    }

    private fun makeLocationJson(location: Location): JSONObject {
        return JSONObject(objectMapper.convertValue(location, Map::class.java)).put("templateType", "Geography")
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_HuntingPlace() {
        val huntingPlace = makeHuntingPlace()
        val result = target.convertJsonToInfoboxPartOfArticle(makeHuntingPlaceJson(huntingPlace), huntingPlace.fieldOrder())
        assertThat(result, `is`(INFOBOX_HUNT_TEXT))
    }

    private fun makeMissile(): Missile {
        return WikiObjectFixtures.missile()
    }

    private fun makeMissileJson(missile: Missile): JSONObject {
        return JSONObject(objectMapper.convertValue(missile, Map::class.java)).put("templateType", "Missile")
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Location() {
        val location = makeLocation()
        val result = target.convertJsonToInfoboxPartOfArticle(makeLocationJson(location), location.fieldOrder())
        assertThat(result, `is`(INFOBOX_LOCATION_TEXT))
    }

    private fun makeMount(): Mount {
        return WikiObjectFixtures.mount()
    }

    private fun makeMountJson(mount: Mount): JSONObject {
        return JSONObject(objectMapper.convertValue(mount, Map::class.java)).put("templateType", "Mount")
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_NPC() {
        val npc = makeNPC()
        val result = target.convertJsonToInfoboxPartOfArticle(makeNPCJson(npc), npc.fieldOrder())
        assertThat(result, `is`(INFOBOX_NPC_TEXT))
    }

    private fun makeNPC(): NPC {
        return WikiObjectFixtures.npc()
    }

    private fun makeNPCJson(npc: NPC): JSONObject {
        return JSONObject(objectMapper.convertValue(npc, Map::class.java)).put("templateType", "NPC")
    }

    @Test
    fun testConvertJsonToInfoboxPartOfArticle_Street() {
        val street = makeStreet()
        val result = target.convertJsonToInfoboxPartOfArticle(makeStreetJson(street), street.fieldOrder())
        assertThat(result, `is`(INFOBOX_STREET_TEXT))
    }

    private fun makeTibiaObject(): TibiaObject {
        return WikiObjectFixtures.tibiaObject()
    }

    @Suppress("UNCHECKED_CAST")
    private fun makeTibiaObjectJson(tibiaObject: TibiaObject): JSONObject {
        val properties = (objectMapper.convertValue(tibiaObject, Map::class.java) as Map<String, Any?>)
            .filterValues { it != null }
        return JSONObject(properties).put("templateType", "Object")
    }

    private fun makeCreature(): Creature {
        return WikiObjectFixtures.creature()
    }

    private fun makeCreatureWithEmptyLootTable(): Creature {
        return WikiObjectFixtures.creatureWithEmptyLoot()
    }

    private fun makeOutfit(): Outfit {
        return WikiObjectFixtures.outfit()
    }

    private fun makeOutfitJson(outfit: Outfit): JSONObject {
        return JSONObject(objectMapper.convertValue(outfit, Map::class.java)).put("templateType", "Outfit")
    }

    private fun makeHuntingPlace(): HuntingPlace {
        return WikiObjectFixtures.huntingPlace()
    }

    private fun makeItem(): TibiaObject {
        return WikiObjectFixtures.item()
    }

    private fun makeQuestJson(quest: Quest): JSONObject {
        return JSONObject(objectMapper.convertValue(quest, Map::class.java)).put("templateType", "Quest")
    }

    private fun makeSpell(): Spell {
        return WikiObjectFixtures.spell()
    }

    private fun makeSpellJson(spell: Spell): JSONObject {
        return JSONObject(objectMapper.convertValue(spell, Map::class.java)).put("templateType", "Spell")
    }

    private fun makeQuest(): Quest {
        return WikiObjectFixtures.quest()
    }

    private fun makeStreet(): Street {
        return WikiObjectFixtures.street()
    }

    private fun makeStreetJson(street: Street): JSONObject {
        return JSONObject(objectMapper.convertValue(street, Map::class.java)).put("templateType", "Street")
    }

    companion object {
        private val LOOT_BEAR_TEXT = """
            {{Loot2
            |version=8.6
            |kills=52807
            |name=Bear
            |Empty, times:24777
            |Meat, times:21065
            |Ham, times:10581
            |Bear Paw, times:1043, amount:1, total:1043
            |Honeycomb, times:250, amount:1, total:249
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_CREATURE_TEXT = """
            {{Infobox Creature|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name           = Dragon
            | article        = a
            | actualname     = dragon
            | plural         = dragons
            | hp             = 1000
            | exp            = 700
            | armor          = 25
            | summon         = --
            | convince       = --
            | illusionable   = yes
            | creatureclass  = Reptiles
            | primarytype    = Dragons
            | bestiaryclass  = Dragon
            | bestiarylevel  = Medium
            | occurrence     = Common
            | spawntype      = Regular, Raid
            | isboss         = no
            | isarenaboss    = no
            | abilities      = [[Melee]] (0-120), [[Fire Wave]] (100-170), [[Great Fireball]] (60-140), [[Self-Healing]] (40-70)
            | maxdmg         = 430
            | pushable       = no
            | pushobjects    = yes
            | walksaround    = None
            | walksthrough   = Fire, Energy, Poison
            | paraimmune     = yes
            | senseinvis     = yes
            | physicalDmgMod = 100%
            | earthDmgMod    = 20%
            | fireDmgMod     = 0%
            | deathDmgMod    = 100%
            | energyDmgMod   = 80%
            | holyDmgMod     = 100%
            | iceDmgMod      = 110%
            | hpDrainDmgMod  = 100%?
            | drownDmgMod    = 100%?
            | bestiaryname   = dragon
            | bestiarytext   = Dragons were
            | sounds         = {{Sound List|FCHHHHH|GROOAAARRR}}
            | implemented    = Pre-6.0
            | notes          = Dragons are
            | behaviour      = Dragons are
            | runsat         = 300
            | speed          = 86
            | strategy       = '''All''' [[player]]s
            | location       = [[Thais]] [[Ancient Temple]], [[Darashia Dragon Lair]], [[Mount Sternum Dragon Cave]], [[Mintwallin]], deep in [[Fibula Dungeon]], [[Kazordoon Dragon Lair]] (near [[Dwarf Bridge]]), [[Plains of Havoc]], [[Elven Bane]] castle, [[Maze of Lost Souls]], southern cave and dragon tower in [[Shadowthorn]], [[Orc Fortress]], [[Venore]] [[Dragon Lair]], [[Pits of Inferno]], [[Behemoth Quest]] room in [[Edron]], [[Hero Cave]], deep [[Cyclopolis]], [[Edron Dragon Lair]], [[Goroma]], [[Ankrahmun Dragon Lair]]s, [[Draconia]], [[Dragonblaze Peaks]], some [[Ankrahmun Tombs]], underground of [[Fenrock]] (on the way to [[Beregar]]), [[Krailos Steppe]] and [[Crystal Lakes]].
            | loot           = {{Loot Table
             |{{Loot Item|0-105|Gold Coin}}
             |{{Loot Item|0-3|Dragon Ham}}
             |{{Loot Item|Steel Shield}}
             |{{Loot Item|Crossbow}}
             |{{Loot Item|Dragon's Tail}}
             |{{Loot Item|0-10|Burst Arrow}}
             |{{Loot Item|Longsword|semi-rare}}
             |{{Loot Item|Steel Helmet|semi-rare}}
             |{{Loot Item|Broadsword|semi-rare}}
             |{{Loot Item|Plate Legs|semi-rare}}
             |{{Loot Item|Green Dragon Leather|rare}}
             |{{Loot Item|Wand of Inferno|rare}}
             |{{Loot Item|Strong Health Potion|rare}}
             |{{Loot Item|Green Dragon Scale|rare}}
             |{{Loot Item|Double Axe|rare}}
             |{{Loot Item|Dragon Hammer|rare}}
             |{{Loot Item|Serpent Sword|rare}}
             |{{Loot Item|Small Diamond|very rare}}
             |{{Loot Item|Dragon Shield|very rare}}
             |{{Loot Item|Life Crystal|very rare}}
             |{{Loot Item|Dragonbone Staff|very rare}}
            }}
            | history        = Dragons are
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_CREATURE_EMPTY_LOOT_TEXT = """
            {{Infobox Creature|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name          = Freed Soul
            | article       = a
            | actualname    = Freed Soul
            | plural        = Freed Soul
            | hp            = ?
            | exp           = ?
            | summon        = --
            | convince      = --
            | illusionable  = no
            | creatureclass =${" "}
            | primarytype   =${" "}
            | isboss        = no
            | abilities     = [[Melee]] (0-?), [[Drown Damage|Drown Bomb]] on self (4000-8000) (damages boss only)
            | implemented   = 11.40
            | behaviour     = They fight in close combat.
            | strategy      = Do not kill them since you need their help in order to kill the boss.
            | location      = [[The Souldespoiler]]'s room.
            | loot          = {{Loot Table}}
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_HUNT_TEXT = """
            {{Infobox Hunt|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Hero Cave
            | image        = Hero
            | implemented  = 6.4
            | city         = Edron
            | location     = North of [[Edron]], [https://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].
            | vocation     = All vocations.
            | lvlknights   = 70
            | lvlpaladins  = 60
            | lvlmages     = 50
            | skknights    = 75
            | skpaladins   = 80
            | skmages      = 1
            | defknights   = 75
            | defpaladins  = 1
            | defmages     = 1
            | lowerlevels  =${" "}
                {{Infobox Hunt Skills
                | areaname    = Demons
                | lvlknights  = 130
                | lvlpaladins = 130
                | lvlmages    = 130
                | skknights   = 1
                | skpaladins  = 1
                | skmages     = 1
                | defknights  = 1
                | defpaladins = 1
                | defmages    = 1
                }}
                {{Infobox Hunt Skills
                | areaname    = Another Area (Past Teleporter)
                | lvlknights  = 230
                | lvlpaladins = 230
                | lvlmages    = 230
                | skknights   = 2
                | skpaladins  = 2
                | skmages     = 2
                | defknights  = 2
                | defpaladins = 2
                | defmages    = 2
                }}
            | loot         = Good
            | exp          = Good
            | bestloot     = Reins
            | bestloot2    = Foobar
            | bestloot3    = Foobar
            | bestloot4    = Foobar
            | bestloot5    = Foobar
            | map          = Hero Cave 3.png
            | map2         = Hero Cave 6.png
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_ITEM_TEXT = """
            {{Infobox Object|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name          = Carlin Sword
            | article       = a
            | actualname    = carlin sword
            | plural        = ?
            | itemid        = 3283
            | objectclass   = Weapons
            | flavortext    = Foobar
            | sounds        = {{Sound List|}}
            | pickupable    = yes
            | usable        = yes
            | levelrequired = 0
            | hands         = One
            | weapontype    = Sword
            | attack        = 15
            | defense       = 13
            | defensemod    = +1
            | enchantable   = no
            | weight        = 40.00
            | marketable    = yes
            | droppedby     = {{Dropped By|Grorlam|Stone Golem}}
            | value         = 118
            | npcvalue      = 118
            | npcprice      = 473
            | npcvaluerook  = 0
            | npcpricerook  = 0
            | buyfrom       = Baltim, Brengus, Cedrik,
            | sellto        = Baltim, Brengus, Cedrik, Esrik,
            | notes         = If you have one of these${" "}
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_KEY_TEXT = """
            {{Infobox Key|List={{{1|}}}|GetValue={{{GetValue|}}}
            | number       = 4055
            | aka          = Panpipe Quest Key
            | primarytype  = Silver
            | location     = [[Jakundaf Desert]]
            | value        = Negotiable
            | npcvalue     = 0
            | npcprice     = 0
            | buyfrom      = --
            | sellto       = --
            | origin       = Hidden in a rock south of the Desert Dungeon entrance.
            | shortnotes   = Access to the [[Panpipe Quest]].
            | longnotes    = Allows you to open the door ([https://tibia.wikia.com/wiki/Mapper?coords=127.131,125.129,8,3,1,1 here]) to the [[Panpipe Quest]].
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_TEXT_SPACE = """
            {{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Goo Goo Dancer
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_TEXT_UNDERSCORE = """
            {{Infobox_Hunt|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Goo Goo Dancer
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_TEXT_WRONG = "{{Infobax_Hunt  \n|List={{{1|}}}|GetValue={{{GetValue|}}}"
        private val INFOBOX_ACHIEVEMENT_TEXT = """
            {{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | grade         = 1
            | name          = Goo Goo Dancer
            | description   = Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!
            | spoiler       = Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.
            | premium       = yes
            | points        = 1
            | secret        = yes
            | implemented   = 9.6
            | achievementid = 319
            | relatedpages  = [[Muck Remover]], [[Mucus Plug]]
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_BOOK_TEXT = """
            {{Infobox Book|List={{{1|}}}|GetValue={{{GetValue|}}}
            | booktype     = Book (Brown)
            | title        = Dungeon Survival Guide
            | pagename     = Dungeon Survival Guide (Book)
            | location     = [[Rookgaard Academy]]
            | blurb        = Tips for exploring dungeons, and warning against being reckless.
            | returnpage   = Rookgaard Libraries
            | relatedpages = [[Rope]], [[Shovel]]
            | text         = Dungeon Survival Guide<br><br>Don't explore the dungeons before you tested your skills in the training cellars of our academy. You will find dungeons somewhere in the wilderness. Don't enter dungeons without equipment. Especially a rope and a shovel will prove valuable. Make sure you have a supply of torches with you, while wandering into the unknown. It's wise to travel the dungeons in groups and not alone. For more help read all the books of the academy before you begin exploring. Traveling in the dungeons will reward the cautious and brave, but punish the reckless.
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_BUILDING_TEXT = """
            {{Infobox Building|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Theater Avenue 8b
            | implemented  = Pre-6.0
            | type         = House
            | location     = South-east of depot, two floors up.
            | posx         = 126.101
            | posy         = 124.48
            | posz         = 5
            | street       = Theater Avenue
            | houseid      = 20315
            | size         = 26
            | beds         = 3
            | rent         = 1370
            | city         = Carlin
            | openwindows  = 3
            | floors       = 1
            | rooms        = 1
            | furnishings  = 1 [[Wall Lamp]].
            | notes        =${" "}
            | image        = [[File:Theater Avenue 8b.png]]
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_CORPSE_TEXT = """
            {{Infobox Corpse|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Dead Rat
            | article      = a
            | liquid       = [[Blood]]
            | 1decaytime   = 5 minutes.
            | 2decaytime   = 5 minutes.
            | 3decaytime   = 60 seconds.
            | 1volume      = 5
            | 1weight      = 63.00
            | 2weight      = 44.00
            | 3weight      = 30.00
            | corpseof     = [[Rat]], [[Cave Rat]], [[Munster]]
            | sellto       = [[Tom]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Seymour]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Billy]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Humgolf]] ([[Kazordoon]]) '''2''' [[gp]]<br>
            [[Baxter]] ([[Thais]]) '''1''' [[gp]]<br>
            | notes        = These corpses are commonly used by low level players on [[Rookgaard]] to earn some gold for better [[equipment]]. Only fresh corpses are accepted, rotted corpses are ignored.
            | implemented  = Pre-6.0
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_LOCATION_TEXT = """
            {{Infobox Geography
            | implemented  = Pre-6.0
            | ruler        = [[King Tibianus]]
            | population   = {{PAGESINCATEGORY:Thais NPCs|pages}}
            | near         = [[Fibula]], [[Mintwallin]], [[Greenshore]], [[Mount Sternum]]
            | organization = [[Thieves Guild]], [[Tibian Bureau of Investigation]], [[Inquisition]]
            | map          = [[File:Map_thais.jpg]]
            | map2         = [[File:Thais.PNG]]
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_MISSILE_TEXT = """
            {{Infobox Missile|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Throwing Cake Missile
            | implemented  = 7.9
            | missileid    = 42
            | primarytype  = Throwing Weapon
            | shotby       = [[Undead Jester]]'s attack and probably by throwing a [[Throwing Cake]].
            | notes        = This missile is followed by the [[Cream Cake Effect]]: [[File:Cream Cake Effect.gif]]
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_EFFECT_TEXT = """
            {{Infobox Effect|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Fireball Effect
            | effectid     = 7, 82
            | primarytype  = Attack
            | lightradius  = 6
            | lightcolor   = 208
            | causes       = *[[Fireball]] and [[Great Fireball]];
            | effect       = [[Fire Damage]] on target or nothing.
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_MOUNT_TEXT = """
            {{Infobox Mount|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name          = Donkey
            | speed         = 10
            | taming_method = Use a [[Bag of Apple Slices]] on a creature transformed into Donkey.
            | achievement   = Loyal Lad
            | implemented   = 9.1
            | notes         = Go to [[Incredibly Old Witch]]'s house,
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_NPC_TEXT = """
            {{Infobox NPC|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Sam
            | job          = Artisan
            | job2         = Weapon Shopkeeper
            | job3         = Armor Shopkeeper
            | location     = [[Temple Street]] in [[Thais]].
            | city         = Thais
            | posx         = 126.104
            | posy         = 125.200
            | posz         = 7
            | gender       = Male
            | race         = Human
            | buysell      = yes
            | buys         = {{Price to Sell |Axe
            | sells        = {{Price to Buy |Axe
            | sounds       = {{Sound List|Hello there, adventurer! Need a deal in weapons or armor? I'm your man!}}
            | implemented  = Pre-6.0
            | notes        = Sam is the Blacksmith of [[Thais]].
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_OBJECT_TEXT = """
            {{Infobox Object|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Blueberry Bush
            | article      = a
            | objectclass  = Bushes
            | implemented  = 7.1
            | walkable     = no
            | location     = Can be found all around [[Tibia]].
            | notes        = They are the source of the [[blueberry|blueberries]].
            | notes2       = <br />{{JSpoiler|After using [[Blueberry]] Bushes 500 times,
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_OUTFIT_TEXT = """
            {{Infobox Outfit|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Pirate
            | primarytype  = Quest
            | premium      = yes
            | outfit       = premium, see [[Pirate Outfits Quest]].
            | addons       = premium, see [[Pirate Outfits Quest]].
            | achievement  = Swashbuckler
            | implemented  = 7.8
            | artwork      = Pirate Outfits Artwork.jpg
            | notes        = Pirate outfits are perfect for swabbing the deck or walking the plank. Quite dashing and great for sailing.
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_QUEST_TEXT = """
            {{Infobox Quest|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = The Paradox Tower Quest
            | aka          = Riddler Quest, Mathemagics Quest
            | reward       = Up to two of the following:
            | location     = [[Paradox Tower]] near [[Kazordoon]]
            | lvl          = 30
            | lvlrec       = 50
            | log          = yes
            | premium      = yes
            | transcripts  = yes
            | dangers      = [[Wyvern]]s<br /> ([[Mintwallin]]): [[Minotaur]]s,
            | legend       = Surpass the wrath of a madman and subject yourself to his twisted taunting.
            | implemented  = 6.61-6.97
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_STREET_TEXT = """
            {{Infobox Street
            | name         = Sugar Street
            | implemented  = 7.8
            | city         = Liberty Bay
            | notes        = {{StreetStyles|Sugar Street}} is in west
            }}
        """.trimIndent().trimStart() + "\n"
        private val INFOBOX_SPELL_TEXT = """
            {{Infobox Spell|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name          = Light Healing
            | type          = Instant
            | subclass      = Healing
            | words         = exura
            | mana          = 20
            | cooldown      = 1
            | cooldowngroup = 1
            | levelrequired = 8
            | premium       = no
            | voc           = [[Paladin]]s, [[Druid]]s and [[Sorcerer]]s
            | d-abd         = [[Maealil]]
            | p-abd         = [[Maealil]]
            | spellcost     = 0
            | effect        = Restores a small amount of [[HP|health]]. (Cures [[paralysis]].)
            | notes         = A weak, but popular healing spell.
            }}
        """.trimIndent().trimStart() + "\n"
    }
}
