package com.tibiawiki.controller;

import com.tibiawiki.domain.objects.Book;
import com.tibiawiki.domain.objects.WikiObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveBooks;
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
@Api(value = "Books")
@RequestMapping("/books")
@RequiredArgsConstructor
 public class BooksResource {

    private final RetrieveBooks retrieveBooks;
    private final ModifyAny modifyAny;

    @GetMapping(value = "")
    @ApiOperation(value = "Get a list of books")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of books retrieved")
    })
    public ResponseEntity<Object> getBooks(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the book names but the full books", required = false)
                                           @RequestParam(value = "expand", required = false) Boolean expand) {
        return ResponseEntity.ok()
                .body(expand != null && expand
                        ? retrieveBooks.getBooksJSON().map(JSONObject::toMap)
                        : retrieveBooks.getBooksList()
                );
    }

    @GetMapping("/{name}")
    @ApiOperation(value = "Get a specific book by name")
    public ResponseEntity<String> getBooksByName(@PathVariable("name") String name) {
        return retrieveBooks.getBookJSON(name)
                .map(a -> ResponseEntity.ok()
                        .body(a.toString(2)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "")
    @ApiOperation(value = "Modify a book")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed book"),
            @ApiResponse(code = 400, message = "the provided changed book is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    public ResponseEntity<WikiObject> putBook(@RequestBody Book book, @RequestHeader("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(book, editSummary)
                .map(a -> ResponseEntity.ok()
                        .body(a))
                .recover(ValidationException.class, e -> ResponseEntity.badRequest().build())
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                .get();
    }
}
