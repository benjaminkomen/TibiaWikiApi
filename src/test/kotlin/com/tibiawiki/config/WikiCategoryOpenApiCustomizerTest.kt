package com.tibiawiki.config

import com.tibiawiki.adapters.rest.WikiCategory
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.tags.Tag
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

class WikiCategoryOpenApiCustomizerTest {

    @Test
    fun expandsGenericTemplatesIntoConcreteCategoryPaths() {
        val openApi = sampleSpec()

        WikiCategoryOpenApiCustomizer().customise(openApi)

        val leftover = openApi.paths.keys.filter { WikiCategoryOpenApiCustomizer.isGenericCategoryPath(it) }
        assertThat(leftover, empty())

        WikiCategory.entries.forEach { category ->
            val collection = openApi.paths[WikiCategoryOpenApiCustomizer.collectionPath(category)]
            val byName = openApi.paths[WikiCategoryOpenApiCustomizer.byNamePath(category)]
            assertThat("${category.path} collection", collection, not(nullValue()))
            assertThat("${category.path} by name", byName, not(nullValue()))

            assertThat(collection!!.get.tags, contains(category.tag))
            assertThat(collection.put.tags, contains(category.tag))
            assertThat(byName!!.get.tags, contains(category.tag))

            assertThat(collection.get.summary, `is`("Get a list of ${category.tag}"))
            assertThat(collection.put.summary, `is`("Modify a ${category.tag} wiki object"))
            assertThat(byName.get.summary, `is`("Get a specific ${category.tag} entry by name"))

            assertThat(parameterNames(collection.get), not(hasItem("category")))
            assertThat(parameterNames(collection.get), hasItem("expand"))
            assertThat(parameterNames(byName.get), not(hasItem("category")))
            assertThat(parameterNames(byName.get), hasItem("name"))

            assertThat(collection.get.operationId, `is`("getWikiObjects_${category.path}"))
            assertThat(collection.put.operationId, `is`("putWikiObject_${category.path}"))
            assertThat(byName.get.operationId, `is`("getWikiObjectByName_${category.path}"))
        }

        assertThat(openApi.paths["/api/huntingplaces"], not(nullValue()))
        assertThat(openApi.paths["/api/huntingplaces"]!!.get.tags, contains("Hunting Places"))

        val tagNames = openApi.tags.map { it.name }
        assertThat(tagNames, not(hasItem(WikiCategoryOpenApiCustomizer.GENERIC_TAG)))
        WikiCategory.entries.forEach { category ->
            assertThat(tagNames, hasItem(category.tag))
        }
    }

    @Test
    fun expandsRegexConstrainedSpringMvcPathKeys() {
        val openApi = OpenAPI().paths(
            Paths()
                .addPathItem(
                    "/api/{category:${WikiCategory.PATH_PATTERN}}",
                    collectionPathItem()
                )
                .addPathItem(
                    "/api/{category:${WikiCategory.PATH_PATTERN}}/{name}",
                    byNamePathItem()
                )
        )

        WikiCategoryOpenApiCustomizer().customise(openApi)

        assertThat(openApi.paths.containsKey("/api/achievements"), `is`(true))
        assertThat(openApi.paths.containsKey("/api/items/{name}"), `is`(true))
        assertThat(
            openApi.paths.keys.filter { WikiCategoryOpenApiCustomizer.isGenericCategoryPath(it) },
            empty()
        )
    }

    @Test
    fun leavesSpecUnchangedWhenTemplatesAreMissing() {
        val openApi = OpenAPI().paths(
            Paths().addPathItem("/api/huntingplaces", dedicatedHuntingPlaces())
        )

        WikiCategoryOpenApiCustomizer().customise(openApi)

        assertThat(openApi.paths.keys, contains("/api/huntingplaces"))
        assertThat(openApi.paths.containsKey("/api/achievements"), `is`(false))
    }

    @Test
    fun wikiWriteCustomizerAnnotatesExpandedPutsWithoutDuplicatingSecurity() {
        val openApi = sampleSpec()
        WikiCategoryOpenApiCustomizer().customise(openApi)
        applyWikiWriteDocs(openApi)

        val put = openApi.paths["/api/achievements"]!!.put
        assertThat(put.responses["401"]!!.description, `is`(WikiWriteApiDocs.UNAUTHORIZED))
        assertThat(put.responses["403"]!!.description, `is`(WikiWriteApiDocs.FORBIDDEN))
        assertThat(put.security, hasSize(1))
        assertThat(put.security[0].containsKey(WikiWriteApiDocs.SECURITY_SCHEME), `is`(true))

        val itemsPut = openApi.paths["/api/items"]!!.put
        assertThat(itemsPut.security, hasSize(1))
        assertThat(itemsPut.responses["401"], not(nullValue()))
    }

