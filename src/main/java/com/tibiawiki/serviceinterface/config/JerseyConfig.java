package com.tibiawiki.serviceinterface.config;

import com.tibiawiki.serviceinterface.AchievementsResource;
import com.tibiawiki.serviceinterface.CreaturesResource;
import com.tibiawiki.serviceinterface.DefaultResource;
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
        register(CreaturesResource.class);
    }
}
