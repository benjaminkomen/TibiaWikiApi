package com.tibiawiki.domain.objects

import com.tibiawiki.config.JacksonConfiguration
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.factories.WikiObjectFactory
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper

class CharmTest {

    @Test
    fun typedPathKeepsMinorAndMajor() {
        val mapper = productionMapper()
        val wikiObjectFactory = WikiObjectFactory(mapper)

        val minor = mapper.readValue(MINOR_JSON, Charm::class.java)
        assertThat(minor.type, `is`(Charm.Type.Minor))
        assertThat(minor.cost, `is`(TIERED_MINOR_COST))
        assertThat(wikiObjectFactory.createJSONObject(minor, "Charm").getString("type"), `is`("Minor"))

        val major = mapper.readValue(MAJOR_JSON, Charm::class.java)
        assertThat(major.type, `is`(Charm.Type.Major))
        assertThat(wikiObjectFactory.createJSONObject(major, "Charm").getString("type"), `is`("Major"))
    }

    @Test
    fun fixtureInfoboxMapsToMinorWithoutStrippingType() {
        val json = JsonFactory().convertInfoboxPartOfArticleToJson(adrenalineBurstInfobox)
        val charm = productionMapper().readValue(json.toString(), Charm::class.java)

        assertThat(charm.type, `is`(Charm.Type.Minor))
        assertThat(charm.cost, `is`(TIERED_MINOR_COST))
    }

    private fun productionMapper() = JsonMapper.builder()
        .also { JacksonConfiguration().jsonMapperBuilderCustomizer().customize(it) }
        .build()

    companion object {
        private const val TIERED_MINOR_COST = "100 / 150 / 225"
        private const val MINOR_JSON = "{\"type\":\"Minor\",\"cost\":\"100 / 150 / 225\",\"effect\":\"boost\"}"
        private const val MAJOR_JSON = "{\"type\":\"Major\",\"cost\":\"600 / 900 / 3000\",\"effect\":\"aoe\"}"
        private val adrenalineBurstInfobox = """
            {{Infobox Charm
            | name         = Adrenaline Burst
            | type         = Minor
            | cost         = 100 / 150 / 225
            | effect       = Boosts damage for a short time.
            | implemented  = 11.50.6055
            | status       = active
            }}
        """.trimIndent()
    }
}
