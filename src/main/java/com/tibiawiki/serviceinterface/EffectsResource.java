package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Effect;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveEffects;
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
@Api(value = "Effects")
@Path("/effects")
public class EffectsResource {

    private RetrieveEffects retrieveEffects;
    private ModifyAny modifyAny;

    @Autowired
    private EffectsResource(RetrieveEffects retrieveEffects, ModifyAny modifyAny) {
        this.retrieveEffects = retrieveEffects;
        this.modifyAny = modifyAny;
    }

    @GET
    @ApiOperation(value = "Get a list of effects")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of effects retrieved")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEffects(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the effect names but the full effects", required = false)
                               @QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveEffects.getEffectsJSON().map(JSONObject::toMap)
                        : retrieveEffects.getEffectsList()
                )
                .build();
    }

    @GET
    @Path("/{name}")
    @ApiOperation(value = "Get a specific effect by name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEffectsByName(@PathParam("name") String name) {
        return retrieveEffects.getEffectJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiOperation(value = "Modify an effect")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed effect"),
            @ApiResponse(code = 400, message = "the provided changed effect is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putEffect(Effect effect, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(effect, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
