package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveKeys;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class KeysResource {

    private RetrieveKeys retrieveKeys;

    public KeysResource() {
        retrieveKeys = new RetrieveKeys();
    }

    @GET
    @Path("/keys")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKeys() {
        return Response.ok()
                .entity(retrieveKeys.getKeysJSON()
                        .map(JSONObject::toMap)
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/keys/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKeysByName(@PathParam("name") String name) {
        return retrieveKeys.getKeyJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
