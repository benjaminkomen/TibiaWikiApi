package com.tibiawiki.domain.utils

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import java.util.Optional

class TemplateUtilsTest {

    @Test
    fun testRemoveFirstAndLastLine() {
        assertThat(TemplateUtils.removeFirstAndLastLine(null), `is`(""))
        assertThat(TemplateUtils.removeFirstAndLastLine(""), `is`(""))
        assertThat(TemplateUtils.removeFirstAndLastLine("foo\nbar\nbaz\n}}foo"), `is`("bar\nbaz\n"))
        assertThat(
            TemplateUtils.removeFirstAndLastLine(INFOBOX__ACHIEVEMENT_TEXT),
            `is`(
                """
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
                """.trimIndent().trimStart() + "\n"
            )
        )
    }

    @Test
    fun testSplitByParameter_EmptyOrNull() {
        assertThat(TemplateUtils.splitInfoboxByParameter(null).size, `is`(0))
        assertThat(TemplateUtils.splitInfoboxByParameter("").size, `is`(0))
    }

    @Test
    fun testSplitByParameter_InfoboxAchievement() {
        val input = TemplateUtils.removeFirstAndLastLine(INFOBOX__ACHIEVEMENT_TEXT)
        val result = TemplateUtils.splitInfoboxByParameter(input)

        assertThat(result["grade"], `is`("1"))
        assertThat(result["name"], `is`("Goo Goo Dancer"))
        assertThat(result["description"], `is`("Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!"))
        assertThat(result["spoiler"], `is`("Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s."))
        assertThat(result["premium"], `is`("yes"))
        assertThat(result["points"], `is`("1"))
        assertThat(result["secret"], `is`("yes"))
        assertThat(result["implemented"], `is`("9.6"))
        assertThat(result["achievementid"], `is`("319"))
        assertThat(result["relatedpages"], `is`("[[Muck Remover]], [[Mucus Plug]]"))
    }

    @Test
    fun testSplitByCommaAndTrim() {
        assertThat(TemplateUtils.splitByCommaAndTrim(null), `is`(emptyList()))
        assertThat(TemplateUtils.splitByCommaAndTrim(" "), hasSize(0))

        val result1 = TemplateUtils.splitByCommaAndTrim("foo,bar")
        assertThat(result1, hasSize(2))
        assertThat(result1[0], `is`("foo"))
        assertThat(result1[1], `is`("bar"))

        val result2 = TemplateUtils.splitByCommaAndTrim("  this , is, a   , weird, list ")
        assertThat(result2, hasSize(5))
        assertThat(result2[0], `is`("this"))
        assertThat(result2[1], `is`("is"))
        assertThat(result2[2], `is`("a"))
        assertThat(result2[3], `is`("weird"))
        assertThat(result2[4], `is`("list"))
    }

    @Test
    fun testExtractLowerLevels_Empty() {
        assertThat(TemplateUtils.extractLowerLevels(null), `is`(Optional.empty()))
        assertThat(TemplateUtils.extractLowerLevels(""), `is`(Optional.empty()))
        assertThat(TemplateUtils.extractLowerLevels(INPUT_EMPTY_LOWERLEVELS), `is`(Optional.empty()))
    }

    @Test
    fun testExtractLowerLevels_MatchOne() {
        val result = TemplateUtils.extractLowerLevels(INPUT_ONE_LOWER_LEVEL)

        assertThat(result, not(Optional.empty()))
        assertThat("Test: result is correct", result.orElseThrow()["lowerlevels"]!!.contains("Demons"))
    }

    @Test
    fun testExtractLowerLevels_MatchMultiple() {
        val result = TemplateUtils.extractLowerLevels(INPUT_MULTIPLE_LOWER_LEVELS)

        assertThat(result, not(Optional.empty()))
        assertThat("Test: result is correct", result.orElseThrow()["lowerlevels"]!!.contains("Demons"))
        assertThat("Test: result is correct", result.orElseThrow()["lowerlevels"]!!.contains("Demons2"))
        assertThat("Test: result is correct", result.orElseThrow()["lowerlevels"]!!.contains("Demons3"))
    }

    @Test
    fun testRemoveLowerLevels_Empty() {
        assertThat(TemplateUtils.removeLowerLevels(INPUT_EMPTY_LOWERLEVELS), `is`(INPUT_EMPTY_LOWERLEVELS))
    }

