package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Corpse;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveCorpses;
import io.swagger.annotations.Api;
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
@Api(value = "Corpses")
@Path("/corpses")
public class CorpsesResource {

    private RetrieveCorpses retrieveCorpses;
    private ModifyAny modifyAny;

    @Autowired
    private CorpsesResource(RetrieveCorpses retrieveCorpses, ModifyAny modifyAny) {
        this.retrieveCorpses = retrieveCorpses;
        this.modifyAny = modifyAny;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCorpses(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveCorpses.getCorpsesJSON().map(JSONObject::toMap)
                        : retrieveCorpses.getCorpsesList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCorpsesByName(@PathParam("name") String name) {
        return retrieveCorpses.getCorpseJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed corpse"),
            @ApiResponse(code = 400, message = "the provided changed corpse is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putCorpse(Corpse corpse, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(corpse, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