    @Test
    fun recognisesGenericTemplateKeys() {
        assertThat(WikiCategoryOpenApiCustomizer.isGenericCollectionPath("/api/{category}"), `is`(true))
        assertThat(WikiCategoryOpenApiCustomizer.isGenericByNamePath("/api/{category}/{name}"), `is`(true))
        assertThat(WikiCategoryOpenApiCustomizer.isGenericCollectionPath("/api/{category:achievements|items}"), `is`(true))
        assertThat(WikiCategoryOpenApiCustomizer.isGenericByNamePath("/api/{category:achievements|items}/{name}"), `is`(true))
        assertThat(WikiCategoryOpenApiCustomizer.isGenericCategoryPath("/api/achievements"), `is`(false))
        assertThat(WikiCategoryOpenApiCustomizer.isGenericCategoryPath("/api/worlds"), `is`(false))
    }

    private fun sampleSpec(): OpenAPI {
        return OpenAPI()
            .paths(
                Paths()
                    .addPathItem("/api/{category}", collectionPathItem())
                    .addPathItem("/api/{category}/{name}", byNamePathItem())
                    .addPathItem("/api/huntingplaces", dedicatedHuntingPlaces())
            )
            .addTagsItem(Tag().name(WikiCategoryOpenApiCustomizer.GENERIC_TAG))
            .addTagsItem(Tag().name("Hunting Places"))
    }

    private fun collectionPathItem(): PathItem {
        val get = Operation()
            .operationId("getWikiObjects")
            .summary("Get a list of wiki objects for a category")
            .addTagsItem(WikiCategoryOpenApiCustomizer.GENERIC_TAG)
            .addParametersItem(Parameter().name("category").`in`("path").required(true))
            .addParametersItem(Parameter().name("expand").`in`("query").required(false))
        get.responses = ApiResponses().addApiResponse("200", ApiResponse().description("ok"))

        val put = Operation()
            .operationId("putWikiObject")
            .summary("Modify a wiki object")
            .addTagsItem(WikiCategoryOpenApiCustomizer.GENERIC_TAG)
            .addParametersItem(Parameter().name("category").`in`("path").required(true))
        put.responses = ApiResponses().addApiResponse("200", ApiResponse().description("ok"))

        return PathItem().get(get).put(put)
    }

    private fun byNamePathItem(): PathItem {
        val get = Operation()
            .operationId("getWikiObjectByName")
            .summary("Get a specific wiki object by category and name")
            .addTagsItem(WikiCategoryOpenApiCustomizer.GENERIC_TAG)
            .addParametersItem(Parameter().name("category").`in`("path").required(true))
            .addParametersItem(Parameter().name("name").`in`("path").required(true))
        get.responses = ApiResponses().addApiResponse("200", ApiResponse().description("ok"))
        return PathItem().get(get)
    }

    private fun dedicatedHuntingPlaces(): PathItem {
        val get = Operation()
            .operationId("getHuntingPlaces")
            .summary("Get a list of hunting places")
            .addTagsItem("Hunting Places")
        get.responses = ApiResponses().addApiResponse("200", ApiResponse().description("ok"))
        return PathItem().get(get)
    }

    private fun applyWikiWriteDocs(openApi: OpenAPI) {
        openApi.paths.values.forEach { pathItem ->
            val put = pathItem.put ?: return@forEach
            put.responses?.addApiResponse(
                "401",
                ApiResponse().description(WikiWriteApiDocs.UNAUTHORIZED)
            )
            put.responses?.addApiResponse(
                "403",
                ApiResponse().description(WikiWriteApiDocs.FORBIDDEN)
            )
            put.addSecurityItem(SecurityRequirement().addList(WikiWriteApiDocs.SECURITY_SCHEME))
        }
    }

    private fun parameterNames(operation: Operation): List<String> {
        return operation.parameters?.mapNotNull { it.name } ?: emptyList()
    }
}
