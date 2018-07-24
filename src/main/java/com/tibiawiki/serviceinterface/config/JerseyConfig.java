package com.tibiawiki.serviceinterface.config;

import com.tibiawiki.serviceinterface.*;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        registerEndpoints();
    }

    private void registerEndpoints() {
        register(DefaultResource.class);
        register(AchievementsResource.class);
        register(BooksResource.class);
        register(BuildingsResource.class);
        register(CorpsesResource.class);
        register(CreaturesResource.class);
        register(EffectsResource.class);
        register(LocationsResource.class);
        register(HuntingPlacesResource.class);
        register(KeysResource.class);
        register(MountsResource.class);
        register(NPCsResource.class);
        register(OutfitsResource.class);
    }
}
