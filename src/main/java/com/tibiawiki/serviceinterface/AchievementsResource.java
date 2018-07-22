package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveAchievements;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class AchievementsResource {

    private RetrieveAchievements retrieveAchievements;

    public AchievementsResource() {
        retrieveAchievements = new RetrieveAchievements();
    }

    @GET
    @Path("/achievements")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAchievements(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveAchievements.getAchievementsJSON().map(JSONObject::toMap)
                        : retrieveAchievements.getAchievementsList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/achievements/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAchievementsByName(@PathParam("name") String name) {
        return retrieveAchievements.getAchievementJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
