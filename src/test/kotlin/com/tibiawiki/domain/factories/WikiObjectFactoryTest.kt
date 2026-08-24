package com.tibiawiki.domain.factories

import com.tibiawiki.config.JacksonConfiguration
import com.tibiawiki.domain.objects.Charm
import com.tibiawiki.domain.objects.Familiar
import com.tibiawiki.domain.objects.Imbuement
import com.tibiawiki.domain.objects.Missile
import com.tibiawiki.domain.objects.Update
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.WikiObjectFixtures
import com.tibiawiki.domain.objects.World
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper

class WikiObjectFactoryTest {

    private lateinit var target: WikiObjectFactory
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = mock(ObjectMapper::class.java)
        target = WikiObjectFactory(objectMapper)
    }

    @Test
    fun testCreateWikiObject_MissingTemplateType() {
        val result = target.createWikiObject(emptyMap())

        assertThat(result, instanceOf(WikiObject::class.java))
        assertThat(result?.getTemplateType(), `is`("WikiObjectImpl"))
    }

    @Test
    fun testCreateWikiObject_Achievement() {
        doReturn(makeAchievement()).`when`(objectMapper).convertValue(any(Map::class.java), any(Class::class.java))

        val someJson = mapOf("templateType" to "Achievement")
        val result = target.createWikiObject(someJson)

        assertThat(result, instanceOf(WikiObject::class.java))
        assertThat(result?.getTemplateType(), `is`("Achievement"))
        assertThat(result?.name, `is`("Goo Goo Dancer"))
    }

    @Test
    fun testCreateWikiObject_CharmPopulatesName() {
        target = WikiObjectFactory(realMapper())

        val json = mapOf(
            "templateType" to "Charm",
            "name" to "Adrenaline Burst",
            "actualname" to "adrenaline burst",
            "type" to "Minor",
            "cost" to "100 / 150 / 225",
            "effect" to "Boosts damage for a short time.",
            "implemented" to "11.50.6055"
        )
        val result = target.createWikiObject(json)

        assertThat(result, instanceOf(Charm::class.java))
        val charm = result as Charm
        assertThat(charm.getTemplateType(), `is`("Charm"))
        assertThat(charm.name, `is`("Adrenaline Burst"))
        assertThat(charm.actualname, `is`("adrenaline burst"))
        assertThat(charm.type, `is`(Charm.Type.Minor))
        assertThat(charm.cost, `is`("100 / 150 / 225"))
        assertThat(charm.implemented, `is`("11.50.6055"))
    }

    @Test
    fun testCreateWikiObject_CharmAcceptsMinorType() {
        target = WikiObjectFactory(realMapper())

        val json = mapOf(
            "templateType" to "Charm",
            "name" to "Adrenaline Burst",
            "type" to "Minor"
        )
        val result = target.createWikiObject(json)

        assertThat(result, instanceOf(Charm::class.java))
        assertThat((result as Charm).type, `is`(Charm.Type.Minor))
        assertThat(result.name, `is`("Adrenaline Burst"))
    }

    @Test
    fun testCreateWikiObject_MissilePopulatesName() {
        target = WikiObjectFactory(realMapper())

        val json = mapOf(
            "templateType" to "Missile",
            "name" to "Throwing Cake Missile",
            "missileid" to 42,
            "implemented" to "7.9"
        )
        val result = target.createWikiObject(json)

        assertThat(result, instanceOf(Missile::class.java))
        val missile = result as Missile
        assertThat(missile.getTemplateType(), `is`("Missile"))
        assertThat(missile.name, `is`("Throwing Cake Missile"))
        assertThat(missile.missileid, `is`(42))
        assertThat(missile.implemented, `is`("7.9"))
    }

    @Test
    fun testCreateJSONObject_Success() {
        val someWikiObject = makeAchievement()
        val someMap = HashMap<String, Any>()
        doReturn(someMap).`when`(objectMapper).convertValue(any(WikiObject::class.java), any(Class::class.java))
        val result = target.createJSONObject(someWikiObject, SOME_TEMPLATE_TYPE)

        assertThat(result, instanceOf(Map::class.java))
        assertThat(result["templateType"], `is`(SOME_TEMPLATE_TYPE))
    }

    @Test
    fun testCreateJSONObject_CharmIncludesName() {
        target = WikiObjectFactory(realMapper())
        val charm = WikiObjectFixtures.charm()

        val result = target.createJSONObject(charm, "Charm")

        assertThat(result.get("templateType"), `is`("Charm"))
        assertThat(result.get("name"), `is`("Adrenaline Burst"))
        assertThat(result.get("type").toString(), `is`("Minor"))
        assertThat(result.get("cost"), `is`("100 / 150 / 225"))
    }

    @Test
    fun testCreateJSONObject_MissileIncludesName() {
        target = WikiObjectFactory(realMapper())
        val missile = WikiObjectFixtures.missile()

        val result = target.createJSONObject(missile, "Missile")

        assertThat(result.get("templateType"), `is`("Missile"))
        assertThat(result.get("name"), `is`("Throwing Cake Missile"))
        assertThat(result.get("missileid"), `is`(42))
    }

    @Test
    fun createWikiObject_FoldedGetOnlyTypes() {
        target = WikiObjectFactory(realMapper())

        val world = target.createWikiObject(
            mapOf("templateType" to "World", "name" to "Antica", "location" to "Europe", "pvpType" to "Open PvP")
        )
        val update = target.createWikiObject(
            mapOf("templateType" to "Update", "name" to "Summer Update 2020", "date" to "July 13, 2020")
        )
        val familiar = target.createWikiObject(
            mapOf(
                "templateType" to "Familiar",
                "name" to "Bladespark",
                "voc" to "[[Paladin]]s",
                "mana" to "2000",
                "cooldown" to "30",
                "premium" to "yes"
            )
        )
        val imbuement = target.createWikiObject(
            mapOf(
                "templateType" to "Imbuement",
                "name" to "Powerful Strike",
                "prefix" to "Powerful",
                "type" to "Strike",
                "amount" to "3"
            )
        )

        assertThat(world, instanceOf(World::class.java))
        assertThat((world as World).location, `is`("Europe"))
        assertThat(update, instanceOf(Update::class.java))
        assertThat((update as Update).date, `is`("July 13, 2020"))
        assertThat(familiar, instanceOf(Familiar::class.java))
        val bladespark = familiar as Familiar
        assertThat(bladespark.mana, `is`(2000))
        assertThat(bladespark.vocations.map { it.description }, `is`(listOf("paladin")))
        assertThat(imbuement, instanceOf(Imbuement::class.java))
        assertThat((imbuement as Imbuement).prefix, `is`("Powerful"))
    }

    @Test
    fun createJSONObject_FoldedGetOnlyTypesIncludeName() {
        target = WikiObjectFactory(realMapper())

        assertThat(target.createJSONObject(WikiObjectFixtures.world(), "World")["name"], `is`("Antica"))
        assertThat(target.createJSONObject(WikiObjectFixtures.update(), "Update")["name"], `is`("Summer Update 2020"))
        assertThat(target.createJSONObject(WikiObjectFixtures.familiar(), "Familiar")["name"], `is`("Grovebeast"))
        assertThat(target.createJSONObject(WikiObjectFixtures.imbuement(), "Imbuement")["prefix"], `is`("Powerful"))
    }

    private fun makeAchievement(): WikiObject = WikiObjectFixtures.namedAchievement("Goo Goo Dancer")

    private fun realMapper(): ObjectMapper {
        val builder = JsonMapper.builder()
        JacksonConfiguration().jsonMapperBuilderCustomizer().customize(builder)
        return builder.build()
    }

    companion object {
        private const val SOME_TEMPLATE_TYPE = "Achievement"
    }
}
