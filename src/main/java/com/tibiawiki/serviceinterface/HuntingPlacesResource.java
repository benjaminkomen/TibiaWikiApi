package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveHuntingPlaces;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class HuntingPlacesResource {

    private RetrieveHuntingPlaces retrieveHuntingPlaces;

    public HuntingPlacesResource() {
        retrieveHuntingPlaces = new RetrieveHuntingPlaces();
    }

    @GET
    @Path("/huntingplaces")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHuntingPlaces() {
        return Response.ok()
                .entity(retrieveHuntingPlaces.getHuntingPlacesJSON()
                        .map(JSONObject::toMap)
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/huntingplaces/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHuntingPlacesByName(@PathParam("name") String name) {
        return retrieveHuntingPlaces.getHuntingPlaceJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
