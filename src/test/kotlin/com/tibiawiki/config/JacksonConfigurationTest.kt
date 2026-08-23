package com.tibiawiki.config

import com.tibiawiki.domain.objects.Achievement
import com.tibiawiki.domain.objects.Charm
import com.tibiawiki.domain.objects.Missile
import com.tibiawiki.domain.objects.WikiObject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper

class JacksonConfigurationTest {

    @Test
    fun mixinAppliesTemplateTypeSubtypesOntoWikiObject() {
        val mapper = configuredMapper()

        val wikiObject = mapper.readValue(
            """{"templateType":"Achievement","name":"Goo Goo Dancer"}""",
            WikiObject::class.java
        )

        assertThat(wikiObject, instanceOf(Achievement::class.java))
        assertThat((wikiObject as Achievement).name, `is`("Goo Goo Dancer"))
        assertThat(mapper.serializationConfig().timeZone.id, `is`("Europe/Paris"))
    }

    @Test
    fun mixinDeserializesCharmWithNameAndMinorType() {
        val mapper = configuredMapper()

        val wikiObject = mapper.readValue(
            """{"templateType":"Charm","name":"Adrenaline Burst","type":"Minor","cost":"100 / 150 / 225","effect":"Boosts damage"}""",
            WikiObject::class.java
        )

        assertThat(wikiObject, instanceOf(Charm::class.java))
        val charm = wikiObject as Charm
        assertThat(charm.name, `is`("Adrenaline Burst"))
        assertThat(charm.type, `is`(Charm.Type.Minor))
        assertThat(charm.cost, `is`("100 / 150 / 225"))
        assertThat(charm.effect, `is`("Boosts damage"))
    }

    @Test
    fun mixinDeserializesMissileWithName() {
        val mapper = configuredMapper()

        val wikiObject = mapper.readValue(
            """{"templateType":"Missile","name":"Throwing Cake Missile","missileid":42}""",
            WikiObject::class.java
        )

        assertThat(wikiObject, instanceOf(Missile::class.java))
        val missile = wikiObject as Missile
        assertThat(missile.name, `is`("Throwing Cake Missile"))
        assertThat(missile.missileid, `is`(42))
    }

    private fun configuredMapper(): ObjectMapper {
        val builder = JsonMapper.builder()
        JacksonConfiguration().jsonMapperBuilderCustomizer().customize(builder)
        return builder.build()
    }
}
