package com.tibiawiki.controller

import com.tibiawiki.domain.repositories.ArticleRepository
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.resttestclient.getForEntity
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ActuatorIT(
    @Autowired private val restTemplate: TestRestTemplate
) {

    @MockitoBean
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun `health is up without calling the wiki`() {
        val result = restTemplate.getForEntity<Map<String, Any>>("/actuator/health")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("UP", result.body?.get("status"))
        verifyNoInteractions(articleRepository)
    }

    @Test
    fun `liveness probe is up without calling the wiki`() {
        val result = restTemplate.getForEntity<Map<String, Any>>("/actuator/health/liveness")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("UP", result.body?.get("status"))
        verifyNoInteractions(articleRepository)
    }

    @Test
    fun `readiness probe is up without calling the wiki`() {
        val result = restTemplate.getForEntity<Map<String, Any>>("/actuator/health/readiness")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("UP", result.body?.get("status"))
        verifyNoInteractions(articleRepository)
    }

    @Test
    fun `info exposes build coordinates without calling the wiki`() {
        val result = restTemplate.getForEntity<String>("/actuator/info")

        assertEquals(HttpStatus.OK, result.statusCode)
        val body = JSONObject(result.body)
        assertTrue(body.has("build"))
        assertEquals("TibiaWikiApi", body.getJSONObject("build").getString("name"))
        assertEquals("2.0.0", body.getJSONObject("build").getString("version"))
        verifyNoInteractions(articleRepository)
    }
}
