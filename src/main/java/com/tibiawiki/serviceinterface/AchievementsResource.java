package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Achievement;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveAchievements;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Achievements")
@Path("/achievements")
public class AchievementsResource {

    private RetrieveAchievements retrieveAchievements;
    private ModifyAny modifyAny;

    @Autowired
    private AchievementsResource(RetrieveAchievements retrieveAchievements, ModifyAny modifyAny) {
        this.retrieveAchievements = retrieveAchievements;
        this.modifyAny = modifyAny;
    }

    @GET
    @ApiOperation(value = "Get a list of achievements")
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
                .build();
    }

    @GET
    @Path("/{name}")
    @ApiOperation(value = "Get a specific achievement by name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "achievement with specified name found"),
            @ApiResponse(code = 404, message = "achievement with specified name not found")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAchievementsByName(@PathParam("name") String name) {
        return retrieveAchievements.getAchievementJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiOperation(value = "Modify an achievement")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed achievement"),
            @ApiResponse(code = 400, message = "the provided changed achievement is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials") // TODO this is not implemented yet
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putAchievement(Achievement achievement, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(achievement, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
