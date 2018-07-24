package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveObjects;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class ObjectsResource {

    private RetrieveObjects retrieveObjects;

    public ObjectsResource() {
        retrieveObjects = new RetrieveObjects();
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
