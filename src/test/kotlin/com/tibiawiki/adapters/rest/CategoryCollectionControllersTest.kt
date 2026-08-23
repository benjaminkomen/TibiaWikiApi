package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.process.RetrieveByTemplate
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.Optional
import java.util.stream.Stream

class CategoryCollectionControllersTest {

    private lateinit var retrieve: RetrieveByTemplate
    private val json = JSONObject().put("name", "Foo")
    private val names = listOf("Foo")

    @BeforeEach
    fun setup() {
        retrieve = mock(RetrieveByTemplate::class.java)
        doReturn(names).`when`(retrieve).names(InfoboxTemplate.IMBUEMENT)
        doReturn(names).`when`(retrieve).names(InfoboxTemplate.UPDATE)
        doReturn(names).`when`(retrieve).names(InfoboxTemplate.WORLD)
        doReturn(names).`when`(retrieve).names(InfoboxTemplate.FAMILIAR)
        doReturn(Stream.of(json)).`when`(retrieve).asJson(InfoboxTemplate.IMBUEMENT)
        doReturn(Stream.of(json)).`when`(retrieve).asJson(InfoboxTemplate.UPDATE)
        doReturn(Stream.of(json)).`when`(retrieve).asJson(InfoboxTemplate.WORLD)
        doReturn(Stream.of(json)).`when`(retrieve).asJson(InfoboxTemplate.FAMILIAR)
        doReturn(Optional.of(json)).`when`(retrieve).getJson("Foo")
        doReturn(Optional.empty<JSONObject>()).`when`(retrieve).getJson("Missing")
    }

    @Test
    fun imbuements() {
        val c = ImbuementsController(retrieve)
        assertListAndDetail(c.getImbuements(false), c.getImbuements(true), c.getImbuementsByName("Foo"), c.getImbuementsByName("Missing"))
    }

    @Test
    fun updates() {
        val c = UpdatesController(retrieve)
        assertListAndDetail(c.getUpdates(false), c.getUpdates(true), c.getUpdatesByName("Foo"), c.getUpdatesByName("Missing"))
    }

    @Test
    fun worlds() {
        val c = WorldsController(retrieve)
        assertListAndDetail(c.getWorlds(false), c.getWorlds(true), c.getWorldsByName("Foo"), c.getWorldsByName("Missing"))
    }

    @Test
    fun familiars() {
        val c = FamiliarsController(retrieve)
        assertListAndDetail(c.getFamiliars(false), c.getFamiliars(true), c.getFamiliarsByName("Foo"), c.getFamiliarsByName("Missing"))
    }

    private fun assertListAndDetail(
        list: ResponseEntity<Any>,
        expanded: ResponseEntity<Any>,
        found: ResponseEntity<String>,
        missing: ResponseEntity<String>
    ) {
        assertThat(list.statusCode, `is`(HttpStatus.OK))
        assertThat(list.body, `is`(names))
        assertThat(expanded.statusCode, `is`(HttpStatus.OK))
        @Suppress("UNCHECKED_CAST")
        assertThat((expanded.body as Iterable<Any>).toList(), hasSize(1))
        assertThat(found.statusCode, `is`(HttpStatus.OK))
        assertThat(missing.statusCode, `is`(HttpStatus.NOT_FOUND))
    }
}
