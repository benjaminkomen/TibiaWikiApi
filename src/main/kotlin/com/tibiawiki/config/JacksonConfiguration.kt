package com.tibiawiki.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.tibiawiki.domain.jackson.WikiObjectMixin
import com.tibiawiki.domain.objects.WikiObject
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.SerializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import java.util.TimeZone

@Configuration
class JacksonConfiguration {

    @Bean
    @Primary
    fun objectMapper(): JsonMapper {
        return JsonMapper.builder()
            .findAndAddModules()
            .addModule(KotlinModule.Builder().build())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .changeDefaultPropertyInclusion { it.withValueInclusion(JsonInclude.Include.NON_NULL) }
            .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .defaultTimeZone(TimeZone.getTimeZone("ECT"))
            .addMixIn(WikiObjectMixin::class.java, WikiObject::class.java)
            .build()
    }
}
