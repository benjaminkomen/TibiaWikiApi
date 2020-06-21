package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Missile;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveMissiles;
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
@Api(value = "Missiles")
@RequestMapping("/missiles")
@RequiredArgsConstructor
public class MissilesResource {

    private final RetrieveMissiles retrieveMissiles;
    private final ModifyAny modifyAny;

    @GetMapping(value = "")
    @ApiOperation(value = "Get a list of missiles")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of missiles retrieved")
    })
    public ResponseEntity<Object> getMissiles(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the missile names but the full missiles", required = false)
                                              @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveMissiles.getMissilesJSON().map(JSONObject::toMap)
                        : retrieveMissiles.getMissilesList()
                );
    }

    @GetMapping("/{name}")
    @ApiOperation(value = "Get a specific missile by name")
    public ResponseEntity<String> getMissilesByName(@PathVariable("name") String name) {
        return retrieveMissiles.getMissileJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "")
    @ApiOperation(value = "Modify a missile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed missile"),
            @ApiResponse(code = 400, message = "the provided changed missile is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putMissile(@RequestBody Missile missile, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(missile, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
