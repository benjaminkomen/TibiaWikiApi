package com.tibiawiki.adapters.rest

import com.tibiawiki.config.WikiWriteApiDocs
import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.objects.HuntingPlace
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveByTemplate
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Tag(name = "Hunting Places")
@RestController
@RequestMapping("/api/huntingplaces")
class HuntingPlacesController(
    private val retrieveByTemplate: RetrieveByTemplate,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Get a list of hunting places",
        description = "Infobox Hunt level/skill/defence columns are knight, paladin, and mage only. " +
            "Monk columns are not modeled until TibiaWiki adds them."
    )
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of hunting places retrieved")])
    fun getHuntingPlaces(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the huntingPlace names but the full hunting places",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiResourceResponses.list(
            expand,
            { retrieveByTemplate.articlesAsJSON(InfoboxTemplate.HUNT) },
            { retrieveByTemplate.pageNames(InfoboxTemplate.HUNT) }
        )
    }

    @GetMapping(value = ["/**"], produces = [MediaType.APPLICATION_JSON_VALUE]) // accept special characters such as slashes in path
    @Operation(
        summary = "Get a specific hunting place by name",
        description = BY_NAME_OPENAPI_NOTE
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "huntingPlace with specified name found"),
            ApiResponse(responseCode = "404", description = "huntingPlace with specified name not found")
        ]
    )
    fun getHuntingPlacesByName(request: HttpServletRequest): ResponseEntity<WikiJson> {
        val requestUri = request.requestURI
        val name = URLDecoder.decode(requestUri.split("/huntingplaces/")[1], StandardCharsets.UTF_8)
        return WikiResourceResponses.jsonOrNotFound(retrieveByTemplate.articleAsJSON(name))
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify a huntingPlace")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed huntingPlace"),
            ApiResponse(responseCode = "400", description = "the provided changed huntingPlace is not valid"),
            ApiResponse(responseCode = "401", description = WikiWriteApiDocs.UNAUTHORIZED),
            ApiResponse(responseCode = "403", description = WikiWriteApiDocs.FORBIDDEN)
        ]
    )
    fun putHuntingPlace(@RequestBody huntingPlace: HuntingPlace, @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?): ResponseEntity<WikiObject> {
        return WikiResourceResponses.modify(modifyAny.modify(huntingPlace, editSummary))
    }

    companion object {
        // Catch-all path so titles with slashes work. Do not invent a fake {name}
        // path parameter in OpenAPI — document the slashy names instead.
        const val BY_NAME_OPENAPI_NOTE =
            "Wiki titles may contain slashes (for example Tiquanda/Bandit Caves). " +
                "The remainder of the path after /api/huntingplaces/ is the name; " +
                "this mapping is /** so OpenAPI does not invent a single {name} path parameter."
    }
}
