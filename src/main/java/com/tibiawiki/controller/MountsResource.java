package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Mount;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveMounts;
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
@Api(value = "Mounts")
@RequestMapping("/mounts")
@RequiredArgsConstructor
public class MountsResource {

    private final RetrieveMounts retrieveMounts;
    private final ModifyAny modifyAny;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a list of mounts")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of mounts retrieved")
    })
    public ResponseEntity<Object> getMounts(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the mount names but the full mounts", required = false)
                                            @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveMounts.getMountsJSON().map(JSONObject::toMap)
                        : retrieveMounts.getMountsList()
                );
    }

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a specific mount by name")
    public ResponseEntity<String> getMountsByName(@PathVariable("name") String name) {
        return retrieveMounts.getMountJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modify a mount")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed mount"),
            @ApiResponse(code = 400, message = "the provided changed mount is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putMount(@RequestBody Mount mount, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(mount, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
