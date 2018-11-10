package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveBooks;
import io.swagger.annotations.Api;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Books")
@Path("/")
public class BooksResource {

    @Autowired
    private RetrieveBooks retrieveBooks;

    private BooksResource() {
        // nothing to do, all dependencies are injected
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
