package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveQuests;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class QuestsResource {

    private RetrieveQuests retrieveQuests;

    public QuestsResource() {
        retrieveQuests = new RetrieveQuests();
    }

    @GET
    @Path("/quests")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuests(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveQuests.getQuestsJSON().map(JSONObject::toMap)
                        : retrieveQuests.getQuestsList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/quests/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestsByName(@PathParam("name") String name) {
        return retrieveQuests.getQuestJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
