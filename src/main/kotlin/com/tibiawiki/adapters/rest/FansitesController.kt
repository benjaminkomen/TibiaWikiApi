package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.objects.Fansite
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveByTemplate
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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

@Tag(name = "Fansites")
@RestController
@RequestMapping("/api/fansites")
class FansitesController(
    private val retrieveByTemplate: RetrieveByTemplate,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of fansites")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of fansites retrieved")])
    fun getFansites(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the fansite names but the full fansites",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiResourceResponses.list(
            expand,
            { retrieveByTemplate.asJson(InfoboxTemplate.FANSITE) },
            { retrieveByTemplate.names(InfoboxTemplate.FANSITE) }
        )
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific fansite by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "fansite with specified name found"),
            ApiResponse(responseCode = "404", description = "fansite with specified name not found")
        ]
    )
    fun getFansitesByName(@PathVariable("name") name: String): ResponseEntity<String> {
        return WikiResourceResponses.jsonOrNotFound(retrieveByTemplate.getJson(name))
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify a fansite")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed fansite"),
            ApiResponse(responseCode = "400", description = "the provided changed fansite is not valid"),
            ApiResponse(responseCode = "401", description = "not authorized to edit without providing credentials")
        ]
    )
    fun putFansite(@RequestBody fansite: Fansite, @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?): ResponseEntity<WikiObject> {
        return WikiResourceResponses.modify(modifyAny.modify(fansite, editSummary))
    }
}
