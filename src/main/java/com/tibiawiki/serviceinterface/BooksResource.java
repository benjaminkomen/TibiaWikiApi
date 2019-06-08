package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Book;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveBooks;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Books")
@Path("/books")
public class BooksResource {

    private RetrieveBooks retrieveBooks;
    private ModifyAny modifyAny;

    @Autowired
    private BooksResource(RetrieveBooks retrieveBooks, ModifyAny modifyAny) {
        this.retrieveBooks = retrieveBooks;
        this.modifyAny = modifyAny;
    }

    @GET
    @ApiOperation(value = "Get a list of books")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of books retrieved")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooks(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the book names but the full books", required = false)
                             @QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveBooks.getBooksJSON().map(JSONObject::toMap)
                        : retrieveBooks.getBooksList()
                )
                .build();
    }

    @GET
    @Path("/{name}")
    @ApiOperation(value = "Get a specific book by name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksByName(@PathParam("name") String name) {
        return retrieveBooks.getBookJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiOperation(value = "Modify a book")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed book"),
            @ApiResponse(code = 400, message = "the provided changed book is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putBook(Book book, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(book, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
