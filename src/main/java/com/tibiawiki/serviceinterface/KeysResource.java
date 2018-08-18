package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveKeys;
import io.swagger.annotations.Api;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Keys")
@Path("/")
public class KeysResource {

    private RetrieveKeys retrieveKeys;

    public KeysResource() {
        retrieveKeys = new RetrieveKeys();
    }

    @GET
    @Path("/keys")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKeys(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveKeys.getKeysJSON().map(JSONObject::toMap)
                        : retrieveKeys.getKeysList()
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
