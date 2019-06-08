package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.TibiaObject;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveObjects;
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
@Api(value = "Objects")
@Path("/objects")
public class ObjectsResource {

    private RetrieveObjects retrieveObjects;
    private ModifyAny modifyAny;

    @Autowired
    private ObjectsResource(RetrieveObjects retrieveObjects, ModifyAny modifyAny) {
        this.retrieveObjects = retrieveObjects;
        this.modifyAny = modifyAny;
    }

    @GET
    @ApiOperation(value = "Get a list of objects")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of objects retrieved")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getObjects(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the object names but the full objects", required = false)
                               @QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveObjects.getObjectsJSON().map(JSONObject::toMap)
                        : retrieveObjects.getObjectsList()
                )
                .build();
    }

    @GET
    @Path("/{name}")
    @ApiOperation(value = "Get a specific object by name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getObjectsByName(@PathParam("name") String name) {
        return retrieveObjects.getObjectJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiOperation(value = "Modify an object")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed tibiaObject"),
            @ApiResponse(code = 400, message = "the provided changed tibiaObject is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putTibiaObject(TibiaObject tibiaObject, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(tibiaObject, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
