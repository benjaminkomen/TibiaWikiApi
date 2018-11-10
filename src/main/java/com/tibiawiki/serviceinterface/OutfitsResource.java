package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveOutfits;
import io.swagger.annotations.Api;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Outfits")
@Path("/")
public class OutfitsResource {

    @Autowired
    private RetrieveOutfits retrieveOutfits;

    private OutfitsResource() {
        // nothing to do, all dependencies are injected
    }

    @GET
    @Path("/outfits")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutfits(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveOutfits.getOutfitsJSON().map(JSONObject::toMap)
                        : retrieveOutfits.getOutfitsList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/outfits/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutfitsByName(@PathParam("name") String name) {
        return retrieveOutfits.getOutfitJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
