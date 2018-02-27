package com.tibiawiki.serviceinterface.config;

import com.tibiawiki.serviceinterface.CreaturesResource;
import com.tibiawiki.serviceinterface.TestResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        registerEndpoints();
    }

    private void registerEndpoints() {
        register(TestResource.class);
        register(CreaturesResource.class);
    }
}
