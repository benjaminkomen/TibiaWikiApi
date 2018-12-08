package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Item;
import com.tibiawiki.domain.objects.validation.ValidationException;
import com.tibiawiki.process.ModifyAny;
import com.tibiawiki.process.RetrieveItems;
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
@Api(value = "Items")
@Path("/")
public class ItemsResource {

    private RetrieveItems retrieveItems;
    private ModifyAny modifyAny;

    @Autowired
    private ItemsResource(RetrieveItems retrieveItems, ModifyAny modifyAny) {
        this.retrieveItems = retrieveItems;
        this.modifyAny = modifyAny;
    }

    @GET
    @Path("/items")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveItems.getItemsJSON().map(JSONObject::toMap)
                        : retrieveItems.getItemsList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/items/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemsByName(@PathParam("name") String name) {
        return retrieveItems.getItemJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }

    @PUT
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "the changed item"),
            @ApiResponse(code = 400, message = "the provided changed item is not valid"),
            @ApiResponse(code = 401, message = "not authorized to edit without providing credentials")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putItem(Item item, @HeaderParam("X-WIKI-Edit-Summary") String editSummary) {
        return modifyAny.modify(item, editSummary)
                .map(a -> Response.ok()
                        .entity(a)
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .recover(ValidationException.class, e -> Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build())
                .recover(e -> Response.serverError().build())
                .get();
    }
}
