package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveObjects;
import io.swagger.annotations.Api;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Objects")
@Path("/")
public class ObjectsResource {

    @Autowired
    private RetrieveObjects retrieveObjects;

    private ObjectsResource() {
        // nothing to do, all dependencies are injected
    }

    @GET
    @Path("/objects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getObjects(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveObjects.getObjectsJSON().map(JSONObject::toMap)
                        : retrieveObjects.getObjectsList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/objects/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getObjectsByName(@PathParam("name") String name) {
        return retrieveObjects.getObjectJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
