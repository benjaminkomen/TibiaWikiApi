package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveCorpses;
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
@Api(value = "Corpses")
@Path("/")
public class CorpsesResource {

    private RetrieveCorpses retrieveCorpses;

    @Autowired
    private CorpsesResource(RetrieveCorpses retrieveCorpses) {
        this.retrieveCorpses = retrieveCorpses;
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
