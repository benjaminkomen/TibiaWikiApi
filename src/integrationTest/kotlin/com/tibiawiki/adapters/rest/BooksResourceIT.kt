package com.tibiawiki.adapters.rest

import com.tibiawiki.TestUtils.makeHttpHeaders
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.objects.Book
import com.tibiawiki.domain.objects.WikiObjectFixtures
import com.tibiawiki.domain.repositories.ArticleRepository
import com.tibiawiki.process.RetrieveAny
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doReturn
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class BooksResourceIT {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockitoBean
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun givenGetBooksNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoBookNames() {
        doReturn(listOf("baz")).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(listOf("foo", "bar", "baz")).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.BOOK.categoryName)

        val result = restTemplate.getForEntity("/api/books?expand=false", List::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body!!.size, `is`(2))
        assertThat(result.body!![0], `is`("foo"))
        assertThat(result.body!![1], `is`("bar"))
    }

    @Test
    fun givenGetBooksExpanded_whenCorrectRequest_thenResponseIsOkAndContainsOneBook() {
        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(listOf("Dungeon Survival Guide (Book)")).`when`(articleRepository).getPageNamesFromCategory(InfoboxTemplate.BOOK.categoryName)
        doReturn(mapOf("Dungeon Survival Guide (Book)" to INFOBOX_BOOK_TEXT))
            .`when`(articleRepository).getArticlesFromCategory(listOf("Dungeon Survival Guide (Book)"))

        val result = restTemplate.getForEntity("/api/books?expand=true", List::class.java)

        assertThat(result.statusCode, `is`(HttpStatus.OK))
        assertThat(result.body!!.size, `is`(1))
        val book = result.body!![0] as Map<*, *>
        assertThat(book["templateType"], `is`("Book"))
        assertThat(book["booktype"], `is`("Book (Brown)"))
        assertThat(book["title"], `is`("Dungeon Survival Guide"))
        assertThat(book["pagename"], `is`("Dungeon Survival Guide (Book)"))
        assertThat(book["location"], `is`("[[Rookgaard Academy]]"))
        assertThat(book["blurb"], `is`("Tips for exploring dungeons, and warning against being reckless."))
        assertThat(book["returnpage"], `is`("Rookgaard Libraries"))
        assertThat(book["relatedpages"], `is`("[[Rope]], [[Shovel]]"))
        assertThat(
            book["text"],
            `is`("Dungeon Survival Guide<br><br>Don't explore the dungeons before you tested your skills in the training cellars of our academy. You will find dungeons somewhere in the wilderness. Don't enter dungeons without equipment. Especially a rope and a shovel will prove valuable. Make sure you have a supply of torches with you, while wandering into the unknown. It's wise to travel the dungeons in groups and not alone. For more help read all the books of the academy before you begin exploring. Traveling in the dungeons will reward the cautious and brave, but punish the reckless.")
        )
    }

    @Test
    fun givenGetBooksByName_whenCorrectRequest_thenResponseIsOkAndContainsTheBook() {
        doReturn(INFOBOX_BOOK_TEXT).`when`(articleRepository).getArticle("Dungeon Survival Guide (Book)")

        val result = restTemplate.getForEntity("/api/books/Dungeon Survival Guide (Book)", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.OK))

        val resultAsJSON = JSONObject(result.body)
        assertThat(resultAsJSON.get("templateType"), `is`("Book"))
        assertThat(resultAsJSON.get("booktype"), `is`("Book (Brown)"))
        assertThat(resultAsJSON.get("title"), `is`("Dungeon Survival Guide"))
        assertThat(resultAsJSON.get("pagename"), `is`("Dungeon Survival Guide (Book)"))
        assertThat(resultAsJSON.get("location"), `is`("[[Rookgaard Academy]]"))
        assertThat(resultAsJSON.get("blurb"), `is`("Tips for exploring dungeons, and warning against being reckless."))
        assertThat(resultAsJSON.get("returnpage"), `is`("Rookgaard Libraries"))
        assertThat(resultAsJSON.get("relatedpages"), `is`("[[Rope]], [[Shovel]]"))
        assertThat(
            resultAsJSON.get("text"),
            `is`("Dungeon Survival Guide<br><br>Don't explore the dungeons before you tested your skills in the training cellars of our academy. You will find dungeons somewhere in the wilderness. Don't enter dungeons without equipment. Especially a rope and a shovel will prove valuable. Make sure you have a supply of torches with you, while wandering into the unknown. It's wise to travel the dungeons in groups and not alone. For more help read all the books of the academy before you begin exploring. Traveling in the dungeons will reward the cautious and brave, but punish the reckless.")
        )
    }

    @Test
    fun givenGetBooksByName_whenWrongRequest_thenResponseIsNotFound() {
        doReturn(null).`when`(articleRepository).getArticle("Foobar")

        val result = restTemplate.getForEntity("/api/books/Foobar", String::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.NOT_FOUND))
    }

    @Test
    fun givenPutBook_whenCorrectRequest_thenResponseIsOkAndContainsTheModifiedBook() {
        val editSummary = "[bot] editing during integration test"

        doReturn(INFOBOX_BOOK_TEXT).`when`(articleRepository).getArticle("Dungeon Survival Guide (Book)")
        doReturn(true).`when`(articleRepository).modifyArticle(anyString(), anyString(), anyString())

        val result = restTemplate.exchange("/api/books", HttpMethod.PUT, HttpEntity(makeBook(), makeHttpHeaders(editSummary)), Void::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.OK))
    }

    @Test
    fun givenPutBook_whenCorrectRequestButUnableToEditWiki_thenResponseIsBadRequest() {
        val editSummary = "[bot] editing during integration test"

        doReturn(INFOBOX_BOOK_TEXT).`when`(articleRepository).getArticle("Dungeon Survival Guide (Book)")
        doReturn(false).`when`(articleRepository).modifyArticle(anyString(), anyString(), anyString())

        val result = restTemplate.exchange("/api/books", HttpMethod.PUT, HttpEntity(makeBook(), makeHttpHeaders(editSummary)), Void::class.java)
        assertThat(result.statusCode, `is`(HttpStatus.BAD_REQUEST))
    }

    private fun makeBook(): Book = WikiObjectFixtures.book()

    companion object {
        private val INFOBOX_BOOK_TEXT =
            """
            {{Infobox Book|List={{{1|}}}|GetValue={{{GetValue|}}}
            | booktype     = Book (Brown)
            | title        = Dungeon Survival Guide
            | pagename     = Dungeon Survival Guide (Book)
            | location     = [[Rookgaard Academy]]
            | blurb        = Tips for exploring dungeons, and warning against being reckless.
            | returnpage   = Rookgaard Libraries
            | relatedpages = [[Rope]], [[Shovel]]
            | text         = Dungeon Survival Guide<br><br>Don't explore the dungeons before you tested your skills in the training cellars of our academy. You will find dungeons somewhere in the wilderness. Don't enter dungeons without equipment. Especially a rope and a shovel will prove valuable. Make sure you have a supply of torches with you, while wandering into the unknown. It's wise to travel the dungeons in groups and not alone. For more help read all the books of the academy before you begin exploring. Traveling in the dungeons will reward the cautious and brave, but punish the reckless.
            }}
            """.trimIndent()
    }
}
