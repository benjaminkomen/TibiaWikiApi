package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Outfit;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveOutfits;
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
@Api(value = "Outfits")
@RequestMapping("/outfits")
@RequiredArgsConstructor
public class OutfitsResource {

    private final RetrieveOutfits retrieveOutfits;
    private final ModifyAny modifyAny;

    @GetMapping(value = "")
    @ApiOperation(value = "Get a list of outfits")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of outfits retrieved")
    })
    public ResponseEntity<Object> getOutfits(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the outfit names but the full outfits", required = false)
                                             @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveOutfits.getOutfitsJSON().map(JSONObject::toMap)
                        : retrieveOutfits.getOutfitsList()
                );
    }

    @GetMapping("/{name}")
    @ApiOperation(value = "Get a specific outfit by name")
    public ResponseEntity<String> getOutfitsByName(@PathVariable("name") String name) {
        return retrieveOutfits.getOutfitJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "")
    @ApiOperation(value = "Modify an outfit")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed outfit"),
            @ApiResponse(code = 400, message = "the provided changed outfit is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putOutfit(@RequestBody Outfit outfit, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(outfit, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
