package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.Street
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveStreets
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

@Tag(name = "Streets")
@RestController
@RequestMapping("/api/streets")
class StreetsController(
    private val retrieveStreets: RetrieveStreets,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of streets")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of streets retrieved")])
    fun getStreets(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the street names but the full streets",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiObjectResponses.list(expand, retrieveStreets.streetsJSON, retrieveStreets.streetsList)
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific street by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "street with specified name found"),
            ApiResponse(responseCode = "404", description = "street with specified name not found")
        ]
    )
    fun getStreetsByName(@PathVariable("name") name: String): ResponseEntity<String> {
        return WikiObjectResponses.byName(retrieveStreets.getStreetJSON(name))
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify a street")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed street"),
            ApiResponse(responseCode = "400", description = "the provided changed street is not valid"),
            ApiResponse(responseCode = "401", description = "not authorized to edit without providing credentials")
        ]
    )
    fun putStreet(@RequestBody street: Street, @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?): ResponseEntity<WikiObject> {
        return WikiObjectResponses.modify(modifyAny.modify(street, editSummary))
    }
}
