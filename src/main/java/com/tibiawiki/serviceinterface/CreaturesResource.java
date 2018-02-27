package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.objects.Creature;
import com.tibiawiki.process.RetrieveCreatures;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

public class CreaturesResource {

    private RetrieveCreatures retrieveCreatures;

    public CreaturesResource() {
        retrieveCreatures = new RetrieveCreatures();
    }

    @GET
    @Path("/creatures")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCreatures() {

        List<Creature> creatures = retrieveCreatures.getCreatures();

        return Response.ok()
                .entity(creatures)
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/creatures/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCreatureByName(@PathParam("name") String name) {
        Optional<Creature> creature = retrieveCreatures.getCreatures().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();

        if (!creature.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        } else {
            return Response.ok()
                    .entity(creature)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
    }
}
