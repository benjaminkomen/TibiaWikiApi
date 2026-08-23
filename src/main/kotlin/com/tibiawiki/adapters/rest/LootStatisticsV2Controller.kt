package com.tibiawiki.adapters.rest

import com.tibiawiki.process.RetrieveLoot
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Loot Statistics")
@RequestMapping("/api/v2/loot")
class LootStatisticsV2Controller(
    private val retrieveLoot: RetrieveLoot
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of loot statistics")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "list of loot statistics retrieved")
        ]
    )
    fun getLoot(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the loot statistics page names but the full loot statistics",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiObjectResponses.list(expand, retrieveLoot.getAllLootPartsJSON(), retrieveLoot.getLootList())
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific loot statistics page by creature name")
    fun getLootByName(@PathVariable("name") name: String): ResponseEntity<String> {
        return WikiObjectResponses.byName(retrieveLoot.getAllLootPartsJSON("Loot_Statistics:$name"))
    }
}
