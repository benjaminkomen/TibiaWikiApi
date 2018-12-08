package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Key;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveKeys;
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
@Api(value = "Keys")
@Path("/keys")
public class KeysResource {

    private RetrieveKeys retrieveKeys;
    private ModifyAny modifyAny;

    @Autowired
    private KeysResource(RetrieveKeys retrieveKeys, ModifyAny modifyAny) {
        this.retrieveKeys = retrieveKeys;
        this.modifyAny = modifyAny;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKeys(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveKeys.getKeysJSON().map(JSONObject::toMap)
                        : retrieveKeys.getKeysList()
                )
                .build();
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKeysByName(@PathParam("name") String name) {
        return retrieveKeys.getKeyJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed key"),
            @ApiResponse(code = 400, message = "the provided changed key is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putKey(Key key, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(key, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
