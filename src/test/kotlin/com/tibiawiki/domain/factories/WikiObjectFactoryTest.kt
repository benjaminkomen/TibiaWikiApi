package com.tibiawiki.domain.factories

import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.WikiObjectFixtures
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import tools.jackson.databind.ObjectMapper

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
        val result = target.createWikiObject(JSONObject())

        assertThat(result, instanceOf(WikiObject::class.java))
        assertThat(result?.getTemplateType(), `is`("WikiObjectImpl"))
    }

    @Test
    fun testCreateWikiObject_Achievement() {
        doReturn(makeAchievement()).`when`(objectMapper).readValue(anyString(), any(Class::class.java))

        val someJSONObject = JSONObject()
        someJSONObject.put("templateType", "Achievement")
        val result = target.createWikiObject(someJSONObject)

        assertThat(result, instanceOf(WikiObject::class.java))
        assertThat(result?.getTemplateType(), `is`("Achievement"))
        assertThat(result?.name, `is`("Goo Goo Dancer"))
    }

    @Test
    fun testCreateJSONObject_Success() {
        val someWikiObject = makeAchievement()
        val someMap = HashMap<String, Any>()
        doReturn(someMap).`when`(objectMapper).convertValue(any(WikiObject::class.java), any(Class::class.java))
        val result = target.createJSONObject(someWikiObject, SOME_TEMPLATE_TYPE)

        assertThat(result, instanceOf(JSONObject::class.java))
        assertThat(result.get("templateType"), `is`(SOME_TEMPLATE_TYPE))
    }

    private fun makeAchievement(): WikiObject = WikiObjectFixtures.namedAchievement("Goo Goo Dancer")

    companion object {
        private const val SOME_TEMPLATE_TYPE = "Achievement"
    }
}
