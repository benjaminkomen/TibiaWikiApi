package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.objects.WikiNamespace
import com.tibiawiki.domain.repositories.ArticleRepository
import com.tibiawiki.process.RetrieveAny
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
class LootStatisticsResourceIT {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockitoBean
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun givenGetLootsNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoLootNames() {
        doReturn(listOf("foo", "bar")).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.LOOT.categoryName, WikiNamespace.LOOT_STATISTICS)

        val result = restTemplate.getForEntity("/api/loot?expand=false", List::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body!!.size, `is`(2))
        assertThat(result.body!![0], `is`("foo"))
        assertThat(result.body!![1], `is`("bar"))
    }

    @Test
    fun givenGetLootsExpanded_whenCorrectRequest_thenResponseIsOkAndContainsOneLoot() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(listOf("Loot:Amazon")).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.LOOT.categoryName, WikiNamespace.LOOT_STATISTICS)
        doReturn(mapOf("Loot:Amazon" to LOOT_AMAZON_TEXT)).`when`(articleRepository).getArticlesFromCategory(listOf("Loot:Amazon"))

        val result = restTemplate.getForEntity("/api/loot?expand=true", List::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body!!.size, `is`(1))
        val loot = result.body!![0] as Map<*, *>
        assertThat(loot["kills"], `is`("22009"))
        assertThat(loot["name"], `is`("Amazon"))
        assertThat(loot["version"], `is`("8.6"))
        assertThat(loot["pageName"], `is`("Loot:Amazon"))
    }

    @Test
    fun givenGetLootsByName_whenCorrectRequest_thenResponseIsOkAndContainsTheLoot() {
        doReturn(LOOT_AMAZON_TEXT).`when`(articleRepository).getArticle("Loot_Statistics:Amazon")

        val result = restTemplate.getForEntity("/api/loot/Amazon", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.OK))

        val resultAsJSON = JSONObject(result.body)
        assertThat(resultAsJSON.get("kills"), `is`("22009"))
        assertThat(resultAsJSON.get("name"), `is`("Amazon"))
        assertThat(resultAsJSON.get("version"), `is`("8.6"))
        assertThat(resultAsJSON.get("pageName"), `is`("Loot_Statistics:Amazon"))
    }

    @Test
    fun givenGetLootsByName_whenLoot2RcComesFirst_thenResponseIsRegularLoot2() {
        doReturn(LOOT_DEMON_RC_FIRST_TEXT).`when`(articleRepository).getArticle("Loot_Statistics:Demon")

        val result = restTemplate.getForEntity("/api/loot/Demon", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.OK))

        val resultAsJSON = JSONObject(result.body)
        assertThat(resultAsJSON.get("kills"), `is`("500"))
        assertThat(resultAsJSON.get("name"), `is`("Demon"))
        assertThat(resultAsJSON.get("version"), `is`("8.6"))
        assertThat(resultAsJSON.get("pageName"), `is`("Loot_Statistics:Demon"))
    }

    @Test
    fun givenGetLootsByName_whenWrongRequest_thenResponseIsNotFound() {
        doReturn(null).`when`(articleRepository).getArticle("Loot:Foobar")

        val result = restTemplate.getForEntity("/api/loot/Foobar", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    companion object {
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
        private val LOOT_DEMON_RC_FIRST_TEXT =
            """
            {{Loot2_RC
            |version=8.6
            |kills=2
            |name=Demon
            |Magic Plate Armor, times:1, amount:1, total:1
            |Demon Shield, times:1, amount:1, total:1
            }}

            {{Loot2
            |version=8.6
            |kills=500
            |name=Demon
            |Gold Coin, times:400, amount:1-200, total:40000
            |Fire Axe, times:10, amount:1, total:10
            }}
            """.trimIndent()
    }
}
