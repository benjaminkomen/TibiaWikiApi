package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveEffects;
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
@Api(value = "Effects")
@Path("/")
public class EffectsResource {

    @Autowired
    private RetrieveEffects retrieveEffects;

    private EffectsResource() {
        // nothing to do, all dependencies are injected
    }

    @GET
    @Path("/effects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEffects(@QueryParam("expand") Boolean expand) {
        return Response.ok()
                .entity(expand != null && expand
                        ? retrieveEffects.getEffectsJSON().map(JSONObject::toMap)
                        : retrieveEffects.getEffectsList()
                )
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/effects/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEffectsByName(@PathParam("name") String name) {
        return retrieveEffects.getEffectJSON(name)
                .map(a -> Response.ok()
                        .entity(a.toString(2))
                        .header("Access-Control-Allow-Origin", "*")
                        .build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .build());
    }
}
