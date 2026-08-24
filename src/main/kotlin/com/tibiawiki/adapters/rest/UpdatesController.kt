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

@Tag(name = "Updates")
@RestController
@RequestMapping("/api/updates")
class UpdatesController(
    private val retrieveByTemplate: RetrieveByTemplate
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of updates")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of updates retrieved")])
    fun getUpdates(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the update names but the full updates",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiResourceResponses.list(
            expand,
            { retrieveByTemplate.asJson(InfoboxTemplate.UPDATE) },
            { retrieveByTemplate.names(InfoboxTemplate.UPDATE) }
        )
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific update by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "update with specified name found"),
            ApiResponse(responseCode = "404", description = "update with specified name not found")
        ]
    )
    fun getUpdatesByName(@PathVariable("name") name: String): ResponseEntity<WikiJson> {
        return WikiResourceResponses.jsonOrNotFound(retrieveByTemplate.getJson(name))
    }
}
