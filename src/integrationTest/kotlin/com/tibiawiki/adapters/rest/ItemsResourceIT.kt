package com.tibiawiki.adapters.rest

import com.tibiawiki.TestUtils.makeHttpHeaders
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.objects.TibiaObject
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
class ItemsResourceIT {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockitoBean
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun givenGetItemsNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoItemNames() {
        doReturn(listOf("baz")).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(listOf("foo", "bar", "baz")).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ITEM.categoryName)

        val result = restTemplate.getForEntity("/api/items?expand=false", List::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body!!.size, `is`(2))
        assertThat(result.body!![0], `is`("foo"))
        assertThat(result.body!![1], `is`("bar"))
    }

    @Test
    fun givenGetItemsExpanded_whenCorrectRequest_thenResponseIsOkAndContainsOneItem() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(listOf("Carlin Sword")).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ITEM.categoryName)
        doReturn(mapOf("Carlin Sword" to INFOBOX_ITEM_TEXT)).`when`(articleRepository).getArticlesFromCategory(listOf("Carlin Sword"))

        val result = restTemplate.getForEntity("/api/items?expand=true", List::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body!!.size, `is`(1))
        val item = result.body!![0] as Map<*, *>
        assertThat(item["templateType"], `is`("Object"))
        assertThat(item["name"], `is`("Carlin Sword"))
        assertThat(item["article"], `is`("a"))
        assertThat(item["actualname"], `is`("carlin sword"))
        assertThat(item["plural"], `is`("?"))
        assertThat(item["itemid"], `is`(listOf("3283")))
        assertThat(item["marketable"], `is`("yes"))
        assertThat(item["usable"], `is`("yes"))
        assertThat(item["sprites"], `is`("{{Frames|{{Frame Sprite|55266}}}}"))
        assertThat(item["flavortext"], `is`("Foobar"))
        assertThat(item["itemclass"], `is`("Weapons"))
        assertThat(item["primarytype"], `is`("Sword Weapons"))
        assertThat(item["levelrequired"], `is`("0"))
        assertThat(item["hands"], `is`("One"))
        assertThat(item["type"], `is`("Sword"))
        assertThat(item["attack"], `is`("15"))
        assertThat(item["defense"], `is`("13"))
        assertThat(item["defensemod"], `is`("+1"))
        assertThat(item["enchantable"], `is`("no"))
        assertThat(item["weight"], `is`("40.00"))
        assertThat(item["droppedby"], `is`(listOf("Grorlam", "Stone Golem")))
        assertThat(item["value"], `is`("118"))
        assertThat(item["npcvalue"], `is`("118"))
        assertThat(item["npcprice"], `is`("473"))
        assertThat(item["npcvaluerook"], `is`("0"))
        assertThat(item["npcpricerook"], `is`("0"))
        assertThat(item["buyfrom"], `is`("Baltim, Brengus, Cedrik,"))
        assertThat(item["sellto"], `is`("Baltim, Brengus, Cedrik, Esrik,"))
        assertThat(item["notes"], `is`("If you have one of these"))
    }

    @Test
    fun givenGetItemsByName_whenCorrectRequest_thenResponseIsOkAndContainsTheItem() {
        doReturn(INFOBOX_ITEM_TEXT).`when`(articleRepository).getArticle("Carlin Sword")

        val result = restTemplate.getForEntity("/api/items/Carlin Sword", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.OK))

        val resultAsJSON = JSONObject(result.body)
        assertThat(resultAsJSON.get("templateType"), `is`("Object"))
        assertThat(resultAsJSON.get("name"), `is`("Carlin Sword"))
        assertThat(resultAsJSON.get("article"), `is`("a"))
        assertThat(resultAsJSON.get("actualname"), `is`("carlin sword"))
        assertThat(resultAsJSON.get("plural"), `is`("?"))
        assertThat(resultAsJSON.get("itemid").toString(), `is`("[\"3283\"]"))
        assertThat(resultAsJSON.get("marketable"), `is`("yes"))
        assertThat(resultAsJSON.get("usable"), `is`("yes"))
        assertThat(resultAsJSON.get("sprites"), `is`("{{Frames|{{Frame Sprite|55266}}}}"))
        assertThat(resultAsJSON.get("flavortext"), `is`("Foobar"))
        assertThat(resultAsJSON.get("itemclass"), `is`("Weapons"))
        assertThat(resultAsJSON.get("primarytype"), `is`("Sword Weapons"))
        assertThat(resultAsJSON.get("levelrequired"), `is`("0"))
        assertThat(resultAsJSON.get("hands"), `is`("One"))
        assertThat(resultAsJSON.get("type"), `is`("Sword"))
        assertThat(resultAsJSON.get("attack"), `is`("15"))
        assertThat(resultAsJSON.get("defense"), `is`("13"))
        assertThat(resultAsJSON.get("defensemod"), `is`("+1"))
        assertThat(resultAsJSON.get("enchantable"), `is`("no"))
        assertThat(resultAsJSON.get("weight"), `is`("40.00"))
        assertThat(resultAsJSON.get("droppedby").toString(), `is`("[\"Grorlam\",\"Stone Golem\"]"))
        assertThat(resultAsJSON.get("value"), `is`("118"))
        assertThat(resultAsJSON.get("npcvalue"), `is`("118"))
        assertThat(resultAsJSON.get("npcprice"), `is`("473"))
        assertThat(resultAsJSON.get("npcvaluerook"), `is`("0"))
        assertThat(resultAsJSON.get("npcpricerook"), `is`("0"))
        assertThat(resultAsJSON.get("buyfrom"), `is`("Baltim, Brengus, Cedrik,"))
        assertThat(resultAsJSON.get("sellto"), `is`("Baltim, Brengus, Cedrik, Esrik,"))
        assertThat(resultAsJSON.get("notes"), `is`("If you have one of these"))
    }

    @Test
    fun givenGetItemsByName_whenWrongRequest_thenResponseIsNotFound() {
        doReturn(null).`when`(articleRepository).getArticle("Foobar")

        val result = restTemplate.getForEntity("/api/items/Foobar", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun givenPutItem_whenCorrectRequest_thenResponseIsOkAndContainsTheModifiedItem() {
        val editSummary = "[bot] editing during integration test"
        doReturn(INFOBOX_ITEM_TEXT).`when`(articleRepository).getArticle("Carlin Sword")
        doReturn(true).`when`(articleRepository).modifyArticle(anyString(), anyString(), anyString())

        val result = restTemplate.exchange("/api/items", HttpMethod.PUT, HttpEntity(makeItem(), makeHttpHeaders(editSummary)), Void::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.OK))
    }

    @Test
    fun givenPutItem_whenCorrectRequestButUnableToEditWiki_thenResponseIsBadRequest() {
        val editSummary = "[bot] editing during integration test"
        doReturn(INFOBOX_ITEM_TEXT).`when`(articleRepository).getArticle("Carlin Sword")
        doReturn(false).`when`(articleRepository).modifyArticle(anyString(), anyString(), anyString())

        val result = restTemplate.exchange("/api/items", HttpMethod.PUT, HttpEntity(makeItem(), makeHttpHeaders(editSummary)), Void::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.BAD_REQUEST))
    }

    private fun makeItem(): TibiaObject = WikiObjectFixtures.item()

    companion object {
        private val INFOBOX_ITEM_TEXT =
            """
            {{Infobox Object|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name          = Carlin Sword
            | article       = a
            | actualname    = carlin sword
            | plural        = ?
            | itemid        = 3283
            | marketable    = yes
            | usable        = yes
            | sprites       = {{Frames|{{Frame Sprite|55266}}}}
            | flavortext    = Foobar
            | itemclass     = Weapons
            | primarytype   = Sword Weapons
            | levelrequired = 0
            | hands         = One
            | type          = Sword
            | attack        = 15
            | defense       = 13
            | defensemod    = +1
            | enchantable   = no
            | weight        = 40.00
            | droppedby     = {{Dropped By|Grorlam|Stone Golem}}
            | value         = 118
            | npcvalue      = 118
            | npcprice      = 473
            | npcvaluerook  = 0
            | npcpricerook  = 0
            | buyfrom       = Baltim, Brengus, Cedrik,
            | sellto        = Baltim, Brengus, Cedrik, Esrik,
            | notes         = If you have one of these
            }}
            """.trimIndent()
    }
}
