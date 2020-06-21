package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Effect;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveEffects;
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
@Api(value = "Effects")
@RequestMapping("/effects")
@RequiredArgsConstructor
public class EffectsResource {

    private final RetrieveEffects retrieveEffects;
    private final ModifyAny modifyAny;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a list of effects")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of effects retrieved")
    })
    public ResponseEntity<Object> getEffects(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the effect names but the full effects", required = false)
                                             @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveEffects.getEffectsJSON().map(JSONObject::toMap)
                        : retrieveEffects.getEffectsList()
                );
    }

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a specific effect by name")
    public ResponseEntity<String> getEffectsByName(@PathVariable("name") String name) {
        return retrieveEffects.getEffectJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modify an effect")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed effect"),
            @ApiResponse(code = 400, message = "the provided changed effect is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putEffect(@RequestBody Effect effect, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(effect, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
