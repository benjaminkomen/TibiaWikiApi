package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveMounts;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class MountsResource {

    private RetrieveMounts retrieveMounts;

    public MountsResource() {
        retrieveMounts = new RetrieveMounts();
    }

    @GET
    @Path("/mounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMounts() {
        return Response.ok()
                .entity(retrieveMounts.getMountsJSON()
                        .map(JSONObject::toMap)
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/mounts/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMountsByName(@PathParam("name") String name) {
        return retrieveMounts.getMountJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
