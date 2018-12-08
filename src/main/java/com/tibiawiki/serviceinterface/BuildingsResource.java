package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveBuildings;
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
@Api(value = "Buildings")
@Path("/")
public class BuildingsResource {

    private RetrieveBuildings retrieveBuildings;

    @Autowired
    private BuildingsResource(RetrieveBuildings retrieveBuildings) {
        this.retrieveBuildings = retrieveBuildings;
    }

    @GET
    @Path("/buildings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuildings(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveBuildings.getBuildingsJSON().map(JSONObject::toMap)
                        : retrieveBuildings.getBuildingsList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/buildings/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuildingsByName(@PathParam("name") String name) {
        return retrieveBuildings.getBuildingJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
