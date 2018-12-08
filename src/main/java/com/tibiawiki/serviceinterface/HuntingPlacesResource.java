package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveHuntingPlaces;
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
@Api(value = "Hunting Places")
@Path("/")
public class HuntingPlacesResource {

    private RetrieveHuntingPlaces retrieveHuntingPlaces;

    @Autowired
    private HuntingPlacesResource(RetrieveHuntingPlaces retrieveHuntingPlaces) {
        this.retrieveHuntingPlaces = retrieveHuntingPlaces;
    }

    @GET
    @Path("/huntingplaces")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHuntingPlaces(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveHuntingPlaces.getHuntingPlacesJSON().map(JSONObject::toMap)
                        : retrieveHuntingPlaces.getHuntingPlacesList()
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
