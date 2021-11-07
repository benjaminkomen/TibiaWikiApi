package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.TibiaObject;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveItems;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Items")
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemsResource {

    private final RetrieveItems retrieveItems;
    private final ModifyAny modifyAny;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a list of items")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200" , description = "list of items retrieved")
    })
    public ResponseEntity<Object> getItems(@Parameter(description = "optionally expands the result to retrieve not only " +
            "the item names but the full items")
                                           @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveItems.getItemsJSON().map(JSONObject::toMap)
                        : retrieveItems.getItemsList()
                );
    }

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a specific item by name")
    public ResponseEntity<String> getItemsByName(@PathVariable("name") String name) {
        return retrieveItems.getItemJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Modify an item")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200" , description = "the changed item"),
           @ApiResponse(responseCode = "400" , description = "the provided changed item is not valid"),
           @ApiResponse(responseCode = "401" , description = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putItem(@RequestBody TibiaObject item, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(item, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
