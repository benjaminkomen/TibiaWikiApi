package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Quest;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveQuests;
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
@Api(value = "Quests")
@RequestMapping("/quests")
@RequiredArgsConstructor
public class QuestsResource {

    private final RetrieveQuests retrieveQuests;
    private final ModifyAny modifyAny;

    @GetMapping(value = "")
    @ApiOperation(value = "Get a list of quests")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of quests retrieved")
    })
    public ResponseEntity<Object> getQuests(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the quest names but the full quests", required = false)
                                            @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveQuests.getQuestsJSON().map(JSONObject::toMap)
                        : retrieveQuests.getQuestsList()
                );
    }

    @GetMapping("/{name}")
    @ApiOperation(value = "Get a specific quest by name")
    public ResponseEntity<String> getQuestsByName(@PathVariable("name") String name) {
        return retrieveQuests.getQuestJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "")
    @ApiOperation(value = "Modify a quest")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed quest"),
            @ApiResponse(code = 400, message = "the provided changed quest is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putQuest(@RequestBody Quest quest, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(quest, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
