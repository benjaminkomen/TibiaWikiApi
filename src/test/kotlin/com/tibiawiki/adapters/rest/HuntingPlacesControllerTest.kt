package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.WikiObjectFixtures
import com.tibiawiki.process.ModifyAny
import com.tibiawiki.process.ModifyResult
import com.tibiawiki.process.RetrieveByTemplate
import jakarta.servlet.http.HttpServletRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus

class HuntingPlacesControllerTest {

    @Test
    fun listDetailWithSlashAndPut() {
        val retrieve = mock(RetrieveByTemplate::class.java)
        val modifyAny = mock(ModifyAny::class.java)
        val json = mapOf("name" to "Foo")
        val names = listOf("Foo")
        doReturn(names).`when`(retrieve).pageNames(InfoboxTemplate.HUNT)
        doReturn(listOf(json)).`when`(retrieve).articlesAsJSON(InfoboxTemplate.HUNT)
        doReturn(json).`when`(retrieve).articleAsJSON("Razachai/Inner Sanctum")
        doReturn(null).`when`(retrieve).articleAsJSON("Missing")

        val body = WikiObjectFixtures.huntingPlace()
        doReturn(ModifyResult.Success(body)).`when`(modifyAny).modify(body, "edit")

        val c = HuntingPlacesController(retrieve, modifyAny)
        val request = mock(HttpServletRequest::class.java)
        doReturn("/api/huntingplaces/Razachai/Inner%20Sanctum").`when`(request).requestURI
        val missingRequest = mock(HttpServletRequest::class.java)
        doReturn("/api/huntingplaces/Missing").`when`(missingRequest).requestURI

        val list = c.getHuntingPlaces(false)
        val expanded = c.getHuntingPlaces(true)
        val found = c.getHuntingPlacesByName(request)
        val put = c.putHuntingPlace(body, "edit")

        assertThat(list.statusCode, `is`(HttpStatus.OK))
        assertThat(list.body, `is`(names))
        assertThat(expanded.statusCode, `is`(HttpStatus.OK))
        @Suppress("UNCHECKED_CAST")
        assertThat((expanded.body as Iterable<Any>).toList(), hasSize(1))
        assertThat(found.statusCode, `is`(HttpStatus.OK))
        assertThrows<ArticleNotFoundException> { c.getHuntingPlacesByName(missingRequest) }
        assertThat(put.statusCode, `is`(HttpStatus.OK))
        assertThat(put.body, `is`(body as WikiObject))
    }
}
