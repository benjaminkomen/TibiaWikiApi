package com.tibiawiki.domain.objects

import com.tibiawiki.domain.objects.validation.ValidationSeverity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class WikiObjectValidationTest {

    @Test
    fun validateRequiresName() {
        val missing = WikiObjectFixtures.achievement(name = null)
        val blank = WikiObjectFixtures.achievement(name = "  ")

        assertThat(descriptions(missing), hasItem(WikiObject.NAME_REQUIRED))
        assertThat(descriptions(blank), hasItem(WikiObject.NAME_REQUIRED))
        assertThat(missing.validate()[0].severity, `is`(ValidationSeverity.ERROR))
    }

    @Test
    fun validateAcceptsNamedAchievement() {
        assertThat(WikiObjectFixtures.achievement().validate(), empty())
    }

    @Test
    fun validateRejectsUnknownTemplateType() {
        val unknown = WikiObject.WikiObjectImpl()

        assertThat(
            descriptions(unknown),
            hasItems(WikiObject.NAME_REQUIRED, WikiObject.UNKNOWN_TEMPLATE_TYPE + "WikiObjectImpl")
        )
    }

    @Test
    fun keyUsesNumberAsArticleTitle() {
        val key = WikiObjectFixtures.key()
        val missingNumber = Key()

        assertThat(key.articleTitle(), `is`("Key 4055"))
        assertThat(key.validate(), empty())
        assertThat(Key(number = "Key 0001").articleTitle(), `is`("Key 0001"))
        assertThat(descriptions(missingNumber), hasItem(WikiObject.NAME_REQUIRED))
    }

    @Test
    fun bookUsesPagenameAsName() {
        val book = WikiObjectFixtures.book()

        assertThat(book.articleTitle(), `is`("Dungeon Survival Guide (Book)"))
        assertThat(book.validate(), empty())
    }

    private fun descriptions(wikiObject: WikiObject): List<String?> {
        return wikiObject.validate().map { it.description }
    }
}
