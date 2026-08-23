package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.Key
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveKeys
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

@Tag(name = "Keys")
@RestController
@RequestMapping("/api/keys")
class KeysController(
    private val retrieveKeys: RetrieveKeys,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of keys")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of keys retrieved")])
    fun getKeys(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the key names but the full keys",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiResourceResponses.list(expand, { retrieveKeys.keysJSON }, { retrieveKeys.keysList })
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific key by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "key with specified name found"),
            ApiResponse(responseCode = "404", description = "key with specified name not found")
        ]
    )
    fun getKeysByName(@PathVariable("name") name: String): ResponseEntity<String> {
        return WikiResourceResponses.jsonOrNotFound(retrieveKeys.getKeyJSON(name))
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify a key")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed key"),
            ApiResponse(responseCode = "400", description = "the provided changed key is not valid"),
            ApiResponse(responseCode = "401", description = "not authorized to edit without providing credentials")
        ]
    )
    fun putKey(@RequestBody key: Key, @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?): ResponseEntity<WikiObject> {
        return WikiResourceResponses.modify(modifyAny.modify(key, editSummary))
    }
}
