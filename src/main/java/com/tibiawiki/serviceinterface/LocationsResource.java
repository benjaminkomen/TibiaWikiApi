package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveLocations;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class LocationsResource {

    private RetrieveLocations retrieveLocations;

    public LocationsResource() {
        retrieveLocations = new RetrieveLocations();
    }

    @GET
    @Path("/locations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocations() {
        return Response.ok()
                .entity(retrieveLocations.getLocationsJSON()
                        .map(JSONObject::toMap)
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/locations/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocationsByName(@PathParam("name") String name) {
        return retrieveLocations.getLocationJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
