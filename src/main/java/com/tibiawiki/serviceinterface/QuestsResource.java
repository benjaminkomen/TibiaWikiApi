package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Quest;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveQuests;
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
@Api(value = "Quests")
@Path("/quests")
public class QuestsResource {

    private RetrieveQuests retrieveQuests;
    private ModifyAny modifyAny;

    @Autowired
    private QuestsResource(RetrieveQuests retrieveQuests, ModifyAny modifyAny) {
        this.retrieveQuests = retrieveQuests;
        this.modifyAny = modifyAny;
    }

    @GET
    @ApiOperation(value = "Get a list of quests")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of quests retrieved")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuests(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the quest names but the full quests", required = false)
                              @QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveQuests.getQuestsJSON().map(JSONObject::toMap)
                        : retrieveQuests.getQuestsList()
                )
                .build();
    }

    @GET
    @Path("/{name}")
    @ApiOperation(value = "Get a specific quest by name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestsByName(@PathParam("name") String name) {
        return retrieveQuests.getQuestJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiOperation(value = "Modify a quest")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed quest"),
            @ApiResponse(code = 400, message = "the provided changed quest is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putQuest(Quest quest, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(quest, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
