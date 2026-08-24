package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.RetrieveWikiPages
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus

class WikiPageControllerTest {

    @Test
    fun getWikiPageByTitle() {
        val retrieve = mock(RetrieveWikiPages::class.java)
        doReturn(mapOf("name" to "Foo")).`when`(retrieve).getWikiPageJSON("Foo")
        doReturn(null).`when`(retrieve).getWikiPageJSON("Missing")
        doReturn(emptyMap<String, Any>()).`when`(retrieve).getWikiPageJSON("Empty")
        val c = WikiPageController(retrieve)

        assertThat(c.getWikiPageByTitle("Foo").statusCode, `is`(HttpStatus.OK))
        assertThrows<ArticleNotFoundException> { c.getWikiPageByTitle("Missing") }
        assertThrows<ArticleNotFoundException> { c.getWikiPageByTitle("Empty") }
    }
}
