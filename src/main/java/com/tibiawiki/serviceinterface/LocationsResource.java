package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveLocations;
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
@Api(value = "Locations")
@Path("/")
public class LocationsResource {

    private RetrieveLocations retrieveLocations;

    @Autowired
    private LocationsResource(RetrieveLocations retrieveLocations) {
        this.retrieveLocations = retrieveLocations;
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
