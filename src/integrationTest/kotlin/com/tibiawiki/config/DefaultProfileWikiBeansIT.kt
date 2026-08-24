package com.tibiawiki.config

import com.tibiawiki.domain.repositories.ArticleRepository
import com.tibiawiki.domain.repositories.JwikiArticleRepository
import com.tibiawiki.domain.wiki.WikiCallSupport
import com.tibiawiki.domain.wiki.WikiFactory
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.resttestclient.getForEntity
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Cloud Run uses the default profile (live [JwikiArticleRepository], lazy Wiki).
 * This must boot without calling Fandom and still serve readiness.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["LOGGING_JSON=true"]
)
@AutoConfigureTestRestTemplate
class DefaultProfileWikiBeansIT(
    @Autowired private val articleRepository: ArticleRepository,
    @Autowired private val wikiFactory: WikiFactory,
    @Autowired private val wikiCallSupport: WikiCallSupport,
    @Autowired private val restTemplate: TestRestTemplate
) {

    @Test
    fun defaultProfileConstructsLiveRepositoryAndServesReadinessWithoutWikiCalls() {
        assertThat(articleRepository, instanceOf(JwikiArticleRepository::class.java))
        assertThat(wikiFactory.javaClass, `is`(WikiFactory::class.java))
        assertThat(wikiCallSupport.javaClass, `is`(WikiCallSupport::class.java))

        val result = restTemplate.getForEntity<Map<String, Any>>("/actuator/health/readiness")

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body?.get("status"), `is`("UP"))
    }
}
