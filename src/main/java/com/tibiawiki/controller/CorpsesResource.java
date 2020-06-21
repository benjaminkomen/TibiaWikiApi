package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Corpse;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveCorpses;
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
@Api(value = "Corpses")
@RequestMapping("/corpses")
@RequiredArgsConstructor
public class CorpsesResource {

    private final RetrieveCorpses retrieveCorpses;
    private final ModifyAny modifyAny;

    @GetMapping(value = "")
    @ApiOperation(value = "Get a list of corpses")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of corpses retrieved")
    })
    public ResponseEntity<Object> getCorpses(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the corpse names but the full corpses", required = false)
                                             @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveCorpses.getCorpsesJSON().map(JSONObject::toMap)
                        : retrieveCorpses.getCorpsesList()
                );
    }

    @GetMapping("/{name}")
    @ApiOperation(value = "Get a specific corpse by name")
    public ResponseEntity<String> getCorpsesByName(@PathVariable("name") String name) {
        return retrieveCorpses.getCorpseJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "")
    @ApiOperation(value = "Modify a corpse")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed corpse"),
            @ApiResponse(code = 400, message = "the provided changed corpse is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putCorpse(@RequestBody Corpse corpse, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(corpse, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
