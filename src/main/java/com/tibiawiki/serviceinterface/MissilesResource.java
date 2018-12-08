package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveMissiles;
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
@Api(value = "Missiles")
@Path("/")
public class MissilesResource {

    private RetrieveMissiles retrieveMissiles;

    @Autowired
    private MissilesResource(RetrieveMissiles retrieveMissiles) {
        this.retrieveMissiles = retrieveMissiles;
    }

    @GET
    @Path("/missiles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMissiles(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveMissiles.getMissilesJSON().map(JSONObject::toMap)
                        : retrieveMissiles.getMissilesList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/missiles/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMissilesByName(@PathParam("name") String name) {
        return retrieveMissiles.getMissileJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
