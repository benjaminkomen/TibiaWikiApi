package com.tibiawiki.serviceinterface.config;

import com.tibiawiki.serviceinterface.AchievementsResource;
import com.tibiawiki.serviceinterface.BooksResource;
import com.tibiawiki.serviceinterface.BuildingsResource;
import com.tibiawiki.serviceinterface.CorpsesResource;
import com.tibiawiki.serviceinterface.CreaturesResource;
import com.tibiawiki.serviceinterface.EffectsResource;
import com.tibiawiki.serviceinterface.HuntingPlacesResource;
import com.tibiawiki.serviceinterface.ItemsResource;
import com.tibiawiki.serviceinterface.KeysResource;
import com.tibiawiki.serviceinterface.LocationsResource;
import com.tibiawiki.serviceinterface.MissilesResource;
import com.tibiawiki.serviceinterface.MountsResource;
import com.tibiawiki.serviceinterface.NPCsResource;
import com.tibiawiki.serviceinterface.ObjectsResource;
import com.tibiawiki.serviceinterface.OutfitsResource;
import com.tibiawiki.serviceinterface.QuestsResource;
import com.tibiawiki.serviceinterface.SpellsResource;
import com.tibiawiki.serviceinterface.StreetsResource;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class JerseyConfig extends ResourceConfig {

    @Value("${spring.jersey.application-path:/}")
    private String apiPath;

    public JerseyConfig() {
        registerEndpoints();
    }

    @PostConstruct
    public void init() {
        // Register components where DI is needed
        this.configureSwagger();
    }

    private void registerEndpoints() {
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
        register(MissilesResource.class);
        register(MountsResource.class);
        register(NPCsResource.class);
        register(ObjectsResource.class);
        register(OutfitsResource.class);
        register(QuestsResource.class);
        register(SpellsResource.class);
        register(StreetsResource.class);
    }

    private void configureSwagger() {
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setConfigId("tibiawikiapi");
        beanConfig.setTitle("TibiaWikiApi");
        beanConfig.setVersion("1.2.0");
        beanConfig.setContact("B. Komen");
        beanConfig.setSchemes(new String[]{"http", "https"});
        beanConfig.setBasePath(this.apiPath); // location where dynamically created swagger.json is reachable
        beanConfig.setHost("localhost:8080");
        beanConfig.setResourcePackage("com.tibiawiki");
        beanConfig.setPrettyPrint(true);
        beanConfig.setScan(true); // scan packages via setResourcePackage
    }
}
