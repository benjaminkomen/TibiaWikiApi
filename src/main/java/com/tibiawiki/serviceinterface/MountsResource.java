package com.tibiawiki.serviceinterface;

import com.tibiawiki.process.RetrieveMounts;
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
@Api(value = "Mounts")
@Path("/")
public class MountsResource {

    private RetrieveMounts retrieveMounts;

    @Autowired
    private MountsResource(RetrieveMounts retrieveMounts) {
        this.retrieveMounts = retrieveMounts;
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
}
