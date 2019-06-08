package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Street;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveStreets;
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
@Api(value = "Streets")
@Path("/streets")
public class StreetsResource {

    private RetrieveStreets retrieveStreets;
    private ModifyAny modifyAny;

    @Autowired
    private StreetsResource(RetrieveStreets retrieveStreets, ModifyAny modifyAny) {
        this.retrieveStreets = retrieveStreets;
        this.modifyAny = modifyAny;
    }

    @GET
    @ApiOperation(value = "Get a list of streets")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of streets retrieved")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStreets(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the street names but the full streets", required = false)
                               @QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveStreets.getStreetsJSON().map(JSONObject::toMap)
                        : retrieveStreets.getStreetsList()
                )
                .build();
    }

    @GET
    @Path("/{name}")
    @ApiOperation(value = "Get a specific street by name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStreetsByName(@PathParam("name") String name) {
        return retrieveStreets.getStreetJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiOperation(value = "Modify a street")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed street"),
            @ApiResponse(code = 400, message = "the provided changed street is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putStreet(Street street, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(street, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
