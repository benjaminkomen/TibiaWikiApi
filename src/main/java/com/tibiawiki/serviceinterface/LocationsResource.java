package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Location;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveLocations;
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
@Api(value = "Locations")
@Path("/locations")
public class LocationsResource {

    private RetrieveLocations retrieveLocations;
    private ModifyAny modifyAny;

    @Autowired
    private LocationsResource(RetrieveLocations retrieveLocations, ModifyAny modifyAny) {
        this.retrieveLocations = retrieveLocations;
        this.modifyAny = modifyAny;
    }

    @GET
    @ApiOperation(value = "Get a list of locations")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of locations retrieved")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocations(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the location names but the full locations", required = false)
                                 @QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveLocations.getLocationsJSON().map(JSONObject::toMap)
                        : retrieveLocations.getLocationsList()
                )
                .build();
    }

    @GET
    @Path("/{name}")
    @ApiOperation(value = "Get a specific location by name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocationsByName(@PathParam("name") String name) {
        return retrieveLocations.getLocationJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiOperation(value = "Modify a location")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed location"),
            @ApiResponse(code = 400, message = "the provided changed location is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putLocation(Location location, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(location, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
