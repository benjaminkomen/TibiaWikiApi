package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Building;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveBuildings;
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
@Api(value = "Buildings")
@Path("/buildings")
public class BuildingsResource {

    private RetrieveBuildings retrieveBuildings;
    private ModifyAny modifyAny;

    @Autowired
    private BuildingsResource(RetrieveBuildings retrieveBuildings, ModifyAny modifyAny) {
        this.retrieveBuildings = retrieveBuildings;
        this.modifyAny = modifyAny;
    }

    @GET
    @ApiOperation(value = "Get a list of buildings")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of buildings retrieved")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuildings(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the building names but the full buildings", required = false)
                                 @QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveBuildings.getBuildingsJSON().map(JSONObject::toMap)
                        : retrieveBuildings.getBuildingsList()
                )
                .build();
    }

    @GET
    @Path("/{name}")
    @ApiOperation(value = "Get a specific building by name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuildingsByName(@PathParam("name") String name) {
        return retrieveBuildings.getBuildingJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiOperation(value = "Modify a building")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed building"),
            @ApiResponse(code = 400, message = "the provided changed building is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putBuilding(Building building, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(building, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
