package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Spell;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveSpells;
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
@Api(value = "Spells")
@RequestMapping("/spells")
@RequiredArgsConstructor
public class SpellsResource {

    private final RetrieveSpells retrieveSpells;
    private final ModifyAny modifyAny;

    @GetMapping(value = "")
    @ApiOperation(value = "Get a list of spells")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of spells retrieved")
    })
    public ResponseEntity<Object> getSpells(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the spell names but the full spells", required = false)
                                            @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveSpells.getSpellsJSON().map(JSONObject::toMap)
                        : retrieveSpells.getSpellsList()
                );
    }

    @GetMapping("/{name}")
    @ApiOperation(value = "Get a specific spell by name")
    public ResponseEntity<String> getSpellsByName(@PathVariable("name") String name) {
        return retrieveSpells.getSpellJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "")
    @ApiOperation(value = "Modify a spell")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed spell"),
            @ApiResponse(code = 400, message = "the provided changed spell is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putSpell(@RequestBody Spell spell, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(spell, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
