package com.tibiawiki.adapters.rest

import com.tibiawiki.config.WikiWriteApiDocs
import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveByTemplate
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper

@Tag(name = "Wiki Categories")
@RestController
@RequestMapping("/api/{category:" + WikiCategory.PATH_PATTERN + "}")
class WikiCategoryController(
    private val retrieveByTemplate: RetrieveByTemplate,
    private val modifyAny: ModifyAny,
    private val objectMapper: ObjectMapper
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of wiki objects for a category")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of wiki objects retrieved")])
    fun getWikiObjects(
        @CategoryPath @PathVariable("category") category: String,
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the page names but the full wiki objects",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        val resource = WikiCategory.fromPath(category) ?: return ResponseEntity.notFound().build()
        return WikiResourceResponses.list(
            expand,
            { retrieveByTemplate.articlesAsJSON(resource.template) },
            { retrieveByTemplate.pageNames(resource.template) }
        )
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific wiki object by category and name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "wiki object with specified name found"),
            ApiResponse(responseCode = "404", description = "wiki object with specified name not found")
        ]
    )
    fun getWikiObjectByName(
        @CategoryPath @PathVariable("category") category: String,
        @PathVariable("name") name: String
    ): ResponseEntity<WikiJson> {
        WikiCategory.fromPath(category) ?: return ResponseEntity.notFound().build()
        return WikiResourceResponses.jsonOrNotFound(retrieveByTemplate.articleAsJSON(name))
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify a wiki object")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed wiki object"),
            ApiResponse(responseCode = "400", description = "the provided changed wiki object is not valid"),
            ApiResponse(responseCode = "401", description = WikiWriteApiDocs.UNAUTHORIZED),
            ApiResponse(responseCode = "403", description = WikiWriteApiDocs.FORBIDDEN),
            ApiResponse(responseCode = "404", description = "unknown wiki category")
        ]
    )
    fun putWikiObject(
        @CategoryPath @PathVariable("category") category: String,
        @RequestBody body: JsonNode,
        @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?
    ): ResponseEntity<WikiObject> {
        val resource = WikiCategory.fromPath(category) ?: return ResponseEntity.notFound().build()
        val wikiObject = try {
            resource.readWikiObject(objectMapper, body)
        } catch (_: RuntimeException) {
            return ResponseEntity.badRequest().build()
        }
        return WikiResourceResponses.modify(modifyAny.modify(wikiObject, editSummary))
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Parameter(
    description = "Wiki category collection path. Hunting places, loot, fansites, and CipSoft members stay on dedicated controllers.",
    schema = Schema(
        allowableValues = [
            "achievements", "books", "buildings", "charms", "corpses", "creatures", "effects", "familiars",
            "imbuements", "items", "keys", "locations", "missiles", "mounts", "npcs", "objects", "outfits",
            "quests", "spells", "streets", "updates", "worlds"
        ]
    )
)
annotation class CategoryPath
