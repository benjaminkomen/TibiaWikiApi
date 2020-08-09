package com.tibiawiki.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAPIConfiguration {

    @Bean
    fun customOpenAPI(@Autowired buildProperties: BuildProperties): OpenAPI {
        return OpenAPI()
                .components(Components())
                .info(Info()
                        .title("TibiaWikiApi")
                        .contact(Contact().name("B. Komen"))
                        .version(buildProperties.version)
                        .description("Expose data (creatures, items, quests, etc.) from TibiaWiki (https://tibia.wikia.com) with a REST API")
                        .license(License().name("MIT License").url("https://github.com/benjaminkomen/TibiaWikiApi/blob/master/LICENSE"))
                )
    }
}