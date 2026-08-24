package com.tibiawiki.adapters.rest

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.matchesPattern
import org.hamcrest.Matchers.not
import org.json.JSONObject
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Swagger UI on tibiawiki.dev failed with:
 * "Unable to render this definition / The provided definition does not specify
 * a valid version field." because `/api-docs` was OpenAPI 3.1.0 and the bundled
 * UI only accepts `openapi: 3.0.n`. This IT is the Gradle smoking gun for that.
 */
@Tag("fixtures")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("fixtures")
@AutoConfigureTestRestTemplate
class SwaggerUiIT(
    @Autowired private val restTemplate: TestRestTemplate
) {

    @Test
    fun apiDocsIsOpenApi30SoBundledSwaggerUiCanRender() {
        val result = restTemplate.getForEntity("/api-docs", String::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        val spec = JSONObject(result.body)
        assertThat(
            spec.optString("openapi", ""),
            matchesPattern("^3\\.0\\.\\d+$")
        )
        assertThat(spec.getJSONObject("info").getString("title"), `is`("TibiaWikiApi"))
        assertThat(spec.has("paths") && spec.getJSONObject("paths").length() > 0, `is`(true))
    }

    @Test
    fun swaggerInitializerDoesNotEmbedPetstoreAndPointsAtThisApi() {
        val result = restTemplate.getForEntity("/swagger-ui/swagger-initializer.js", String::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        val body = result.body!!
        assertThat(body.lowercase(), not(containsString("petstore.swagger.io")))
        assertThat(
            body.contains("/api-docs") || body.contains("swagger-config"),
            `is`(true)
        )
    }

    @Test
    fun swaggerConfigUrlIsThisServicesApiDocs() {
        val result = restTemplate.getForEntity("/api-docs/swagger-config", String::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(JSONObject(result.body).getString("url"), `is`("/api-docs"))
    }

    @Test
    fun swaggerUiIndexLoadsBundleAndInitializer() {
        val result = restTemplate.getForEntity("/swagger-ui/index.html", String::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.headers.contentType?.isCompatibleWith(MediaType.TEXT_HTML), `is`(true))
        val body = result.body!!
        assertThat(body, containsString("id=\"swagger-ui\""))
        assertThat(body, containsString("swagger-ui-bundle.js"))
        assertThat(body, containsString("swagger-initializer.js"))
    }
}
