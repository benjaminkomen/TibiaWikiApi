package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.repositories.ArticleRepository
import com.tibiawiki.process.RetrieveAny
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.doReturn
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.resttestclient.getForEntity
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class CategoryCollectionControllersIT(
    @Autowired private val restTemplate: TestRestTemplate
) {

    @MockitoBean
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun imbuementsListExpandAndDetail() {
        assertCollection(
            path = "/api/imbuements",
            template = InfoboxTemplate.IMBUEMENT,
            pageName = "Powerful Strike",
            infobox = INFOBOX_IMBUEMENT
        )
    }

    @Test
    fun updatesListExpandAndDetail() {
        assertCollection(
            path = "/api/updates",
            template = InfoboxTemplate.UPDATE,
            pageName = "Summer Update 2020",
            infobox = INFOBOX_UPDATE
        )
    }

    @Test
    fun worldsListExpandAndDetail() {
        assertCollection(
            path = "/api/worlds",
            template = InfoboxTemplate.WORLD,
            pageName = "Antica",
            infobox = INFOBOX_WORLD
        )
    }

    @Test
    fun familiarsListExpandAndDetail() {
        assertCollection(
            path = "/api/familiars",
            template = InfoboxTemplate.FAMILIAR,
            pageName = "Grovebeast",
            infobox = INFOBOX_FAMILIAR
        )
    }

    @Test
    fun fansitesListExpandAndDetail() {
        assertCollection(
            path = "/api/fansites",
            template = InfoboxTemplate.FANSITE,
            pageName = "TibiaWiki",
            infobox = INFOBOX_FANSITE
        )
    }

    @Test
    fun cipsoftMembersListExpandAndDetail() {
        assertCollection(
            path = "/api/cipsoftmembers",
            template = InfoboxTemplate.CIPSOFT_MEMBER,
            pageName = "Knightmare",
            infobox = INFOBOX_CIPSOFT_MEMBER
        )
    }

    private fun assertCollection(
        path: String,
        template: InfoboxTemplate,
        pageName: String,
        infobox: String
    ) {
        doReturn(listOf("baz")).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(listOf("foo", "bar", "baz")).`when`(articleRepository).getPageNamesFromCategory(template.categoryName)

        val names = restTemplate.getForEntity<List<String>>("$path?expand=false")
        assertEquals(HttpStatus.OK, names.statusCode)
        assertEquals(listOf("foo", "bar"), names.body)

        doReturn(emptyList<String>()).`when`(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(listOf(pageName)).`when`(articleRepository).getPageNamesFromCategory(template.categoryName)
        doReturn(mapOf(pageName to infobox)).`when`(articleRepository).getArticlesFromCategory(listOf(pageName))

        val expanded = restTemplate.getForEntity<List<Map<String, Any>>>("$path?expand=true")
        assertEquals(HttpStatus.OK, expanded.statusCode)
        assertEquals(1, expanded.body?.size)
        val expandedJson = JSONObject(expanded.body?.get(0))
        assertEquals(template.templateName, expandedJson["templateType"])
        assertEquals(pageName, expandedJson["name"])

        doReturn(infobox).`when`(articleRepository).getArticle(pageName)
        val detail = restTemplate.getForEntity<String>("$path/$pageName")
        assertEquals(HttpStatus.OK, detail.statusCode)
        val detailJson = JSONObject(detail.body)
        assertEquals(template.templateName, detailJson["templateType"])
        assertEquals(pageName, detailJson["name"])

        doReturn(null).`when`(articleRepository).getArticle("Foobar")
        val missing = restTemplate.getForEntity<String>("$path/Foobar")
        assertEquals(HttpStatus.NOT_FOUND, missing.statusCode)
    }

    companion object {
        private val INFOBOX_IMBUEMENT =
            """
            {{Infobox Imbuement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Powerful Strike
            | prefix       = Powerful
            | type         = Strike
            | implemented  = 11.50
            }}
            """.trimIndent()
        private val INFOBOX_UPDATE =
            """
            {{Infobox Update|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Summer Update 2020
            | implemented  = 12.40
            | date         = July 13, 2020
            }}
            """.trimIndent()
        private val INFOBOX_WORLD =
            """
            {{Infobox World|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Antica
            | location     = Europe
            | pvpType      = Open PvP
            | implemented  = 7.0
            }}
            """.trimIndent()
        private val INFOBOX_FAMILIAR =
            """
            {{Infobox Familiar|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Grovebeast
            | implemented  = 11.40
            }}
            """.trimIndent()
        private val INFOBOX_FANSITE =
            """
            {{Infobox Fansite|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = TibiaWiki
            | url          = https://tibia.fandom.com
            | type         = Official
            }}
            """.trimIndent()
        private val INFOBOX_CIPSOFT_MEMBER =
            """
            {{Infobox Cipsoft_Member|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Knightmare
            | actualname   = Stephan
            | job          = Content Designer
            }}
            """.trimIndent()
    }
}
