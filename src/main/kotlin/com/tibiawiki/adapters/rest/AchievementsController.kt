package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.Achievement
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.validation.ValidationException
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveAchievements
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.json.JSONObject
import org.springframework.http.HttpStatus
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

@Tag(name = "Achievements")
@RestController
@RequestMapping("/api/achievements")
class AchievementsController(
    private val retrieveAchievements: RetrieveAchievements,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of achievements")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of achievements retrieved")])
    fun getAchievements(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the achievement names but the full achievements",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return ResponseEntity.ok()
            .body(
                if (expand != null && expand) {
                    retrieveAchievements.achievementsJSON.map(JSONObject::toMap)
                } else {
                    retrieveAchievements.achievementsList
                }
            )
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific achievement by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "achievement with specified name found"),
            ApiResponse(responseCode = "404", description = "achievement with specified name not found")
        ]
    )
    fun getAchievementsByName(@PathVariable("name") name: String): ResponseEntity<String> {
        return retrieveAchievements.getAchievementJSON(name)
            ?.let { a: JSONObject ->
                ResponseEntity.ok()
                    .body(a.toString(2))
            }
            ?: ResponseEntity.notFound().build()
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify an achievement")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed achievement"),
            ApiResponse(responseCode = "400", description = "the provided changed achievement is not valid"),
            ApiResponse(responseCode = "401", description = "not authorized to edit without providing credentials")
        ]
    )
    fun putAchievement(@RequestBody achievement: Achievement, @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?): ResponseEntity<WikiObject> {
        return modifyAny.modify(achievement, editSummary)
            .map { a: WikiObject ->
                ResponseEntity.ok()
                    .body(a)
            }
            .recover(ValidationException::class.java) { ResponseEntity.badRequest().build() }
//                .recover(ValidationException.class, e -> ResponseEntity.badRequest().body(e.getMessage()))
            .recover { ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() }
            .get()
    }
}
