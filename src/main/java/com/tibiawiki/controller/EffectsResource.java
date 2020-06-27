package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Effect;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveEffects;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Effects")
@RequestMapping("/api/effects")
@RequiredArgsConstructor
public class EffectsResource {

    private final RetrieveEffects retrieveEffects;
    private final ModifyAny modifyAny;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a list of effects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of effects retrieved")
    })
    public ResponseEntity<Object> getEffects(@Parameter(description = "optionally expands the result to retrieve not only " +
            "the effect names but the full effects", required = false)
                                             @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveEffects.getEffectsJSON().map(JSONObject::toMap)
                        : retrieveEffects.getEffectsList()
                );
    }

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a specific effect by name")
    public ResponseEntity<String> getEffectsByName(@PathVariable("name") String name) {
        return retrieveEffects.getEffectJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Modify an effect")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "the changed effect"),
            @ApiResponse(responseCode = "400", description = "the provided changed effect is not valid"),
            @ApiResponse(responseCode = "401", description = "not authorized to edit without providing credentials")
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
