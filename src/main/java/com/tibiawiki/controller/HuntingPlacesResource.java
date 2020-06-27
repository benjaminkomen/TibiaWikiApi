package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.HuntingPlace;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveHuntingPlaces;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@Tag(name = "Hunting Places")
@RequestMapping("/api/huntingplaces")
@RequiredArgsConstructor
public class HuntingPlacesResource {

    private final RetrieveHuntingPlaces retrieveHuntingPlaces;
    private final ModifyAny modifyAny;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a list of hunting places")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200" , description = "list of hunting places retrieved")
    })
    public ResponseEntity<Object> getHuntingPlaces(@Parameter(description = "optionally expands the result to retrieve not only " +
            "the hunting place names, but the full hunting places", required = false)
                                                   @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveHuntingPlaces.getHuntingPlacesJSON().map(JSONObject::toMap)
                        : retrieveHuntingPlaces.getHuntingPlacesList()
                );
    }

    @GetMapping(value = "/**", produces = MediaType.APPLICATION_JSON_VALUE) // accept special characters such as slashes in path
    @Operation(summary = "Get a specific hunting place by name")
    public ResponseEntity<String> getHuntingPlacesByName(HttpServletRequest request) {
        var requestUri = request.getRequestURI();
        var name = URLDecoder.decode(requestUri.split("/huntingplaces/")[1], StandardCharsets.UTF_8);
        return retrieveHuntingPlaces.getHuntingPlaceJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Modify a hunting place")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200" , description = "the changed huntingPlace"),
           @ApiResponse(responseCode = "400" , description = "the provided changed huntingPlace is not valid"),
           @ApiResponse(responseCode = "401" , description = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putHuntingPlace(@RequestBody HuntingPlace huntingPlace, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(huntingPlace, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
