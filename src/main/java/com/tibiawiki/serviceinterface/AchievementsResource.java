package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveAchievements;
import io.swagger.annotations.*;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Achievements")
@Path("/")
public class AchievementsResource {

    private RetrieveAchievements retrieveAchievements;

    public AchievementsResource() {
        retrieveAchievements = new RetrieveAchievements();
    }

    @GET
    @Path("/achievements")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of achievements retrieved")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAchievements(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the achievement names but the full achievements", required = false)
                                        @QueryParam("expand") Boolean expand) {
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
    @ApiOperation(value = "achievements")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "achievement with specified name found"),
            @ApiResponse(code = 404, message = "achievement with specified name not found")
    })
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
