package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.TibiaObject
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveItems
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

@Tag(name = "Items")
@RestController
@RequestMapping("/api/items")
class ItemsController(
    private val retrieveItems: RetrieveItems,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of items")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of items retrieved")])
    fun getItems(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the item names but the full items",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return ResponseEntity.ok()
            .body(
                if (expand != null && expand) {
                    retrieveItems.itemsJSON.map<Any>(JSONObject::toMap)
                } else {
                    retrieveItems.itemsList
                }
            )
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific item by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "item with specified name found"),
            ApiResponse(responseCode = "404", description = "item with specified name not found")
        ]
    )
    fun getItemsByName(@PathVariable("name") name: String): ResponseEntity<String> {
        return retrieveItems.getItemJSON(name)
            .map { a: JSONObject ->
                ResponseEntity.ok()
                    .body(a.toString(2))
            }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify an item")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed item"),
            ApiResponse(responseCode = "400", description = "the provided changed item is not valid"),
            ApiResponse(responseCode = "401", description = "not authorized to edit without providing credentials")
        ]
    )
    fun putItem(@RequestBody item: TibiaObject, @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?): ResponseEntity<WikiObject> {
        return modifyAny.modify(item, editSummary)
            .map { a: WikiObject ->
                ResponseEntity.ok()
                    .body(a)
            }
            .recover<ValidationException>(ValidationException::class.java) { ResponseEntity.badRequest().build() }
            .recover { ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() }
            .get()
    }
}
