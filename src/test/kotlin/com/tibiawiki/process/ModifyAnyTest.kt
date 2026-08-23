package com.tibiawiki.process

import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.factories.WikiObjectFactory
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.WikiObjectFixtures
import com.tibiawiki.domain.repositories.ArticleRepository
import io.vavr.control.Try
import org.hamcrest.MatcherAssert.assertThat
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import java.util.Optional

class ModifyAnyTest {

    private lateinit var target: ModifyAny
    private lateinit var wikiObjectFactory: WikiObjectFactory
    private lateinit var jsonFactory: JsonFactory
    private lateinit var articleFactory: ArticleFactory
    private lateinit var articleRepository: ArticleRepository

    @BeforeEach
    fun setup() {
        wikiObjectFactory = mock(WikiObjectFactory::class.java)
        jsonFactory = mock(JsonFactory::class.java)
        articleFactory = mock(ArticleFactory::class.java)
        articleRepository = mock(ArticleRepository::class.java)
        target = ModifyAny(wikiObjectFactory, jsonFactory, articleFactory, articleRepository)
    }

    @Test
    fun testModify_Success() {
        val someAchievement = makeAchievement()
        val name = someAchievement.name.orEmpty()
        doReturn("").`when`(articleRepository).getArticle(name)
        doReturn(SOME_JSON_OBJECT).`when`(wikiObjectFactory).createJSONObject(someAchievement, someAchievement.getTemplateType())
        doReturn("").`when`(jsonFactory).convertJsonToInfoboxPartOfArticle(SOME_JSON_OBJECT, someAchievement.fieldOrder())
        doReturn(Optional.of("")).`when`(articleFactory).insertInfoboxPartOfArticle("", "")
        doReturn(true).`when`(articleRepository).modifyArticle(name, "", "[test] editing the page")

        val result: Try<WikiObject> = target.modify(someAchievement, "[test] editing the page")

        assertThat("Test: successfully modified article", result.isSuccess)
    }

    @Test
    fun testModify_CharmUsesName() {
        val charm = WikiObjectFixtures.charm()
        val name = charm.name.orEmpty()
        doReturn("").`when`(articleRepository).getArticle(name)
        doReturn(SOME_JSON_OBJECT).`when`(wikiObjectFactory).createJSONObject(charm, charm.getTemplateType())
        doReturn("").`when`(jsonFactory).convertJsonToInfoboxPartOfArticle(SOME_JSON_OBJECT, charm.fieldOrder())
        doReturn(Optional.of("")).`when`(articleFactory).insertInfoboxPartOfArticle("", "")
        doReturn(true).`when`(articleRepository).modifyArticle(name, "", "[test] editing the page")

        val result: Try<WikiObject> = target.modify(charm, "[test] editing the page")

        assertThat("Test: successfully modified charm using populated name", result.isSuccess)
    }

    @Test
    fun testModify_Failure() {
        val someAchievement = makeAchievement()
        val name = someAchievement.name.orEmpty()
        doReturn("").`when`(articleRepository).getArticle(name)
        doReturn(SOME_JSON_OBJECT).`when`(wikiObjectFactory).createJSONObject(someAchievement, someAchievement.getTemplateType())
        doReturn("").`when`(jsonFactory).convertJsonToInfoboxPartOfArticle(SOME_JSON_OBJECT, someAchievement.fieldOrder())
        doReturn(Optional.empty<String>()).`when`(articleFactory).insertInfoboxPartOfArticle("", "")

        val result: Try<WikiObject> = target.modify(someAchievement, "[test] editing the page")

        assertThat("Test: failed to modify article", result.isFailure)
    }

    private fun makeAchievement(): WikiObject = WikiObjectFixtures.achievement()

    companion object {
        private val SOME_JSON_OBJECT = JSONObject()
    }
}
