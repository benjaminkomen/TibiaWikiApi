package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Achievement;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveAchievements;
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

@Api(value = "Achievements")
@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
 public class AchievementsResource {

    private final RetrieveAchievements retrieveAchievements;
    private final ModifyAny modifyAny;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a list of achievements")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of achievements retrieved")
    })
    public ResponseEntity<Object> getAchievements(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the achievement names but the full achievements", required = false)
                                                  @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveAchievements.getAchievementsJSON().map(JSONObject::toMap)
                        : retrieveAchievements.getAchievementsList()
                );
    }

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a specific achievement by name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "achievement with specified name found"),
            @ApiResponse(code = 404, message = "achievement with specified name not found")
    })
    public ResponseEntity<String> getAchievementsByName(@PathVariable("name") String name) {
        return retrieveAchievements.getAchievementJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2))
                )
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modify an achievement")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed achievement"),
            @ApiResponse(code = 400, message = "the provided changed achievement is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials") // TODO this is not implemented yet
    })
    public ResponseEntity<WikiObject> putAchievement(@RequestBody Achievement achievement, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(achievement, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
//                .recover(ValidationException.class, e -> ResponseEntity.badRequest().body(e.getMessage()))
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
