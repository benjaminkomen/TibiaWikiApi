package com.tibiawiki.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI customOpenAPI(@Autowired BuildProperties buildProperties) {
        return new OpenAPI()
                .components(new Components())
                .addServersItem(new Server().url("/"))
                .info(new Info()
                        .title("TibiaWikiApi")
                        .contact(new Contact().name("B. Komen"))
                        .version(buildProperties.getVersion())
                        .description("Expose data (creatures, items, quests, etc.) from TibiaWiki (https://tibia.wikia.com) with a REST API")
                        .license(new License().name("MIT License").url("https://github.com/benjaminkomen/TibiaWikiApi/blob/master/LICENSE"))
                );
    }
}
