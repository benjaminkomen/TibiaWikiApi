package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.HuntingPlace;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveHuntingPlaces;
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
@Api(value = "Hunting Places")
@Path("/huntingplaces")
public class HuntingPlacesResource {

    private RetrieveHuntingPlaces retrieveHuntingPlaces;
    private ModifyAny modifyAny;

    @Autowired
    private HuntingPlacesResource(RetrieveHuntingPlaces retrieveHuntingPlaces, ModifyAny modifyAny) {
        this.retrieveHuntingPlaces = retrieveHuntingPlaces;
        this.modifyAny = modifyAny;
    }

    @GET
    @ApiOperation(value = "Get a list of hunting places")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of hunting places retrieved")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHuntingPlaces(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the hunting place names but the full hunting places", required = false)
                                     @QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveHuntingPlaces.getHuntingPlacesJSON().map(JSONObject::toMap)
                        : retrieveHuntingPlaces.getHuntingPlacesList()
                )
                .build();
    }

    @GET
    @Path("/{name}")
    @ApiOperation(value = "Get a specific hunting place by name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHuntingPlacesByName(@PathParam("name") String name) {
        return retrieveHuntingPlaces.getHuntingPlaceJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiOperation(value = "Modify a hunting place")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed huntingPlace"),
            @ApiResponse(code = 400, message = "the provided changed huntingPlace is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putHuntingPlace(HuntingPlace huntingPlace, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(huntingPlace, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
