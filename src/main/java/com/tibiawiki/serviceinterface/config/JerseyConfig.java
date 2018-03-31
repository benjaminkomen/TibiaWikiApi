package com.tibiawiki.serviceinterface.config;

import com.tibiawiki.serviceinterface.CreaturesResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        registerEndpoints();
    }

    private void registerEndpoints() {
        register(CreaturesResource.class);
    }
}
