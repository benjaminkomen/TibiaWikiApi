package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.Missile
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveMissiles
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

@Tag(name = "Missiles")
@RestController
@RequestMapping("/api/missiles")
class MissilesController(
    private val retrieveMissiles: RetrieveMissiles,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of missiles")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of missiles retrieved")])
    fun getMissiles(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the missile names but the full missiles",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiResourceResponses.list(expand, retrieveMissiles.missilesJSON, retrieveMissiles.missilesList)
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific missile by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "missile with specified name found"),
            ApiResponse(responseCode = "404", description = "missile with specified name not found")
        ]
    )
    fun getMissilesByName(@PathVariable("name") name: String): ResponseEntity<String> {
        return WikiResourceResponses.json(retrieveMissiles.getMissileJSON(name))
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify a missile")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed missile"),
            ApiResponse(responseCode = "400", description = "the provided changed missile is not valid"),
            ApiResponse(responseCode = "401", description = "not authorized to edit without providing credentials")
        ]
    )
    fun putMissile(@RequestBody missile: Missile, @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?): ResponseEntity<WikiObject> {
        return WikiResourceResponses.modified(modifyAny.modify(missile, editSummary))
    }
}
