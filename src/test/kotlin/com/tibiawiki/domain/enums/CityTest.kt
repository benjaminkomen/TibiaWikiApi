package com.tibiawiki.domain.enums

import com.tibiawiki.config.JacksonConfiguration
import com.tibiawiki.domain.factories.WikiObjectFactory
import com.tibiawiki.domain.objects.Building
import com.tibiawiki.domain.objects.HuntingPlace
import com.tibiawiki.domain.objects.NPC
import com.tibiawiki.domain.objects.Street
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper

class CityTest {

    private lateinit var objectMapper: ObjectMapper
    private lateinit var wikiObjectFactory: WikiObjectFactory

    @BeforeEach
    fun setup() {
        val builder = JsonMapper.builder()
        JacksonConfiguration().jsonMapperBuilderCustomizer().customize(builder)
        objectMapper = builder.build()
        wikiObjectFactory = WikiObjectFactory(objectMapper)
    }

    @Test
    fun newHometownsRoundTripThroughJsonValue() {
        assertJsonValueRoundTrip(City.BOUNAC, "Bounac")
        assertJsonValueRoundTrip(City.ISSAVI, "Issavi")
        assertJsonValueRoundTrip(City.MARAPUR, "Marapur")
    }

    @Test
    fun fixtureAndAuditHometownsAreRepresented() {
        val descriptions = City.entries.map { it.description }.toSet()

        KNOWN_HOMETOWNS.forEach { hometown ->
            assertThat(
                "City must include hometown '$hometown' so typed mapping does not drop it",
                descriptions.contains(hometown),
                `is`(true)
            )
        }
    }

    @Test
    fun typedNpcMappingKeepsNewHometowns() {
        assertTypedNpcCity(City.BOUNAC, "Bounac")
        assertTypedNpcCity(City.ISSAVI, "Issavi")
        assertTypedNpcCity(City.MARAPUR, "Marapur")
    }

    @Test
    fun typedHuntBuildingAndStreetMappingKeepIssavi() {
        val hunt = wikiObjectFactory.createWikiObject(cityJson("Hunt", "Issavi Place"))
        val building = wikiObjectFactory.createWikiObject(cityJson("Building", "Issavi Place"))
        val street = wikiObjectFactory.createWikiObject(cityJson("Street", "Issavi Place"))

        assertThat(hunt, instanceOf(HuntingPlace::class.java))
        assertThat((hunt as HuntingPlace).city, `is`(City.ISSAVI))
        assertThat(building, instanceOf(Building::class.java))
        assertThat((building as Building).city, `is`(City.ISSAVI))
        assertThat(street, instanceOf(Street::class.java))
        assertThat((street as Street).city, `is`(City.ISSAVI))
    }

    @Test
    fun unknownHometownDropsTypedNpcRatherThanMapping() {
        val result = wikiObjectFactory.createWikiObject(npcJson("Not A City"))

        assertThat(result, `is`(nullValue()))
    }

    private fun assertJsonValueRoundTrip(city: City, wikiValue: String) {
        assertThat(city.description, `is`(wikiValue))
        assertThat(objectMapper.writeValueAsString(city), `is`("\"$wikiValue\""))
        assertThat(objectMapper.readValue("\"$wikiValue\"", City::class.java), `is`(city))
    }

    private fun assertTypedNpcCity(city: City, wikiValue: String) {
        val result = wikiObjectFactory.createWikiObject(npcJson(wikiValue))

        assertThat(result, `is`(notNullValue()))
        assertThat(result, instanceOf(NPC::class.java))
        assertThat((result as NPC).city, `is`(city))
        assertThat(result.name, `is`("Hometown NPC"))
    }

    private fun npcJson(city: String): Map<String, Any> {
        return mapOf(
            "templateType" to "NPC",
            "name" to "Hometown NPC",
            "city" to city
        )
    }

    private fun cityJson(templateType: String, name: String): Map<String, Any> {
        return mapOf(
            "templateType" to templateType,
            "name" to name,
            "city" to "Issavi"
        )
    }

    companion object {
        private val KNOWN_HOMETOWNS = listOf(
            // regression fixtures and existing tests
            "Carlin",
            "Edron",
            "Liberty Bay",
            "Port Hope",
            "Thais",
            // wiki freshness audit (August 2026) Geography / NPC hometowns
            "Bounac",
            "Issavi",
            "Marapur"
        )
    }
}
