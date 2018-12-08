package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveSpells;
import io.swagger.annotations.Api;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Spells")
@Path("/")
public class SpellsResource {

    private RetrieveSpells retrieveSpells;

    @Autowired
    private SpellsResource(RetrieveSpells retrieveSpells) {
        this.retrieveSpells = retrieveSpells;
    }

    @GET
    @Path("/spells")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpells(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveSpells.getSpellsJSON().map(JSONObject::toMap)
                        : retrieveSpells.getSpellsList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/spells/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpellsByName(@PathParam("name") String name) {
        return retrieveSpells.getSpellJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
