package com.tibiawiki.config

import com.tibiawiki.domain.repositories.ArticleRepository
import com.tibiawiki.domain.repositories.JwikiArticleRepository
import com.tibiawiki.domain.wiki.WikiFactory
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.resttestclient.getForEntity
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.concurrent.TimeUnit

/**
 * Default profile must construct [JwikiArticleRepository] (Mockito ITs do not).
 * An unusable `wiki.api-url` makes [WikiFactory] fail without calling Fandom, and
 * GET /api/creatures returns 503 + Retry-After instead of 500 or hanging.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "wiki.api-url=not-a-url",
        "wiki.warm-on-startup=false"
    ]
)
@AutoConfigureTestRestTemplate
class DefaultProfileWikiInitFailureIT(
    @Autowired private val articleRepository: ArticleRepository,
    @Autowired private val wikiFactory: WikiFactory,
    @Autowired private val restTemplate: TestRestTemplate
) {

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun givenWikiFactoryCannotInitialize_whenListCreatures_then503WithRetryAfter() {
        assertThat(articleRepository, instanceOf(JwikiArticleRepository::class.java))
        assertThat(wikiFactory.javaClass, `is`(WikiFactory::class.java))

        val result = restTemplate.getForEntity<Map<String, Any>>("/api/creatures")

        assertThat(result.statusCode, `is`(HttpStatus.SERVICE_UNAVAILABLE))
        assertThat(result.headers.getFirst("Retry-After"), `is`("5"))
        assertThat(result.body?.get("error"), `is`("wiki_unavailable"))
    }
}
