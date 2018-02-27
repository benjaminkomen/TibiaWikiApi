package com.tibiawiki.serviceinterface;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class TestResource {

    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public String test() {
        return "{ " +
                "\"parameter\": \"Hello world!\"" +
                "}";
    }
}