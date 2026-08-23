package com.tibiawiki.config

import com.tibiawiki.TestUtils.makeHttpHeaders
import com.tibiawiki.domain.objects.WikiObjectFixtures
import com.tibiawiki.domain.repositories.ArticleRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doReturn
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["wiki.write.enabled=false"]
)
@AutoConfigureTestRestTemplate
class WikiWriteDisabledIT {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockitoBean
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun putIsForbiddenWhenWritesAreDisabled() {
        val headers = makeHttpHeaders("[bot] should not reach wiki")
        val result = restTemplate.exchange(
            "/api/creatures",
            HttpMethod.PUT,
            HttpEntity(WikiObjectFixtures.creature(), headers),
            String::class.java
        )

        assertThat(result.statusCode, `is`(HttpStatus.FORBIDDEN))
        assertThat(result.body!!.contains("Wiki writes are disabled"), `is`(true))
    }

    @Test
    fun getStillSucceedsWhenWritesAreDisabled() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(anyString())

        val result = restTemplate.getForEntity("/api/creatures", String::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
    }
}

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["wiki.write.enabled=true", "wiki.write.token=test-write-token"]
)
@AutoConfigureTestRestTemplate
class WikiWriteTokenIT {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockitoBean
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun putIsUnauthorizedWithoutToken() {
        val result = restTemplate.exchange(
            "/api/creatures",
            HttpMethod.PUT,
            HttpEntity(WikiObjectFixtures.creature(), makeHttpHeaders("edit")),
            String::class.java
        )

        assertThat(result.statusCode, `is`(HttpStatus.UNAUTHORIZED))
    }

    @Test
    fun putSucceedsWithMatchingWriteToken() {
        doReturn("{{Infobox Creature|List={{{1|}}}|GetValue={{{GetValue|}}}\n| name = Dragon\n}}")
            .`when`(articleRepository).getArticle("Dragon")
        doReturn(true).`when`(articleRepository).modifyArticle(anyString(), anyString(), anyString())

        val headers = makeHttpHeaders("[bot] editing during integration test")
        headers[WikiWriteFilter.TOKEN_HEADER] = "test-write-token"
        val result = restTemplate.exchange(
            "/api/creatures",
            HttpMethod.PUT,
            HttpEntity(WikiObjectFixtures.creature(), headers),
            Void::class.java
        )

        assertThat(result.statusCode, `is`(HttpStatus.OK))
    }
}

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class WikiWriteOpenApiAndCorsIT {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockitoBean
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun openApiDocumentsRealPutAuthInsteadOfFakeCredentials401() {
        val spec = restTemplate.getForEntity("/api-docs", String::class.java)
        assertThat(spec.statusCode, `is`(HttpStatus.OK))
        val body = spec.body!!
        assertThat(body.contains("not authorized to edit without providing credentials"), `is`(false))
        assertThat(body.contains(WikiWriteApiDocs.UNAUTHORIZED), `is`(true))
        assertThat(body.contains(WikiWriteApiDocs.FORBIDDEN), `is`(true))
        assertThat(body.contains(WikiWriteApiDocs.SECURITY_SCHEME), `is`(true))
    }

    @Test
    fun corsAllowsGetFromUiOriginAndOmitsPut() {
        val headers = HttpHeaders()
        headers.origin = "https://tibiawiki.dev"
        headers.set("Access-Control-Request-Method", "GET")
        val preflight = restTemplate.exchange(
            "/api/corpses",
            HttpMethod.OPTIONS,
            HttpEntity<Void>(headers),
            Void::class.java
        )

        assertThat(preflight.statusCode.value() < 400, `is`(true))
        assertThat(preflight.headers.accessControlAllowOrigin, `is`("https://tibiawiki.dev"))
        assertThat(preflight.headers.accessControlAllowCredentials == true, `is`(false))
        val allowedMethods = preflight.headers.accessControlAllowMethods?.map { it.name() } ?: emptyList()
        if (allowedMethods.isNotEmpty()) {
            assertThat(allowedMethods.contains("GET"), `is`(true))
            assertThat(allowedMethods.contains("PUT"), `is`(false))
        }
    }

    @Test
    fun corsDoesNotAllowUnknownOrigin() {
        val headers = HttpHeaders()
        headers.origin = "https://evil.example"
        val result = restTemplate.exchange(
            "/api/corpses",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertThat(result.statusCode, `is`(HttpStatus.FORBIDDEN))
        assertThat(result.headers.accessControlAllowOrigin, `is`(nullValue()))
    }

    @Test
    fun getListStillReturnsOk() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(anyString())

        val result = restTemplate.getForEntity("/api/corpses", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body, `is`(not(nullValue())))
    }
}
