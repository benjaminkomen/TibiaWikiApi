package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveLocations;
import io.swagger.annotations.Api;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Locations")
@Path("/")
public class LocationsResource {

    @Autowired
    private RetrieveLocations retrieveLocations;

    private LocationsResource() {
        // nothing to do, all dependencies are injected
    }

    @GET
    @Path("/locations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocations(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveLocations.getLocationsJSON().map(JSONObject::toMap)
                        : retrieveLocations.getLocationsList()
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
