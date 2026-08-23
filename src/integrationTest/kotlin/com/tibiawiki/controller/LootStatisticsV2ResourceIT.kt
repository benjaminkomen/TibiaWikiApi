package com.tibiawiki.controller

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.repositories.ArticleRepository
import com.tibiawiki.process.RetrieveAny
import com.tibiawiki.process.RetrieveLoot
import io.github.fastily.jwiki.core.NS
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.doReturn
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class LootStatisticsV2ResourceIT {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockitoBean
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun givenGetLootsNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoLootNames() {
        doReturn(listOf("foo", "bar")).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.LOOT.categoryName, LOOT_NAMESPACE)

        val result = restTemplate.getForEntity("/api/v2/loot?expand=false", List::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body!!.size, `is`(2))
        assertThat(result.body!![0], `is`("foo"))
        assertThat(result.body!![1], `is`("bar"))
    }

    @Test
    fun givenGetLootsExpanded_whenCorrectRequest_thenResponseIsOkAndContainsOneLoot() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(listOf("Loot:Amazon")).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.LOOT.categoryName, LOOT_NAMESPACE)
        doReturn(mapOf("Loot:Amazon" to LOOT_AMAZON_TEXT)).`when`(articleRepository).getArticlesFromCategory(listOf("Loot:Amazon"))

        val result = restTemplate.getForEntity("/api/v2/loot?expand=true", List::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body!!.size, `is`(1))
        val loot2 = (result.body!![0] as Map<*, *>)["loot2"] as Map<*, *>
        assertThat(loot2["kills"], `is`("22009"))
        assertThat(loot2["name"], `is`("Amazon"))
        assertThat(loot2["version"], `is`("8.6"))
        assertThat(loot2["pageName"], `is`("Loot:Amazon"))
    }

    @Test
    fun givenGetLootsByName_whenCorrectRequest_thenResponseIsOkAndContainsTheLoot() {
        doReturn(LOOT_AMAZON_TEXT).`when`(articleRepository).getArticle("Loot_Statistics:Amazon")

        val result = restTemplate.getForEntity("/api/v2/loot/Amazon", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.OK))

        val resultAsJSON = JSONObject(result.body).getJSONObject("loot2")
        assertThat(resultAsJSON.get("kills"), `is`("22009"))
        assertThat(resultAsJSON.get("name"), `is`("Amazon"))
        assertThat(resultAsJSON.get("version"), `is`("8.6"))
        assertThat(resultAsJSON.get("pageName"), `is`("Loot_Statistics:Amazon"))
    }

    @Test
    fun givenGetLootsByName_whenCorrectRequest_thenResponseIsOkAndContainsTwoLootEntities() {
        doReturn(LOOT_FERUMBRAS_TEXT).`when`(articleRepository).getArticle("Loot_Statistics:Ferumbras")

        val result = restTemplate.getForEntity("/api/v2/loot/Ferumbras", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.OK))

        val loot2Result = JSONObject(result.body).getJSONObject("loot2")
        assertThat(loot2Result.get("kills"), `is`("49"))
        assertThat(loot2Result.get("name"), `is`("Ferumbras"))
        assertThat(loot2Result.get("version"), `is`("8.6"))
        assertThat(loot2Result.get("pageName"), `is`("Loot_Statistics:Ferumbras"))

        val loot2RewardChestResult = JSONObject(result.body).getJSONObject("loot2_rc")
        assertThat(loot2RewardChestResult.get("kills"), `is`("1"))
        assertThat(loot2RewardChestResult.get("name"), `is`("Ferumbras"))
        assertThat(loot2RewardChestResult.get("version"), `is`("8.6"))
        assertThat(loot2RewardChestResult.get("pageName"), `is`("Loot_Statistics:Ferumbras"))
    }

    @Test
    fun givenGetLootsByName_whenWrongRequest_thenResponseIsNotFound() {
        doReturn(null).`when`(articleRepository).getArticle("Loot:Foobar")

        val result = restTemplate.getForEntity("/api/v2/loot/Foobar", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    companion object {
        private val LOOT_NAMESPACE: NS = RetrieveLoot.makeLootNamespace(112)
        private val LOOT_AMAZON_TEXT =
            """
            {{Loot2
            |version=8.6
            |kills=22009
            |name=Amazon
            |Empty, times:253
            |Dagger, times:17626, amount:1, total:17626
            |Skull, times:17604, amount:1-2, total:26348
            |Gold Coin, times:8829, amount:1-20, total:93176
            |Brown Bread, times:6496, amount:1, total:6496
            |Sabre, times:5098, amount:1, total:5098
            |Girlish Hair Decoration, times:2179, amount:1, total:2179
            |Protective Charm, times:1154, amount:1, total:1154
            |Torch, times:223, amount:1, total:223
            |Crystal Necklace, times:56, amount:1, total:56
            |Small Ruby, times:27, amount:1, total:27
            }}
            """.trimIndent()
        private val LOOT_FERUMBRAS_TEXT =
            """
            __NOWYSIWYG__

            {{Loot2
            |version=8.6
            |kills=49
            |name=Ferumbras
            |Ferumbras' Hat, times:49, total:3
            |Gold Coin, times:48, amount:18-184, total:4751
            |Gold Ingot, times:37, amount:1-2, total:52
            |Great Shield, times:13, amount:1, total:13
            |Spellbook of Lost Souls, times:13, amount:1, total:13
            |Golden Armor, times:12, amount:1, total:12
            }}

            {{Loot2_RC
            |version=8.6
            |kills=1
            |name=Ferumbras
            |Blue Gem, times:1, amount:1, total:1
            |Giant Shimmering Pearl, times:1, amount:1, total:1
            |Gold Coin, times:1, amount:100, total:100
            |Golden Armor, times:1, amount:1, total:1
            |Lightning Legs, times:1
            |Runed Sword, times:1, amount:1, total:1
            |Small Emerald, times:1, amount:10, total:10
            }}

            {{Loot
            |version=8.54
            |kills=4
            |name=Ferumbras
            |[[Gold Coin]], 399
            |[[Small Ruby]], 126
            |[[Small Diamond]], 45
            |[[Gold Ingot]], 6
            |[[Ferumbras' Hat]], 4
            |[[Small Topaz]], 3
            |[[Spellbook of Lost Souls]], 3
            }}
            <br/>Average gold: 99.75
            """.trimIndent()
    }
}
