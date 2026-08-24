package com.tibiawiki.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import io.swagger.v3.oas.models.responses.ApiResponse as OpenApiResponse

@Configuration
class OpenAPIConfiguration {

    @Bean
    fun customOpenAPI(buildProperties: BuildProperties): OpenAPI {
        return OpenAPI()
            .addServersItem(Server().url("/"))
            .components(
                Components().addSecuritySchemes(
                    WikiWriteApiDocs.SECURITY_SCHEME,
                    SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .`in`(SecurityScheme.In.HEADER)
                        .name(WikiWriteFilter.TOKEN_HEADER)
                        .description(
                            "Required on PUT only when WIKI_WRITE_TOKEN is set. " +
                                "Authorization: Bearer <token> is also accepted. " +
                                "Public Cloud Run leaves WIKI_WRITE_ENABLED=false, so PUT returns 403."
                        )
                )
            )
            .info(
                Info()
                    .title("TibiaWikiApi")
                    .contact(Contact().name("B. Komen"))
                    .version(buildProperties.version)
                    .description(
                        "Expose data (creatures, items, quests, etc.) from TibiaWiki " +
                            "(https://tibia.fandom.com) with a REST API. " +
                            "PUT is disabled unless WIKI_WRITE_ENABLED=true."
                    )
                    .license(License().name("MIT License").url("https://github.com/benjaminkomen/TibiaWikiApi/blob/master/LICENSE"))
            )
    }

    @Bean
    fun wikiCategoryOpenApiCustomizer(): OpenApiCustomizer {
        return WikiCategoryOpenApiCustomizer()
    }

    @Bean
    @Order(WIKI_WRITE_CUSTOMIZER_ORDER)
    fun wikiWriteOpenApiCustomizer(): OpenApiCustomizer {
        return OpenApiCustomizer { openApi ->
            openApi.paths?.values?.forEach { pathItem ->
                val put = pathItem.put ?: return@forEach
                put.responses?.addApiResponse(
                    "401",
                    OpenApiResponse().description(WikiWriteApiDocs.UNAUTHORIZED)
                )
                put.responses?.addApiResponse(
                    "403",
                    OpenApiResponse().description(WikiWriteApiDocs.FORBIDDEN)
                )
                put.addSecurityItem(SecurityRequirement().addList(WikiWriteApiDocs.SECURITY_SCHEME))
            }
        }
    }

    companion object {
        const val WIKI_WRITE_CUSTOMIZER_ORDER = WikiCategoryOpenApiCustomizer.EXPANSION_ORDER + 1
    }
}
