package com.tibiawiki.controller;

import com.tibiawiki.process.RetrieveLoot;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "Loot Statistics")
@RequestMapping("/loot")
@RequiredArgsConstructor
public class LootStatisticsResource {

    private final RetrieveLoot retrieveLoot;

    @GetMapping(value = "")
    @ApiOperation(value = "Get a list of loot statistics")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of loot statistics retrieved")
    })
    public ResponseEntity<Object> getLoot(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the loot statistics page names but the full loot statistics", required = false)
                                          @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveLoot.getLootJSONObject().map(JSONObject::toMap)
                        : retrieveLoot.getLootList()
                );
    }

    @GetMapping("/{name}")
    @ApiOperation(value = "Get a specific loot statistics page by creature name")
    public ResponseEntity<String> getLootByName(@PathVariable("name") String name) {
        return retrieveLoot.getLootJSONObject("Loot_Statistics:" + name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
