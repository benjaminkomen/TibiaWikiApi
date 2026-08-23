package com.tibiawiki.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.tibiawiki.domain.jackson.WikiObjectMixin
import com.tibiawiki.domain.objects.WikiObject
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.cfg.DateTimeFeature
import tools.jackson.module.kotlin.KotlinModule
import java.util.TimeZone

@Configuration
class JacksonConfiguration {

    @Bean
    fun jsonMapperBuilderCustomizer(): JsonMapperBuilderCustomizer {
        return JsonMapperBuilderCustomizer { builder ->
            builder
                .findAndAddModules()
                .addModule(KotlinModule.Builder().build())
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .changeDefaultPropertyInclusion { incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL) }
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .defaultTimeZone(TimeZone.getTimeZone("Europe/Paris"))
                // Jackson: addMixIn(target, mixinSource) — apply mixin annotations onto WikiObject
                .addMixIn(WikiObject::class.java, WikiObjectMixin::class.java)
        }
    }
}
