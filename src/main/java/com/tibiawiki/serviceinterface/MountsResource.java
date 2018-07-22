package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveMounts;
import org.json.JSONObject;

import javax.ws.rs.*;
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
    public Response getMounts(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveMounts.getMountsJSON().map(JSONObject::toMap)
                        : retrieveMounts.getMountsList()
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
