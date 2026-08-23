package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.Location
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveLocations
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.json.JSONObject
import org.springframework.http.HttpStatus
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

@Tag(name = "Locations")
@RestController
@RequestMapping("/api/locations")
class LocationsController(
    private val retrieveLocations: RetrieveLocations,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of locations")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of locations retrieved")])
    fun getLocations(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the location names but the full locations",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return ResponseEntity.ok()
            .body(
                if (expand != null && expand) {
                    retrieveLocations.locationsJSON.map<Any>(JSONObject::toMap)
                } else {
                    retrieveLocations.locationsList
                }
            )
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific location by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "location with specified name found"),
            ApiResponse(responseCode = "404", description = "location with specified name not found")
        ]
    )
    fun getLocationsByName(@PathVariable("name") name: String): ResponseEntity<String> {
        return retrieveLocations.getLocationJSON(name)
            .map { a: JSONObject ->
                ResponseEntity.ok()
                    .body(a.toString(2))
            }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify a location")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed location"),
            ApiResponse(responseCode = "400", description = "the provided changed location is not valid"),
            ApiResponse(responseCode = "401", description = "not authorized to edit without providing credentials")
        ]
    )
    fun putLocation(@RequestBody location: Location, @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?): ResponseEntity<WikiObject> {
        return modifyAny.modify(location, editSummary)
            .map { a: WikiObject ->
                ResponseEntity.ok()
                    .body(a)
            }
            .recover<ValidationException>(ValidationException::class.java) { ResponseEntity.badRequest().build() }
            .recover { ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() }
            .get()
    }
}
