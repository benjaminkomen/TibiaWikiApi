package com.tibiawiki.serviceinterface.config;

import com.tibiawiki.serviceinterface.*;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        registerEndpoints();
        configureSwagger();
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
        register(ItemsResource.class);
        register(KeysResource.class);
        register(MountsResource.class);
        register(NPCsResource.class);
        register(ObjectsResource.class);
        register(OutfitsResource.class);
        register(QuestsResource.class);
        register(SpellsResource.class);
        register(StreetsResource.class);
    }

    public void configureSwagger() {
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setTitle("TibiaWikiApi");
        beanConfig.setVersion("1.2.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/");
        beanConfig.setResourcePackage("com.tibiawiki");
        beanConfig.setScan(true);
        beanConfig.setPrettyPrint(true);
    }
}
