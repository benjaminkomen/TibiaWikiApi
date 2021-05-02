package com.tibiawiki.adapters.rest

import com.tibiawiki.TestUtils
import com.tibiawiki.domain.ArticleRepository
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.YesNo
import com.tibiawiki.domain.objects.Achievement
import com.tibiawiki.process.RetrieveAny
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.doReturn
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AchievementsControllerIT(
    @Autowired private val restTemplate: TestRestTemplate
) {

    @MockBean
    private lateinit var articleRepository: ArticleRepository

    @Test
    fun `given get achievements not expanded when correct request then response is ok and contains two achievement names`() {
        doReturn(listOf("baz")).`when`<ArticleRepository>(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(listOf("foo", "bar", "baz")).`when`<ArticleRepository>(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ACHIEVEMENT.categoryName)

        val result = restTemplate.getForEntity<List<String>>("/api/achievements?expand=false")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(2, result.body?.size)
        assertEquals("foo", result.body?.get(0))
        assertEquals("bar", result.body?.get(1))
    }

    @Test
    fun `given get achievements expanded when correct request then response is ok and contains one achievement`() {
        doReturn(emptyList<Any>()).`when`<ArticleRepository>(articleRepository).getPageNamesFromCategory(RetrieveAny.CATEGORY_LISTS)
        doReturn(listOf(SOME_ACHIEVEMENT_NAME))
            .`when`<ArticleRepository>(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ACHIEVEMENT.categoryName)
        doReturn(mapOf(SOME_ACHIEVEMENT_NAME to INFOBOX_ACHIEVEMENT_TEXT))
            .`when`<ArticleRepository>(articleRepository).getArticlesFromCategory(listOf(SOME_ACHIEVEMENT_NAME))

        val result = restTemplate.getForEntity<List<Map<String, Any>>>("/api/achievements?expand=true")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(1, result.body?.size)

        val resultAsJSON = JSONObject(result.body?.get(0))

        assertEquals("Achievement", resultAsJSON["templateType"])
        assertEquals("yes", resultAsJSON["premium"])
        assertEquals("[[Muck Remover]], [[Mucus Plug]]", resultAsJSON["relatedpages"])
        assertEquals("1", resultAsJSON["grade"])
        assertEquals(SOME_ACHIEVEMENT_NAME, resultAsJSON["name"])
        assertEquals("9.6", resultAsJSON["implemented"])
        assertEquals("Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!", resultAsJSON["description"])
        assertEquals("319", resultAsJSON["achievementid"])
        assertEquals("Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.", resultAsJSON["spoiler"])
        assertEquals("yes", resultAsJSON["secret"])
        assertEquals("1", resultAsJSON["points"])
    }

    @Test
    fun `given get achievements by name when correct request then response is ok and contains the achievement`() {
        doReturn(INFOBOX_ACHIEVEMENT_TEXT).`when`<ArticleRepository>(articleRepository).getArticle(SOME_ACHIEVEMENT_NAME)

        val result = restTemplate.getForEntity<String>("/api/achievements/Goo Goo Dancer")

        assertEquals(HttpStatus.OK, result.statusCode)
        val resultAsJSON = JSONObject(result.body)
        assertEquals("Achievement", resultAsJSON["templateType"])
        assertEquals("yes", resultAsJSON["premium"])
        assertEquals("[[Muck Remover]], [[Mucus Plug]]", resultAsJSON["relatedpages"])
        assertEquals("1", resultAsJSON["grade"])
        assertEquals(SOME_ACHIEVEMENT_NAME, resultAsJSON["name"])
        assertEquals("9.6", resultAsJSON["implemented"])
        assertEquals("Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!", resultAsJSON["description"])
        assertEquals("319", resultAsJSON["achievementid"])
        assertEquals("Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.", resultAsJSON["spoiler"])
        assertEquals("yes", resultAsJSON["secret"])
        assertEquals("1", resultAsJSON["points"])
    }

    @Test
    fun `given get achievements by name when wrong request then response Is Not Found`() {
        doReturn(null).`when`<ArticleRepository>(articleRepository).getArticle("Foobar")

        val result = restTemplate.getForEntity<String>("/api/achievements/Foobar")

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    }

    @Test
    fun `given put achievement when correct request then response Is Ok And Contains The Modified Achievement`() {
        val editSummary = "[bot] editing during integration test"
        doReturn(INFOBOX_ACHIEVEMENT_TEXT).`when`<ArticleRepository>(articleRepository).getArticle(SOME_ACHIEVEMENT_NAME)
        doReturn(true).`when`<ArticleRepository>(articleRepository).modifyArticle(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        val httpHeaders = TestUtils.makeHttpHeaders(editSummary)

        val result = restTemplate.exchange("/api/achievements", HttpMethod.PUT, HttpEntity(makeAchievement(), httpHeaders), Void::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
    }

    @Test
    fun `given put achievement when correct request but unable to Edit Wiki then response is BadRequest`() {
        val editSummary = "[bot] editing during integration test"
        doReturn(INFOBOX_ACHIEVEMENT_TEXT).`when`<ArticleRepository>(articleRepository).getArticle(SOME_ACHIEVEMENT_NAME)
        doReturn(false).`when`<ArticleRepository>(articleRepository).modifyArticle(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        val httpHeaders = TestUtils.makeHttpHeaders(editSummary)

        val result = restTemplate.exchange("/api/achievements", HttpMethod.PUT, HttpEntity(makeAchievement(), httpHeaders), Void::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    private fun makeAchievement(): Achievement {
        return Achievement.builder()
            .grade(1)
            .name(SOME_ACHIEVEMENT_NAME)
            .description("Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!")
            .spoiler("Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.")
            .premium(YesNo.YES_LOWERCASE)
            .points(1)
            .secret(YesNo.YES_LOWERCASE)
            .implemented("9.6")
            .achievementid(319)
            .relatedpages("[[Muck Remover]], [[Mucus Plug]]")
            .build()
    }

    companion object {
        private const val SOME_ACHIEVEMENT_NAME = "Goo Goo Dancer"
        private val INFOBOX_ACHIEVEMENT_TEXT =
            """
            {{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | grade        = 1
            | name         = Goo Goo Dancer
            | description  = Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!
            | spoiler      = Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.
            | premium      = yes
            | points       = 1
            | secret       = yes
            | implemented  = 9.6
            | achievementid = 319
            | relatedpages = [[Muck Remover]], [[Mucus Plug]]
            }}
            """.trimIndent()
    }
}
