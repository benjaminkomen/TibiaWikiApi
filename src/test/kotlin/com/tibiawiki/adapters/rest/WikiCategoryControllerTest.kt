package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.objects.Achievement
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.WikiObjectFixtures
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.ModifyResult
import com.tibiawiki.process.RetrieveByTemplate
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule

class WikiCategoryControllerTest {

    private lateinit var retrieveByTemplate: RetrieveByTemplate
    private lateinit var modifyAny: ModifyAny
    private lateinit var controller: WikiCategoryController
    private val json = mapOf("name" to "Foo")
    private val names = listOf("Foo")
    private val mapper = JsonMapper.builder().addModule(KotlinModule.Builder().build()).build()

    @BeforeEach
    fun setup() {
        retrieveByTemplate = mock(RetrieveByTemplate::class.java)
        modifyAny = mock(ModifyAny::class.java)
        controller = WikiCategoryController(retrieveByTemplate, modifyAny, mapper)
    }

    @Test
    fun listDetailAndPutUseTheCategoryTemplate() {
        doReturn(names).`when`(retrieveByTemplate).pageNames(InfoboxTemplate.ACHIEVEMENT)
        doReturn(listOf(json)).`when`(retrieveByTemplate).articlesAsJSON(InfoboxTemplate.ACHIEVEMENT)
        doReturn(json).`when`(retrieveByTemplate).articleAsJSON("Foo")
        doReturn(null).`when`(retrieveByTemplate).articleAsJSON("Missing")

        val body = WikiObjectFixtures.achievement()
        doReturn(ModifyResult.Success(body)).`when`(modifyAny).modify(
            org.mockito.ArgumentMatchers.any<WikiObject>() ?: body,
            org.mockito.ArgumentMatchers.eq("edit") ?: "edit"
        )

        val list = controller.getWikiObjects("achievements", false)
        val expanded = controller.getWikiObjects("achievements", true)
        val found = controller.getWikiObjectByName("achievements", "Foo")
        val put = controller.putWikiObject("achievements", mapper.valueToTree(body), "edit")

        assertThat(list.statusCode, `is`(HttpStatus.OK))
        assertThat(list.body, `is`(names))
        assertThat(expanded.statusCode, `is`(HttpStatus.OK))
        @Suppress("UNCHECKED_CAST")
        assertThat((expanded.body as Iterable<Any>).toList(), hasSize(1))
        assertThat(found.statusCode, `is`(HttpStatus.OK))
        assertThrows<ArticleNotFoundException> {
            controller.getWikiObjectByName("achievements", "Missing")
        }
        assertThat(put.statusCode, `is`(HttpStatus.OK))
        assertThat(put.body, `is`(body as WikiObject))
    }

    @Test
    fun itemsUsePickupableObjectsTemplate() {
        doReturn(names).`when`(retrieveByTemplate).pageNames(InfoboxTemplate.ITEM)

        val list = controller.getWikiObjects("items", false)

        assertThat(list.statusCode, `is`(HttpStatus.OK))
        assertThat(list.body, `is`(names))
    }

    @Test
    fun unknownCategoryIsNotFound() {
        val list = controller.getWikiObjects("not-a-category", false)
        val detail = controller.getWikiObjectByName("not-a-category", "Foo")
        val put = controller.putWikiObject("not-a-category", mapper.readTree("""{"name":"Foo"}"""), "edit")

        assertThat(list.statusCode, `is`(HttpStatus.NOT_FOUND))
        assertThat(detail.statusCode, `is`(HttpStatus.NOT_FOUND))
        assertThat(put.statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun putDeserializesToTheCategoryWikiObjectType() {
        val captured = mutableListOf<WikiObject>()
        org.mockito.Mockito.doAnswer { invocation ->
            captured.add(invocation.getArgument(0))
            ModifyResult.Success(invocation.getArgument(0))
        }.`when`(modifyAny).modify(
            org.mockito.ArgumentMatchers.any<WikiObject>() ?: WikiObject.WikiObjectImpl(),
            org.mockito.ArgumentMatchers.eq("edit") ?: "edit"
        )

        controller.putWikiObject("achievements", mapper.readTree("""{"name":"Goo Goo Dancer"}"""), "edit")

        assertThat(captured, hasSize(1))
        assertThat(captured[0] is Achievement, `is`(true))
        assertThat((captured[0] as Achievement).name, `is`(WikiObjectFixtures.achievement().name))
    }

    @Test
    fun putReturnsBadRequestWhenBodyCannotBeReadAsCategoryType() {
        val result = controller.putWikiObject("items", mapper.readTree("""{"name":"Sword"}"""), "edit")

        assertThat(result.statusCode, `is`(HttpStatus.BAD_REQUEST))
    }
}
