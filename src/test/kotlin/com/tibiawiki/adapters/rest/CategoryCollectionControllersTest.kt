package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.process.ModifyAny
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
import org.springframework.http.ResponseEntity

class CategoryCollectionControllersTest {

    private lateinit var retrieve: RetrieveByTemplate
    private val json = mapOf("name" to "Foo")
    private val names = listOf("Foo")

    @BeforeEach
    fun setup() {
        retrieve = mock(RetrieveByTemplate::class.java)
        doReturn(names).`when`(retrieve).names(InfoboxTemplate.IMBUEMENT)
        doReturn(names).`when`(retrieve).names(InfoboxTemplate.UPDATE)
        doReturn(names).`when`(retrieve).names(InfoboxTemplate.WORLD)
        doReturn(names).`when`(retrieve).names(InfoboxTemplate.FAMILIAR)
        doReturn(names).`when`(retrieve).names(InfoboxTemplate.FANSITE)
        doReturn(names).`when`(retrieve).names(InfoboxTemplate.CIPSOFT_MEMBER)
        doReturn(listOf(json)).`when`(retrieve).asJson(InfoboxTemplate.IMBUEMENT)
        doReturn(listOf(json)).`when`(retrieve).asJson(InfoboxTemplate.UPDATE)
        doReturn(listOf(json)).`when`(retrieve).asJson(InfoboxTemplate.WORLD)
        doReturn(listOf(json)).`when`(retrieve).asJson(InfoboxTemplate.FAMILIAR)
        doReturn(listOf(json)).`when`(retrieve).asJson(InfoboxTemplate.FANSITE)
        doReturn(listOf(json)).`when`(retrieve).asJson(InfoboxTemplate.CIPSOFT_MEMBER)
        doReturn(json).`when`(retrieve).getJson("Foo")
        doReturn(null).`when`(retrieve).getJson("Missing")
    }

    @Test
    fun imbuements() {
        val c = ImbuementsController(retrieve)
        assertListAndDetail(c.getImbuements(false), c.getImbuements(true), c.getImbuementsByName("Foo")) {
            c.getImbuementsByName("Missing")
        }
    }

    @Test
    fun updates() {
        val c = UpdatesController(retrieve)
        assertListAndDetail(c.getUpdates(false), c.getUpdates(true), c.getUpdatesByName("Foo")) {
            c.getUpdatesByName("Missing")
        }
    }

    @Test
    fun worlds() {
        val c = WorldsController(retrieve)
        assertListAndDetail(c.getWorlds(false), c.getWorlds(true), c.getWorldsByName("Foo")) {
            c.getWorldsByName("Missing")
        }
    }

    @Test
    fun familiars() {
        val c = FamiliarsController(retrieve)
        assertListAndDetail(c.getFamiliars(false), c.getFamiliars(true), c.getFamiliarsByName("Foo")) {
            c.getFamiliarsByName("Missing")
        }
    }

    @Test
    fun fansites() {
        val c = FansitesController(retrieve, mock(ModifyAny::class.java))
        assertListAndDetail(c.getFansites(false), c.getFansites(true), c.getFansitesByName("Foo")) {
            c.getFansitesByName("Missing")
        }
    }

    @Test
    fun cipsoftMembers() {
        val c = CipsoftMembersController(retrieve, mock(ModifyAny::class.java))
        assertListAndDetail(c.getCipsoftMembers(false), c.getCipsoftMembers(true), c.getCipsoftMembersByName("Foo")) {
            c.getCipsoftMembersByName("Missing")
        }
    }

    private fun assertListAndDetail(
        list: ResponseEntity<Any>,
        expanded: ResponseEntity<Any>,
        found: ResponseEntity<WikiJson>,
        missing: () -> ResponseEntity<WikiJson>
    ) {
        assertThat(list.statusCode, `is`(HttpStatus.OK))
        assertThat(list.body, `is`(names))
        assertThat(expanded.statusCode, `is`(HttpStatus.OK))
        @Suppress("UNCHECKED_CAST")
        assertThat((expanded.body as Iterable<Any>).toList(), hasSize(1))
        assertThat(found.statusCode, `is`(HttpStatus.OK))
        assertThrows<ArticleNotFoundException> { missing() }
    }
}
