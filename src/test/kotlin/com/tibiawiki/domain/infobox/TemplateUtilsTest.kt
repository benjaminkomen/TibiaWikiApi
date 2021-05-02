package com.tibiawiki.domain.infobox

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class TemplateUtilsTest {

    @Test
    fun testRemoveFirstAndLastLine() {
        assertEquals("", TemplateUtils.removeFirstAndLastLine(null))
        assertEquals("", TemplateUtils.removeFirstAndLastLine(""))
        assertEquals("bar\nbaz\n", TemplateUtils.removeFirstAndLastLine("foo\nbar\nbaz\n}}foo"))
        assertEquals(
            """| grade        = 1
| name         = Goo Goo Dancer
| description  = Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!
| spoiler      = Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.
| premium      = yes
| points       = 1
| secret       = yes
| implemented  = 9.6
| achievementid = 319
| relatedpages = [[Muck Remover]], [[Mucus Plug]]
""",
            TemplateUtils.removeFirstAndLastLine(INFOBOX__ACHIEVEMENT_TEXT)
        )
    }

    @Test
    fun testSplitByParameter_EmptyOrNull() {
        assertEquals(0, TemplateUtils.splitInfoboxByParameter(null).size)
        assertEquals(0, TemplateUtils.splitInfoboxByParameter("").size)
    }

    @Test
    fun testSplitByParameter_InfoboxAchievement() {
        val input = TemplateUtils.removeFirstAndLastLine(INFOBOX__ACHIEVEMENT_TEXT)
        val result: Map<String, String?> = TemplateUtils.splitInfoboxByParameter(input)
        assertEquals("1", result["grade"])
        assertEquals("Goo Goo Dancer", result["name"])
        assertEquals(
            "Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!",
            result["description"]
        )
        assertEquals(
            "Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.",
            result["spoiler"]
        )
        assertEquals("yes", result["premium"])
        assertEquals("1", result["points"])
        assertEquals("yes", result["secret"])
        assertEquals("9.6", result["implemented"])
        assertEquals("319", result["achievementid"])
        assertEquals("[[Muck Remover]], [[Mucus Plug]]", result["relatedpages"])
    }

    @Test
    fun testSplitByCommaAndTrim() {
        assertEquals(emptyList<String>(), TemplateUtils.splitByCommaAndTrim(null))
        assertEquals(0, TemplateUtils.splitByCommaAndTrim(" ").size)

        val result1 = TemplateUtils.splitByCommaAndTrim("foo,bar")
        assertEquals(2, result1.size)
        assertEquals("foo", result1[0])
        assertEquals("bar", result1[1])

        val result2 = TemplateUtils.splitByCommaAndTrim("  this , is, a   , weird, list ")
        assertEquals(5, result2.size)
        assertEquals("this", result2[0])
        assertEquals("is", result2[1])
        assertEquals("a", result2[2])
        assertEquals("weird", result2[3])
        assertEquals("list", result2[4])
    }

    @Test
    fun testExtractLowerLevels_Empty() {
        assertNull(TemplateUtils.extractLowerLevels(null))
        assertNull(TemplateUtils.extractLowerLevels(""))
        assertNull(TemplateUtils.extractLowerLevels(INPUT_EMPTY_LOWERLEVELS))
    }

    @Test
    fun testExtractLowerLevels_MatchOne() {
        val result = TemplateUtils.extractLowerLevels(INPUT_ONE_LOWER_LEVEL)
        assertNotNull(result)
        assertTrue(result?.get("lowerlevels")?.contains("Demons") ?: false)
    }

    @Test
    fun testExtractLowerLevels_MatchMultiple() {
        val result = TemplateUtils.extractLowerLevels(INPUT_MULTIPLE_LOWER_LEVELS)
        assertNotNull(result)
        assertTrue(result?.get("lowerlevels")?.contains("Demons") ?: false)
        assertTrue(result?.get("lowerlevels")?.contains("Demons2") ?: false)
        assertTrue(result?.get("lowerlevels")?.contains("Demons3") ?: false)
    }

    @Test
    fun testRemoveLowerLevels_Empty() {
        assertEquals(INPUT_EMPTY_LOWERLEVELS, TemplateUtils.removeLowerLevels(INPUT_EMPTY_LOWERLEVELS))
    }

    @Test
    fun testRemoveLowerLevels_One() {
        val result = TemplateUtils.removeLowerLevels(INPUT_ONE_LOWER_LEVEL)
        assertNotEquals(INPUT_ONE_LOWER_LEVEL, result)
        assertFalse(result.contains("lowerlevels"))
    }

    @Test
    fun testRemoveLowerLevels_Multiple() {
        val result = TemplateUtils.removeLowerLevels(INPUT_MULTIPLE_LOWER_LEVELS)
        assertNotEquals(INPUT_MULTIPLE_LOWER_LEVELS, result)
        assertFalse(result.contains("lowerlevels"))
    }

    private val INFOBOX__ACHIEVEMENT_TEXT = """{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
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
}}"""

    private val INPUT_EMPTY_LOWERLEVELS = """| name            = Hero Cave
| image           = Hero
| implemented     = 6.4
| city            = Edron
| location        = North of [[Edron]], [http://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].
| vocation        = All vocations.
| lvlknights      = 70
| lvlpaladins     = 60
| lvlmages        = 50
| skknights       = 75
| skpaladins      = 80
| skmages         = 
| defknights      = 75
| defpaladins     = 
| defmages        =
| lowerlevels     = 
| exp             = Good
| loot            = Good
| bestloot        = Reins
| bestloot2       = 
| bestloot3       = 
| bestloot4       = 
| bestloot5       = 
| map             = Hero Cave 3.png
| map2            = Hero Cave 6.png
"""

    private val INPUT_ONE_LOWER_LEVEL = """| name            = Hero Cave
| image           = Hero
| implemented     = 6.4
| city            = Edron
| location        = North of [[Edron]], [http://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].
| vocation        = All vocations.
| lvlknights      = 70
| lvlpaladins     = 60
| lvlmages        = 50
| skknights       = 75
| skpaladins      = 80
| skmages         = 
| defknights      = 75
| defpaladins     = 
| defmages        =
| lowerlevels     = 
    {{Infobox Hunt Skills
    | areaname        = Demons
    | lvlknights      = 130
    | lvlpaladins     = 130
    | lvlmages        = 130
    | skknights       = 
    | skpaladins      = 
    | skmages         = 
    | defknights      = 
    | defpaladins     =
    | defmages        =
    }}
| exp             = Good
| loot            = Good
| bestloot        = Reins
| bestloot2       = 
| bestloot3       = 
| bestloot4       = 
| bestloot5       = 
| map             = Hero Cave 3.png
| map2            = Hero Cave 6.png
"""

    private val INPUT_MULTIPLE_LOWER_LEVELS = """| name            = Hero Cave
| image           = Hero
| implemented     = 6.4
| city            = Edron
| location        = North of [[Edron]], [http://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].
| vocation        = All vocations.
| lvlknights      = 70
| lvlpaladins     = 60
| lvlmages        = 50
| skknights       = 75
| skpaladins      = 80
| skmages         = 
| defknights      = 75
| defpaladins     = 
| defmages        =
| lowerlevels     = 
    {{Infobox Hunt Skills
    | areaname        = Demons
    | lvlknights      = 130
    | lvlpaladins     = 130
    | lvlmages        = 130
    | skknights       = 
    | skpaladins      = 
    | skmages         = 
    | defknights      = 
    | defpaladins     =
    | defmages        =
    }}
    {{Infobox Hunt Skills
    | areaname        = Demons2
    | lvlknights      = 139
    | lvlpaladins     = 139
    | lvlmages        = 139
    | skknights       = 
    | skpaladins      = 
    | skmages         = 
    | defknights      = 
    | defpaladins     =
    | defmages        =
    }}
    {{Infobox Hunt Skills
    | areaname        = Demons3
    | lvlknights      = 149
    | lvlpaladins     = 149
    | lvlmages        = 149
    | skknights       = 
    | skpaladins      = 
    | skmages         = 
    | defknights      = 
    | defpaladins     =
    | defmages        =
    }}
| exp             = Good
| loot            = Good
| bestloot        = Reins
| bestloot2       = 
| bestloot3       = 
| bestloot4       = 
| bestloot5       = 
| map             = Hero Cave 3.png
| map2            = Hero Cave 6.png
"""
}