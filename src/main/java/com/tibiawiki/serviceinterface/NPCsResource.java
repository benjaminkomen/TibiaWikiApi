package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveNPCs;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class NPCsResource {

    private RetrieveNPCs retrieveNPCs;

    public NPCsResource() {
        retrieveNPCs = new RetrieveNPCs();
    }

    @GET
    @Path("/npcs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNPCs(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveNPCs.getNPCsJSON().map(JSONObject::toMap)
                        : retrieveNPCs.getNPCsList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/npcs/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNPCsByName(@PathParam("name") String name) {
        return retrieveNPCs.getNPCJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
