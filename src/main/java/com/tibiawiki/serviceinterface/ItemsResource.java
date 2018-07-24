package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveItems;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class ItemsResource {

    private RetrieveItems retrieveItems;

    public ItemsResource() {
        retrieveItems = new RetrieveItems();
    }

    @GET
    @Path("/items")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveItems.getItemsJSON().map(JSONObject::toMap)
                        : retrieveItems.getItemsList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/items/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemsByName(@PathParam("name") String name) {
        return retrieveItems.getItemJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
