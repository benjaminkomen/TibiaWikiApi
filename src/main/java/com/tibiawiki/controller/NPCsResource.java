package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.NPC;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveNPCs;
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
@Api(value = "NPCs")
@RequestMapping("/npcs")
@RequiredArgsConstructor
public class NPCsResource {

    private final RetrieveNPCs retrieveNPCs;
    private final ModifyAny modifyAny;

    @GetMapping(value = "")
    @ApiOperation(value = "Get a list of NPCs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of NPCs retrieved")
    })
    public ResponseEntity<Object> getNPCs(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the NPC names but the full NPCs", required = false)
                                          @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveNPCs.getNPCsJSON().map(JSONObject::toMap)
                        : retrieveNPCs.getNPCsList()
                );
    }

    @GetMapping("/{name}")
    @ApiOperation(value = "Get a specific NPC by name")
    public ResponseEntity<String> getNPCsByName(@PathVariable("name") String name) {
        return retrieveNPCs.getNPCJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "")
    @ApiOperation(value = "Modify a NPC")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed npc"),
            @ApiResponse(code = 400, message = "the provided changed npc is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putNPC(@RequestBody NPC npc, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(npc, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
