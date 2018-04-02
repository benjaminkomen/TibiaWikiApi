package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveEffects;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class EffectsResource {

    private RetrieveEffects retrieveEffects;

    public EffectsResource() {
        retrieveEffects = new RetrieveEffects();
    }

    @GET
    @Path("/effects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEffects() {
        return Response.ok()
                .entity(retrieveEffects.getEffectsJSON()
                        .map(JSONObject::toMap)
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/effects/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEffectsByName(@PathParam("name") String name) {
        return retrieveEffects.getEffectJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
