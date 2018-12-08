package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Mount;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveMounts;
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
@Api(value = "Mounts")
@Path("/")
public class MountsResource {

    private RetrieveMounts retrieveMounts;
    private ModifyAny modifyAny;

    @Autowired
    private MountsResource(RetrieveMounts retrieveMounts, ModifyAny modifyAny) {
        this.retrieveMounts = retrieveMounts;
        this.modifyAny = modifyAny;
    }

    @GET
    @Path("/mounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMounts(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveMounts.getMountsJSON().map(JSONObject::toMap)
                        : retrieveMounts.getMountsList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/mounts/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMountsByName(@PathParam("name") String name) {
        return retrieveMounts.getMountJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed mount"),
            @ApiResponse(code = 400, message = "the provided changed mount is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putMount(Mount mount, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(mount, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
