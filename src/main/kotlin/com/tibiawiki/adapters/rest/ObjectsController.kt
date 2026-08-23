package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.TibiaObject
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveObjects
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

@Tag(name = "Objects")
@RestController
@RequestMapping("/api/objects")
class ObjectsController(
    private val retrieveObjects: RetrieveObjects,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of objects")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of objects retrieved")])
    fun getObjects(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the object names but the full objects",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiResourceResponses.list(expand, retrieveObjects.objectsJSON, retrieveObjects.objectsList)
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific object by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "object with specified name found"),
            ApiResponse(responseCode = "404", description = "object with specified name not found")
        ]
    )
    fun getObjectsByName(@PathVariable("name") name: String): ResponseEntity<String> {
        return WikiResourceResponses.json(retrieveObjects.getObjectJSON(name))
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify an object")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed object"),
            ApiResponse(responseCode = "400", description = "the provided changed object is not valid"),
            ApiResponse(responseCode = "401", description = "not authorized to edit without providing credentials")
        ]
    )
    fun putObject(@RequestBody tibiaObject: TibiaObject, @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?): ResponseEntity<WikiObject> {
        return WikiResourceResponses.modified(modifyAny.modify(tibiaObject, editSummary))
    }
}
