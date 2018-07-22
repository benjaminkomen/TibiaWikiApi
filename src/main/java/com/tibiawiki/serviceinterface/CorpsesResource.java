package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveCorpses;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class CorpsesResource {

    private RetrieveCorpses retrieveCorpses;

    public CorpsesResource() {
        retrieveCorpses = new RetrieveCorpses();
    }

    @GET
    @Path("/corpses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCorpses(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveCorpses.getCorpsesJSON().map(JSONObject::toMap)
                        : retrieveCorpses.getCorpsesList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/corpses/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCorpsesByName(@PathParam("name") String name) {
        return retrieveCorpses.getCorpseJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
