package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.enums.InfoboxTemplate
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Imbuements")
@RestController
@RequestMapping("/api/imbuements")
class ImbuementsController(
    private val retrieveByTemplate: RetrieveByTemplate
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of imbuements")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of imbuements retrieved")])
    fun getImbuements(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the imbuement names but the full imbuements",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiResourceResponses.list(
            expand,
            { retrieveByTemplate.asJson(InfoboxTemplate.IMBUEMENT) },
            { retrieveByTemplate.names(InfoboxTemplate.IMBUEMENT) }
        )
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific imbuement by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "imbuement with specified name found"),
            ApiResponse(responseCode = "404", description = "imbuement with specified name not found")
        ]
    )
    fun getImbuementsByName(@PathVariable("name") name: String): ResponseEntity<WikiJson> {
        return WikiResourceResponses.jsonOrNotFound(retrieveByTemplate.getJson(name))
    }
}
