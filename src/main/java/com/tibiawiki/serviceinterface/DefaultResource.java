package com.tibiawiki.serviceinterface;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Api(value = "Default")
@Path("/")
public class DefaultResource {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHome() {
        return Response.ok()
                .entity("{ \"message\": \"Welcome to this API!\"}")
                .build();
    }
}
