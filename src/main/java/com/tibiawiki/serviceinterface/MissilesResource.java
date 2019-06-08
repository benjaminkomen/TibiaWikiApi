package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Missile;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveMissiles;
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
@Api(value = "Missiles")
@Path("/missiles")
public class MissilesResource {

    private RetrieveMissiles retrieveMissiles;
    private ModifyAny modifyAny;

    @Autowired
    private MissilesResource(RetrieveMissiles retrieveMissiles, ModifyAny modifyAny) {
        this.retrieveMissiles = retrieveMissiles;
        this.modifyAny = modifyAny;
    }

    @GET
    @ApiOperation(value = "Get a list of missiles")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of missiles retrieved")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMissiles(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the missile names but the full missiles", required = false)
                                @QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveMissiles.getMissilesJSON().map(JSONObject::toMap)
                        : retrieveMissiles.getMissilesList()
                )
                .build();
    }

    @GET
    @Path("/{name}")
    @ApiOperation(value = "Get a specific missile by name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMissilesByName(@PathParam("name") String name) {
        return retrieveMissiles.getMissileJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiOperation(value = "Modify a missile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed missile"),
            @ApiResponse(code = 400, message = "the provided changed missile is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putMissile(Missile missile, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(missile, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
