package com.tibiawiki.process

import com.tibiawiki.domain.ArticleNotFoundException
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.factories.WikiObjectFactory
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.WikiObjectFixtures
import com.tibiawiki.domain.objects.validation.ValidationException
import com.tibiawiki.domain.objects.validation.ValidationResult
import com.tibiawiki.domain.repositories.ArticleRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
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

        val result = target.modify(someAchievement, "[test] editing the page")

        assertThat("Test: successfully modified article", result, instanceOf(ModifyResult.Success::class.java))
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

        val result = target.modify(charm, "[test] editing the page")

        assertThat("Test: successfully modified charm using populated name", result, instanceOf(ModifyResult.Success::class.java))
    }

    @Test
    fun testModify_Failure() {
        val someAchievement = makeAchievement()
        val name = someAchievement.name.orEmpty()
        doReturn("").`when`(articleRepository).getArticle(name)
        doReturn(SOME_JSON_OBJECT).`when`(wikiObjectFactory).createJSONObject(someAchievement, someAchievement.getTemplateType())
        doReturn("").`when`(jsonFactory).convertJsonToInfoboxPartOfArticle(SOME_JSON_OBJECT, someAchievement.fieldOrder())
        doReturn(Optional.empty<String>()).`when`(articleFactory).insertInfoboxPartOfArticle("", "")

        val result = target.modify(someAchievement, "[test] editing the page")

        assertThat("Test: failed to modify article", result, instanceOf(ModifyResult.Failure::class.java))
        assertThat((result as ModifyResult.Failure).cause, instanceOf(IllegalArgumentException::class.java))
    }

    @Test
    fun testModify_EditRejected() {
        val someAchievement = makeAchievement()
        val name = someAchievement.name.orEmpty()
        doReturn("").`when`(articleRepository).getArticle(name)
        doReturn(SOME_JSON_OBJECT).`when`(wikiObjectFactory).createJSONObject(someAchievement, someAchievement.getTemplateType())
        doReturn("").`when`(jsonFactory).convertJsonToInfoboxPartOfArticle(SOME_JSON_OBJECT, someAchievement.fieldOrder())
        doReturn(Optional.of("")).`when`(articleFactory).insertInfoboxPartOfArticle("", "")
        doReturn(false).`when`(articleRepository).modifyArticle(name, "", "[test] editing the page")

        val result = target.modify(someAchievement, "[test] editing the page")

        assertThat(result, instanceOf(ModifyResult.Failure::class.java))
        assertThat((result as ModifyResult.Failure).cause, instanceOf(ValidationException::class.java))
    }

    @Test
    fun testModify_ValidationFailure() {
        val unnamed = WikiObjectFixtures.achievement(name = null)

        val result = target.modify(unnamed, "[test] editing the page")

        assertThat(result, instanceOf(ModifyResult.Failure::class.java))
        assertThat((result as ModifyResult.Failure).cause, instanceOf(ValidationException::class.java))
        verify(articleRepository, never()).getArticle(anyString())
    }

    @Test
    fun testModify_ArticleMissing() {
        val someAchievement = makeAchievement()
        doReturn(null).`when`(articleRepository).getArticle(someAchievement.name.orEmpty())

        val result = target.modify(someAchievement, "[test] editing the page")

        assertThat(result, instanceOf(ModifyResult.Failure::class.java))
        assertThat((result as ModifyResult.Failure).cause, instanceOf(ArticleNotFoundException::class.java))
    }

    @Test
    fun testModify_InvalidWikiObject() {
        val result = target.modify(InvalidWikiObject(), "[test] editing the page")

        assertThat(result, instanceOf(ModifyResult.Failure::class.java))
        assertThat((result as ModifyResult.Failure).cause, instanceOf(ValidationException::class.java))
    }

    private fun makeAchievement(): WikiObject = WikiObjectFixtures.achievement()

    private class InvalidWikiObject : WikiObject(name = "X") {
        override fun fieldOrder(): List<String> = emptyList()
        override fun getTemplateType(): String = "x"
        override fun validate(): List<ValidationResult> = listOf(ValidationResult(description = "bad"))
    }

    companion object {
        private val SOME_JSON_OBJECT = emptyMap<String, Any>()
    }
}
