package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveQuests;
import io.swagger.annotations.Api;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Quests")
@Path("/")
public class QuestsResource {

    @Autowired
    private RetrieveQuests retrieveQuests;

    private QuestsResource() {
        // nothing to do, all dependencies are injected
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
