package com.tibiawiki.controller;

import com.tibiawiki.process.RetrieveLoot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Loot Statistics")
@RequestMapping("/api/loot")
@RequiredArgsConstructor
public class LootStatisticsResource {

    private final RetrieveLoot retrieveLoot;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a list of loot statistics")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200" , description = "list of loot statistics retrieved")
    })
    public ResponseEntity<Object> getLoot(@Parameter(description = "optionally expands the result to retrieve not only " +
            "the loot statistics page names but the full loot statistics", required = false)
                                          @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveLoot.getLootJSONObject().map(JSONObject::toMap)
                        : retrieveLoot.getLootList()
                );
    }

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a specific loot statistics page by creature name")
    public ResponseEntity<String> getLootByName(@PathVariable("name") String name) {
        return retrieveLoot.getLootJSONObject("Loot_Statistics:" + name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