    @Test
    fun testRemoveLowerLevels_One() {
        val result = TemplateUtils.removeLowerLevels(INPUT_ONE_LOWER_LEVEL)
        assertThat(result, not(INPUT_ONE_LOWER_LEVEL))
        assertThat(result, not(containsString("lowerlevels")))
    }

    @Test
    fun testRemoveLowerLevels_Multiple() {
        val result = TemplateUtils.removeLowerLevels(INPUT_MULTIPLE_LOWER_LEVELS)
        assertThat(result, not(INPUT_MULTIPLE_LOWER_LEVELS))
        assertThat(result, not(containsString("lowerlevels")))
    }

    companion object {
        private val INFOBOX__ACHIEVEMENT_TEXT = """
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
            }}""".trimIndent().trimStart()
        private val INPUT_EMPTY_LOWERLEVELS = """
            | name            = Hero Cave
            | image           = Hero
            | implemented     = 6.4
            | city            = Edron
            | location        = North of [[Edron]], [https://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].
            | vocation        = All vocations.
            | lvlknights      = 70
            | lvlpaladins     = 60
            | lvlmages        = 50
            | skknights       = 75
            | skpaladins      = 80
            | skmages         =\s
            | defknights      = 75
            | defpaladins     =\s
            | defmages        =
            | lowerlevels     =\s
            | exp             = Good
            | loot            = Good
            | bestloot        = Reins
            | bestloot2       =\s
            | bestloot3       =\s
            | bestloot4       =\s
            | bestloot5       =\s
            | map             = Hero Cave 3.png
            | map2            = Hero Cave 6.png
            """.trimIndent().trimStart()
        private val INPUT_ONE_LOWER_LEVEL = """
            | name            = Hero Cave
            | image           = Hero
            | implemented     = 6.4
            | city            = Edron
            | location        = North of [[Edron]], [https://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].
            | vocation        = All vocations.
            | lvlknights      = 70
            | lvlpaladins     = 60
            | lvlmages        = 50
            | skknights       = 75
            | skpaladins      = 80
            | skmages         =\s
            | defknights      = 75
            | defpaladins     =\s
            | defmages        =
            | lowerlevels     =\s
                {{Infobox Hunt Skills
                | areaname        = Demons
                | lvlknights      = 130
                | lvlpaladins     = 130
                | lvlmages        = 130
                | skknights       =\s
                | skpaladins      =\s
                | skmages         =\s
                | defknights      =\s
                | defpaladins     =
                | defmages        =
                }}
            | exp             = Good
            | loot            = Good
            | bestloot        = Reins
            | bestloot2       =\s
            | bestloot3       =\s
            | bestloot4       =\s
            | bestloot5       =\s
            | map             = Hero Cave 3.png
            | map2            = Hero Cave 6.png
            """.trimIndent().trimStart()
        private val INPUT_MULTIPLE_LOWER_LEVELS = """
            | name            = Hero Cave
            | image           = Hero
            | implemented     = 6.4
            | city            = Edron
            | location        = North of [[Edron]], [https://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].
            | vocation        = All vocations.
            | lvlknights      = 70
            | lvlpaladins     = 60
            | lvlmages        = 50
            | skknights       = 75
            | skpaladins      = 80
            | skmages         =\s
            | defknights      = 75
            | defpaladins     =\s
            | defmages        =
            | lowerlevels     =\s
                {{Infobox Hunt Skills
                | areaname        = Demons
                | lvlknights      = 130
                | lvlpaladins     = 130
                | lvlmages        = 130
                | skknights       =\s
                | skpaladins      =\s
                | skmages         =\s
                | defknights      =\s
                | defpaladins     =
                | defmages        =
                }}
                {{Infobox Hunt Skills
                | areaname        = Demons2
                | lvlknights      = 139
                | lvlpaladins     = 139
                | lvlmages        = 139
                | skknights       =\s
                | skpaladins      =\s
                | skmages         =\s
                | defknights      =\s
                | defpaladins     =
                | defmages        =
                }}
                {{Infobox Hunt Skills
                | areaname        = Demons3
                | lvlknights      = 149
                | lvlpaladins     = 149
                | lvlmages        = 149
                | skknights       =\s
                | skpaladins      =\s
                | skmages         =\s
                | defknights      =\s
                | defpaladins     =
                | defmages        =
                }}
            | exp             = Good
            | loot            = Good
            | bestloot        = Reins
            | bestloot2       =\s
            | bestloot3       =\s
            | bestloot4       =\s
            | bestloot5       =\s
            | map             = Hero Cave 3.png
            | map2            = Hero Cave 6.png
            """.trimIndent().trimStart()
    }
}
