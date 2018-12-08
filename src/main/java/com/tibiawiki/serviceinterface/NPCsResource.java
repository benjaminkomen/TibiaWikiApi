package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveNPCs;
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
@Api(value = "NPCs")
@Path("/")
public class NPCsResource {

    private RetrieveNPCs retrieveNPCs;

    @Autowired
    private NPCsResource(RetrieveNPCs retrieveNPCs) {
        this.retrieveNPCs = retrieveNPCs;
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
