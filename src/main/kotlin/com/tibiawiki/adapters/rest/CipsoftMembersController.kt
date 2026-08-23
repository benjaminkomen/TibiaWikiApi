package com.tibiawiki.adapters.rest

import com.tibiawiki.config.WikiWriteApiDocs
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.objects.CipsoftMember
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.RetrieveByTemplate
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

@Tag(name = "CipSoft Members")
@RestController
@RequestMapping("/api/cipsoftmembers")
class CipsoftMembersController(
    private val retrieveByTemplate: RetrieveByTemplate,
    private val modifyAny: ModifyAny
) {

    @GetMapping(value = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a list of CipSoft members")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "list of CipSoft members retrieved")])
    fun getCipsoftMembers(
        @Parameter(
            description = "optionally expands the result to retrieve not only " +
                "the CipSoft member names but the full CipSoft members",
            required = false
        )
        @RequestParam(value = "expand", required = false) expand: Boolean?
    ): ResponseEntity<Any> {
        return WikiResourceResponses.list(
            expand,
            { retrieveByTemplate.asJson(InfoboxTemplate.CIPSOFT_MEMBER) },
            { retrieveByTemplate.names(InfoboxTemplate.CIPSOFT_MEMBER) }
        )
    }

    @GetMapping(value = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific CipSoft member by name")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "CipSoft member with specified name found"),
            ApiResponse(responseCode = "404", description = "CipSoft member with specified name not found")
        ]
    )
    fun getCipsoftMembersByName(@PathVariable("name") name: String): ResponseEntity<String> {
        return WikiResourceResponses.jsonOrNotFound(retrieveByTemplate.getJson(name))
    }

    @PutMapping(value = [""], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Modify a CipSoft member")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "the changed CipSoft member"),
            ApiResponse(responseCode = "400", description = "the provided changed CipSoft member is not valid"),
            ApiResponse(responseCode = "401", description = WikiWriteApiDocs.UNAUTHORIZED),
            ApiResponse(responseCode = "403", description = WikiWriteApiDocs.FORBIDDEN)
        ]
    )
    fun putCipsoftMember(
        @RequestBody cipsoftMember: CipsoftMember,
        @RequestHeader("X-WIKI-Edit-Summary") editSummary: String?
    ): ResponseEntity<WikiObject> {
        return WikiResourceResponses.modify(modifyAny.modify(cipsoftMember, editSummary))
    }
}
