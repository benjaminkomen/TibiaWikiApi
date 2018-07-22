package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveNPCs;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    public Response getNPCs() {
        return Response.ok()
                .entity(retrieveNPCs.getNPCsJSON()
                        .map(JSONObject::toMap)
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
