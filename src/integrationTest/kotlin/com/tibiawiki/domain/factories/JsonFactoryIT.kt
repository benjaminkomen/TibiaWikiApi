package com.tibiawiki.domain.factories

import com.tibiawiki.domain.objects.Achievement
import com.tibiawiki.domain.objects.WikiObjectFixtures
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * We want to be sure that the deserialization of a wiki article to a java object,
 * and serialisation back to the wiki article, do not cause unwanted side-effects in the article's content. Some effects
 * are however not side-effects, but completely legit:
 * - removal of undocumentend parameters;
 * - removal of empty parameters;
 * - ...
 */
class JsonFactoryIT {

    private lateinit var target: JsonFactory

    @BeforeEach
    fun setup() {
        target = JsonFactory()
    }

    @Test
    fun testDeserializeAndSerialiseSomeAchievement() {
        val achievementAsJson = target.convertInfoboxPartOfArticleToJson(INFOBOX_ACHIEVEMENT_TEXT)
        val result = target.convertJsonToInfoboxPartOfArticle(achievementAsJson, makeAchievement().fieldOrder())
        assertThat(result, `is`(INFOBOX_ACHIEVEMENT_TEXT))
    }

    private fun makeAchievement(): Achievement = WikiObjectFixtures.achievement()

    companion object {
        private val INFOBOX_ACHIEVEMENT_TEXT =
            """
            {{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | grade         = 1
            | name          = Goo Goo Dancer
            | description   = Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!
            | spoiler       = Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.
            | premium       = yes
            | points        = 1
            | secret        = yes
            | implemented   = 9.6
            | achievementid = 319
            | relatedpages  = [[Muck Remover]], [[Mucus Plug]]
            }}
            """.trimIndent() + "\n"
    }
}
