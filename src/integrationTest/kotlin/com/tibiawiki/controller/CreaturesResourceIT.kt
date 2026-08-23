package com.tibiawiki.controller

import com.tibiawiki.TestUtils.makeHttpHeaders
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.objects.Creature
import com.tibiawiki.domain.objects.WikiObjectFixtures
import com.tibiawiki.domain.repositories.ArticleRepository
import com.tibiawiki.process.RetrieveAny
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doReturn
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class CreaturesResourceIT {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockitoBean
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun givenGetCreaturesNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoCreatureNames() {
        doReturn(listOf("baz")).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(listOf("foo", "bar", "baz")).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.CREATURE.categoryName)

        val result = restTemplate.getForEntity("/api/creatures?expand=false", List::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body!!.size, `is`(2))
        assertThat(result.body!![0], `is`("foo"))
        assertThat(result.body!![1], `is`("bar"))
    }

    @Test
    fun givenGetCreaturesExpanded_whenCorrectRequest_thenResponseIsOkAndContainsOneCreature() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(listOf("Dragon")).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.CREATURE.categoryName)
        doReturn(mapOf("Dragon" to INFOBOX_CREATURE_TEXT)).`when`(articleRepository).getArticlesFromCategory(listOf("Dragon"))

        val result = restTemplate.getForEntity("/api/creatures?expand=true", List::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body!!.size, `is`(1))
        val creature = result.body!![0] as Map<*, *>
        assertThat(creature["templateType"], `is`("Creature"))
        assertThat(creature["name"], `is`("Dragon"))
        assertThat(creature["article"], `is`("a"))
        assertThat(creature["actualname"], `is`("dragon"))
        assertThat(creature["plural"], `is`("dragons"))
        assertThat(creature["hp"], `is`("1000"))
        assertThat(creature["exp"], `is`("700"))
        assertThat(creature["armor"], `is`("25"))
        assertThat(creature["summon"], `is`("--"))
        assertThat(creature["convince"], `is`("--"))
        assertThat(creature["illusionable"], `is`("yes"))
        assertThat(creature["creatureclass"], `is`("Reptiles"))
        assertThat(creature["primarytype"], `is`("Dragons"))
        assertThat(creature["bestiaryclass"], `is`("Dragon"))
        assertThat(creature["bestiarylevel"], `is`("Medium"))
    }

    @Test
    fun givenGetCreaturesByName_whenCorrectRequest_thenResponseIsOkAndContainsTheCreature() {
        doReturn(INFOBOX_CREATURE_TEXT).`when`(articleRepository).getArticle("Dragon")

        val result = restTemplate.getForEntity("/api/creatures/Dragon", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.OK))

        val resultAsJSON = JSONObject(result.body)
        assertThat(resultAsJSON.get("templateType"), `is`("Creature"))
        assertThat(resultAsJSON.get("name"), `is`("Dragon"))
        assertThat(resultAsJSON.get("article"), `is`("a"))
        assertThat(resultAsJSON.get("actualname"), `is`("dragon"))
        assertThat(resultAsJSON.get("plural"), `is`("dragons"))
        assertThat(resultAsJSON.get("hp"), `is`("1000"))
        assertThat(resultAsJSON.get("exp"), `is`("700"))
        assertThat(resultAsJSON.get("armor"), `is`("25"))
        assertThat(resultAsJSON.get("summon"), `is`("--"))
        assertThat(resultAsJSON.get("convince"), `is`("--"))
        assertThat(resultAsJSON.get("illusionable"), `is`("yes"))
        assertThat(resultAsJSON.get("creatureclass"), `is`("Reptiles"))
        assertThat(resultAsJSON.get("primarytype"), `is`("Dragons"))
        assertThat(resultAsJSON.get("bestiaryclass"), `is`("Dragon"))
        assertThat(resultAsJSON.get("bestiarylevel"), `is`("Medium"))
    }

    @Test
    fun givenGetCreaturesByName_whenWrongRequest_thenResponseIsNotFound() {
        doReturn(null).`when`(articleRepository).getArticle("Foobar")

        val result = restTemplate.getForEntity("/api/creatures/Foobar", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun givenPutCreature_whenCorrectRequest_thenResponseIsOkAndContainsTheModifiedCreature() {
        val editSummary = "[bot] editing during integration test"
        doReturn(INFOBOX_CREATURE_TEXT).`when`(articleRepository).getArticle("Dragon")
        doReturn(true).`when`(articleRepository).modifyArticle(anyString(), anyString(), anyString())

        val result = restTemplate.exchange("/api/creatures", HttpMethod.PUT, HttpEntity(makeCreature(), makeHttpHeaders(editSummary)), Void::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.OK))
    }

    @Test
    fun givenPutCreature_whenCorrectRequestButUnableToEditWiki_thenResponseIsBadRequest() {
        val editSummary = "[bot] editing during integration test"
        doReturn(INFOBOX_CREATURE_TEXT).`when`(articleRepository).getArticle("Dragon")
        doReturn(false).`when`(articleRepository).modifyArticle(anyString(), anyString(), anyString())

        val result = restTemplate.exchange("/api/creatures", HttpMethod.PUT, HttpEntity(makeCreature(), makeHttpHeaders(editSummary)), Void::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.BAD_REQUEST))
    }

    private fun makeCreature(): Creature = WikiObjectFixtures.creature()

    companion object {
        private val INFOBOX_CREATURE_TEXT =
            """
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
            """.trimIndent()
    }
}
