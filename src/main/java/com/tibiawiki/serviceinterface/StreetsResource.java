package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveStreets;
import io.swagger.annotations.Api;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Streets")
@Path("/")
public class StreetsResource {

    private RetrieveStreets retrieveStreets;

    public StreetsResource() {
        retrieveStreets = new RetrieveStreets();
    }

    @GET
    @Path("/streets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStreets(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveStreets.getStreetsJSON().map(JSONObject::toMap)
                        : retrieveStreets.getStreetsList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/streets/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStreetsByName(@PathParam("name") String name) {
        return retrieveStreets.getStreetJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
