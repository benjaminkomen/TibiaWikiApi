package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveBooks;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class BooksResource {

    private RetrieveBooks retrieveBooks;

    public BooksResource() {
        retrieveBooks = new RetrieveBooks();
    }

    @GET
    @Path("/books")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooks(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveBooks.getBooksJSON().map(JSONObject::toMap)
                        : retrieveBooks.getBooksList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/books/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksByName(@PathParam("name") String name) {
        return retrieveBooks.getBookJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
