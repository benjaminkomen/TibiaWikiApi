package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.NPC
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveNPCs
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

@Tag(name = "NPCs")
@RestController
@RequestMapping("/api/npcs")
class NPCsController(
    private val retrieveNPCs: RetrieveNPCs,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of npcs")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of npcs retrieved")])
    fun getNPCs(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the NPC names but the full npcs",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiResourceResponses.list(expand, { retrieveNPCs.getNPCsJSON() }, { retrieveNPCs.getNPCsList() })
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific NPC by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "NPC with specified name found"),
            ApiResponse(responseCode = "404", description = "NPC with specified name not found")
        ]
    )
    fun getNPCsByName(@PathVariable("name") name: String): ResponseEntity<String> {
        return WikiResourceResponses.jsonOrNotFound(retrieveNPCs.getNPCJSON(name))
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify an NPC")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed NPC"),
            ApiResponse(responseCode = "400", description = "the provided changed NPC is not valid"),
            ApiResponse(responseCode = "401", description = "not authorized to edit without providing credentials")
        ]
    )
    fun putNPC(@RequestBody npc: NPC, @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?): ResponseEntity<WikiObject> {
        return WikiResourceResponses.modify(modifyAny.modify(npc, editSummary))
    }
}
