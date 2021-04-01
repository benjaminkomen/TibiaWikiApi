package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.RetrieveWikiPages
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "WikiPages")
@RestController
@RequestMapping("/api/pages")
class WikiPageController(
    private val retrieveWikiPages: RetrieveWikiPages,
) {

    @GetMapping(value = ["/{title}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get a specific wiki page by title")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "wiki page with specified title found"),
            ApiResponse(responseCode = "404", description = "wiki page with specified title not found," +
                    " or not parsable to one of the supported domain models.")
        ]
    )
    fun getWikiPageByTitle(@PathVariable("title") title: String): ResponseEntity<String> {
        return retrieveWikiPages.getWikiPageJSON(title)
            ?.takeIf { it.isEmpty.not() }
            ?.let { ResponseEntity.ok().body(it.toString(2)) }
            ?: ResponseEntity.notFound().build()
    }
}
