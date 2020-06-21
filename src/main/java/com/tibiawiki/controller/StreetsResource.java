package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Street;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveStreets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(value = "Streets")
@RequestMapping("/streets")
@RequiredArgsConstructor
public class StreetsResource {

    private final RetrieveStreets retrieveStreets;
    private final ModifyAny modifyAny;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a list of streets")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of streets retrieved")
    })
    public ResponseEntity<Object> getStreets(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the street names but the full streets", required = false)
                                             @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveStreets.getStreetsJSON().map(JSONObject::toMap)
                        : retrieveStreets.getStreetsList()
                );
    }

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a specific street by name")
    public ResponseEntity<String> getStreetsByName(@PathVariable("name") String name) {
        return retrieveStreets.getStreetJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modify a street")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed street"),
            @ApiResponse(code = 400, message = "the provided changed street is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putStreet(@RequestBody Street street, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(street, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
