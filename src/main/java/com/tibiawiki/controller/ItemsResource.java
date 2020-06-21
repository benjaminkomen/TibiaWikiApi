package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Item;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveItems;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
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
@Api(value = "Items")
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemsResource {

    private final RetrieveItems retrieveItems;
    private final ModifyAny modifyAny;

    @GetMapping(value = "")
    @ApiOperation(value = "Get a list of items")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of items retrieved")
    })
    public ResponseEntity<Object> getItems(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the item names but the full items", required = false)
                                           @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveItems.getItemsJSON().map(JSONObject::toMap)
                        : retrieveItems.getItemsList()
                );
    }

    @GetMapping("/{name}")
    @ApiOperation(value = "Get a specific item by name")
    public ResponseEntity<String> getItemsByName(@PathVariable("name") String name) {
        return retrieveItems.getItemJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "")
    @ApiOperation(value = "Modify an item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed item"),
            @ApiResponse(code = 400, message = "the provided changed item is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putItem(@RequestBody Item item, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(item, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
