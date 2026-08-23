package com.tibiawiki.adapters.rest

import com.tibiawiki.config.WikiWriteApiDocs
import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.objects.Quest
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveQuests
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Quests")
@RestController
@RequestMapping("/api/quests")
class QuestsController(
    private val retrieveQuests: RetrieveQuests,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of quests")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of quests retrieved")])
    fun getQuests(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the quest names but the full quests",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiResourceResponses.list(expand, { retrieveQuests.questsJSON }, { retrieveQuests.questsList })
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific quest by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "quest with specified name found"),
            ApiResponse(responseCode = "404", description = "quest with specified name not found")
        ]
    )
    fun getQuestsByName(@PathVariable("name") name: String): ResponseEntity<WikiJson> {
        return WikiResourceResponses.jsonOrNotFound(retrieveQuests.getQuestJSON(name))
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify a quest")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed quest"),
            ApiResponse(responseCode = "400", description = "the provided changed quest is not valid"),
            ApiResponse(responseCode = "401", description = WikiWriteApiDocs.UNAUTHORIZED),
            ApiResponse(responseCode = "403", description = WikiWriteApiDocs.FORBIDDEN)
        ]
    )
    fun putQuest(@RequestBody quest: Quest, @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?): ResponseEntity<WikiObject> {
        return WikiResourceResponses.modify(modifyAny.modify(quest, editSummary))
    }
}
