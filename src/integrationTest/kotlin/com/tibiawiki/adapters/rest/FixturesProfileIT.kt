package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.repositories.ArticleRepository
import com.tibiawiki.domain.repositories.FixtureArticleRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.json.JSONObject
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Slice of ITs that boot with the offline `fixtures` profile (no jwiki, no Fandom).
 * Assertions use regression/fixtures rather than Mockito stubs.
 */
@Tag("fixtures")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("fixtures")
@AutoConfigureTestRestTemplate
class FixturesProfileIT(
    @Autowired private val restTemplate: TestRestTemplate,
    @Autowired private val articleRepository: ArticleRepository
) {

    @Test
    fun fixturesProfileUsesOfflineArticleRepository() {
        assertThat(articleRepository, instanceOf(FixtureArticleRepository::class.java))
    }

    @Test
    fun givenGetWikiPageByTitle_whenFixtureExists_thenResponseIsOk() {
        val result = restTemplate.getForEntity("/api/pages/Dragon", String::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        val json = JSONObject(result.body)
        assertThat(json.get("templateType"), `is`("Creature"))
        assertThat(json.get("name"), `is`("Dragon"))
        assertThat(json.get("hp"), `is`("1000"))
    }

    @Test
    fun givenGetWikiPageByTitle_whenFixtureMissing_thenResponseIsNotFound() {
        val result = restTemplate.getForEntity("/api/pages/ThisDoesNotExistXYZ123", String::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun givenGetLootV2List_whenFixturesLoaded_thenResponseContainsFerumbras() {
        val result = restTemplate.getForEntity("/api/v2/loot", List::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body, hasItem("Ferumbras"))
    }

    @Test
    fun givenGetLootV2ByName_whenFixtureExists_thenResponseContainsLoot2() {
        val result = restTemplate.getForEntity("/api/v2/loot/Ferumbras", String::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        val loot2 = JSONObject(result.body).getJSONObject("loot2")
        assertThat(loot2.get("name"), `is`("Ferumbras"))
        assertThat(loot2.get("kills"), `is`("143"))
        assertThat(loot2.get("pageName"), `is`("Loot_Statistics:Ferumbras"))
    }

    @Test
    fun givenGetCreatureByName_whenFixtureExists_thenResponseIsOk() {
        val result = restTemplate.getForEntity("/api/creatures/Dragon", String::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        val json = JSONObject(result.body)
        assertThat(json.get("templateType"), `is`("Creature"))
        assertThat(json.get("name"), `is`("Dragon"))
    }
}
