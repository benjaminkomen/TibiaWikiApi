package com.tibiawiki.adapters.rest

import com.tibiawiki.config.WikiWriteApiDocs
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.matchesPattern
import org.hamcrest.Matchers.not
import org.json.JSONArray
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
 * Docs/UI wiring that wiki golden compares never see. Status-200 HTML is not
 * enough: initializer must not advertise Petstore, swagger-config must point
 * at this service's `/api-docs`, and `openapi` must be 3.0.n so Swagger UI
 * builds that reject 3.1.x still render (the "valid version field" error).
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

    @Test
    fun apiDocsExpandsEveryWikiCategoryOntoItsOwnPathAndTag() {
        val result = restTemplate.getForEntity("/api-docs", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.OK))

        val spec = JSONObject(result.body)
        val paths = spec.getJSONObject("paths")
        val pathKeys = paths.keys().asSequence().toSet()
        val leftoverTemplates = pathKeys.filter { it.contains("{category") }
        assertThat(
            "generic /api/{category} templates should be replaced by concrete paths",
            leftoverTemplates,
            `is`(emptyList())
        )

        val tagNames = tagNames(spec)
        WikiCategory.entries.forEach { category ->
            val collection = "/api/${category.path}"
            val byName = "/api/${category.path}/{name}"
            assertThat(pathKeys, hasItem(collection))
            assertThat(pathKeys, hasItem(byName))
            assertThat(tagNames, hasItem(category.tag))

            val collectionGet = paths.getJSONObject(collection).getJSONObject("get")
            val collectionPut = paths.getJSONObject(collection).getJSONObject("put")
            val byNameGet = paths.getJSONObject(byName).getJSONObject("get")
            assertThat(stringValues(collectionGet.getJSONArray("tags")), hasItem(category.tag))
            assertThat(stringValues(collectionPut.getJSONArray("tags")), hasItem(category.tag))
            assertThat(stringValues(byNameGet.getJSONArray("tags")), hasItem(category.tag))

            val getParamNames = parameterNames(collectionGet)
            assertThat(getParamNames, hasItem("expand"))
            assertThat(getParamNames, not(hasItem("category")))
            assertThat(parameterNames(byNameGet), hasItem("name"))
            assertThat(parameterNames(byNameGet), not(hasItem("category")))
        }

        val achievementsPut = paths.getJSONObject("/api/achievements").getJSONObject("put")
        assertThat(achievementsPut.getJSONObject("responses").has("401"), `is`(true))
        assertThat(achievementsPut.getJSONObject("responses").has("403"), `is`(true))
        assertThat(achievementsPut.toString(), containsString(WikiWriteApiDocs.SECURITY_SCHEME))

        assertThat(pathKeys, hasItem("/api/huntingplaces"))
        assertThat(pathKeys, hasItem("/api/fansites"))
        assertThat(tagNames, not(hasItem("Wiki Categories")))
    }

    private fun tagNames(spec: JSONObject): List<String> {
        if (!spec.has("tags")) {
            return emptyList()
        }
        val tags = spec.getJSONArray("tags")
        return List(tags.length()) { tags.getJSONObject(it).getString("name") }
    }

    private fun parameterNames(operation: JSONObject): List<String> {
        if (!operation.has("parameters")) {
            return emptyList()
        }
        val parameters = operation.getJSONArray("parameters")
        return List(parameters.length()) { parameters.getJSONObject(it).optString("name") }
    }

    private fun stringValues(array: JSONArray): List<String> {
        return List(array.length()) { array.getString(it) }
    }
}
