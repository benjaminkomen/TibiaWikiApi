package com.tibiawiki.config

import com.tibiawiki.domain.objects.Achievement
import com.tibiawiki.domain.objects.WikiObject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper

class JacksonConfigurationTest {

    @Test
    fun mixinAppliesTemplateTypeSubtypesOntoWikiObject() {
        val builder = JsonMapper.builder()
        JacksonConfiguration().jsonMapperBuilderCustomizer().customize(builder)
        val mapper = builder.build()

        val wikiObject = mapper.readValue(
            """{"templateType":"Achievement","name":"Goo Goo Dancer"}""",
            WikiObject::class.java
        )

        assertThat(wikiObject, instanceOf(Achievement::class.java))
        assertThat((wikiObject as Achievement).name, `is`("Goo Goo Dancer"))
        assertThat(mapper.serializationConfig().timeZone.id, `is`("Europe/Paris"))
    }
}
