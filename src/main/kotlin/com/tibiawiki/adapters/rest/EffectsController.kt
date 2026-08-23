package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.Effect
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveEffects
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

@Tag(name = "Effects")
@RestController
@RequestMapping("/api/effects")
class EffectsController(
    private val retrieveEffects: RetrieveEffects,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of effects")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of effects retrieved")])
    fun getEffects(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the effect names but the full effects",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiResourceResponses.list(expand, retrieveEffects.effectsJSON, retrieveEffects.effectsList)
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific effect by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "effect with specified name found"),
            ApiResponse(responseCode = "404", description = "effect with specified name not found")
        ]
    )
    fun getEffectsByName(@PathVariable("name") name: String): ResponseEntity<String> {
        return WikiResourceResponses.json(retrieveEffects.getEffectJSON(name))
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify an effect")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed effect"),
            ApiResponse(responseCode = "400", description = "the provided changed effect is not valid"),
            ApiResponse(responseCode = "401", description = "not authorized to edit without providing credentials")
        ]
    )
    fun putEffect(@RequestBody effect: Effect, @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?): ResponseEntity<WikiObject> {
        return WikiResourceResponses.modified(modifyAny.modify(effect, editSummary))
    }
}
