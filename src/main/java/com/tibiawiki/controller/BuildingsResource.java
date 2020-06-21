package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Building;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveBuildings;
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
@Api(value = "Buildings")
@RequestMapping("/buildings")
@RequiredArgsConstructor
public class BuildingsResource {

    private final RetrieveBuildings retrieveBuildings;
    private final ModifyAny modifyAny;

    @GetMapping(value = "")
    @ApiOperation(value = "Get a list of buildings")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of buildings retrieved")
    })
    public ResponseEntity<Object> getBuildings(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the building names but the full buildings", required = false)
                                               @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveBuildings.getBuildingsJSON().map(JSONObject::toMap)
                        : retrieveBuildings.getBuildingsList()
                );
    }

    @GetMapping("/{name}")
    @ApiOperation(value = "Get a specific building by name")
    public ResponseEntity<String> getBuildingsByName(@PathVariable("name") String name) {
        return retrieveBuildings.getBuildingJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "")
    @ApiOperation(value = "Modify a building")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed building"),
            @ApiResponse(code = 400, message = "the provided changed building is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putBuilding(@RequestBody Building building, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(building, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
