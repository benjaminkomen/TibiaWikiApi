package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveLoot;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Loot Statistics")
@Path("/v2/loot")
@RequiredArgsConstructor
public class LootStatisticsV2Resource {

    private final RetrieveLoot retrieveLoot;

    @GET
    @ApiOperation(value = "Get a list of loot statistics")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "list of loot statistics retrieved")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLoot(@ApiParam(value = "optionally expands the result to retrieve not only " +
            "the loot statistics page names but the full loot statistics", required = false)
                            @QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveLoot.getAllLootPartsJSON().map(JSONObject::toMap)
                        : retrieveLoot.getLootList()
                )
                .build();
    }

    @GET
    @Path("/{name}")
    @ApiOperation(value = "Get a specific loot statistics page by creature name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLootByName(@PathParam("name") String name) {
        return retrieveLoot.getAllLootPartsJSON("Loot_Statistics:" + name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
