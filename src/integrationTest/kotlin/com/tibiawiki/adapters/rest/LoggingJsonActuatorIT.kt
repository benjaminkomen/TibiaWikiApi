package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.repositories.ArticleRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verifyNoInteractions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.resttestclient.getForEntity
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Cloud Run boots with `LOGGING_JSON=true`. The JSON console path must still
 * expose process-local readiness without touching Fandom.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["LOGGING_JSON=true"]
)
@AutoConfigureTestRestTemplate
class LoggingJsonActuatorIT(
    @Autowired private val restTemplate: TestRestTemplate
) {

    @MockitoBean
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun `readiness is up with JSON console logging and without calling the wiki`() {
        val root = LoggerFactory.getILoggerFactory()
            .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
        assertThat(root.getAppender("CONSOLE") != null, `is`(true))

        val result = restTemplate.getForEntity<Map<String, Any>>("/actuator/health/readiness")

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body?.get("status"), `is`("UP"))
        verifyNoInteractions(articleRepository)
    }
}
