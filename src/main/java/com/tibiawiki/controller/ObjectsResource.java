package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.TibiaObject;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveObjects;
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
@Api(value = "Objects")
@RequestMapping("/objects")
@RequiredArgsConstructor
public class ObjectsResource {

    private final RetrieveObjects retrieveObjects;
    private final ModifyAny modifyAny;

    @GetMapping(value = "")
    @ApiOperation(value = "Get a list of objects")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of objects retrieved")
    })
    public ResponseEntity<Object> getObjects(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the object names but the full objects", required = false)
                                             @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveObjects.getObjectsJSON().map(JSONObject::toMap)
                        : retrieveObjects.getObjectsList()
                );
    }

    @GetMapping("/{name}")
    @ApiOperation(value = "Get a specific object by name")
    public ResponseEntity<String> getObjectsByName(@PathVariable("name") String name) {
        return retrieveObjects.getObjectJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "")
    @ApiOperation(value = "Modify an object")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed tibiaObject"),
            @ApiResponse(code = 400, message = "the provided changed tibiaObject is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putTibiaObject(@RequestBody TibiaObject tibiaObject, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(tibiaObject, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
