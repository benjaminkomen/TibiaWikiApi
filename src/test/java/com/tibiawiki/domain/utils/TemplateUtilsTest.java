package com.tibiawiki.domain.utils;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class TemplateUtilsTest {

    @Test
    void testRemoveFirstAndLastLine() {
        assertThat(TemplateUtils.removeFirstAndLastLine(null), is(""));
        assertThat(TemplateUtils.removeFirstAndLastLine(""), is(""));
        assertThat(TemplateUtils.removeFirstAndLastLine("foo\nbar\nbaz\n}}foo"), is("bar\nbaz\n"));
        assertThat(TemplateUtils.removeFirstAndLastLine(INFOBOX__ACHIEVEMENT_TEXT), is("""
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
                """));
    }

    @Test
    void testSplitByParameter_EmptyOrNull() {
        assertThat(TemplateUtils.splitInfoboxByParameter(null).size(), is(0));
        assertThat(TemplateUtils.splitInfoboxByParameter("").size(), is(0));
    }

    @Test
    void testSplitByParameter_InfoboxAchievement() {
        String input = TemplateUtils.removeFirstAndLastLine(INFOBOX__ACHIEVEMENT_TEXT);
        Map<String, String> result = TemplateUtils.splitInfoboxByParameter(input);

        assertThat(result.get("grade"), is("1"));
        assertThat(result.get("name"), is("Goo Goo Dancer"));
        assertThat(result.get("description"), is("Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!"));
        assertThat(result.get("spoiler"), is("Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s."));
        assertThat(result.get("premium"), is("yes"));
        assertThat(result.get("points"), is("1"));
        assertThat(result.get("secret"), is("yes"));
        assertThat(result.get("implemented"), is("9.6"));
        assertThat(result.get("achievementid"), is("319"));
        assertThat(result.get("relatedpages"), is("[[Muck Remover]], [[Mucus Plug]]"));
    }

    @Test
    void testSplitByCommaAndTrim() {
        assertThat(TemplateUtils.splitByCommaAndTrim(null), is(Collections.emptyList()));
        assertThat(TemplateUtils.splitByCommaAndTrim(" "), hasSize(0));

        List<String> result1 = TemplateUtils.splitByCommaAndTrim("foo,bar");
        assertThat(result1, hasSize(2));
        assertThat(result1.get(0), is("foo"));
        assertThat(result1.get(1), is("bar"));

        List<String> result2 = TemplateUtils.splitByCommaAndTrim("  this , is, a   , weird, list ");
        assertThat(result2, hasSize(5));
        assertThat(result2.get(0), is("this"));
        assertThat(result2.get(1), is("is"));
        assertThat(result2.get(2), is("a"));
        assertThat(result2.get(3), is("weird"));
        assertThat(result2.get(4), is("list"));
    }

    @Test
    void testExtractLowerLevels_Empty() {
        assertThat(TemplateUtils.extractLowerLevels(null), is(Optional.empty()));
        assertThat(TemplateUtils.extractLowerLevels(""), is(Optional.empty()));
        assertThat(TemplateUtils.extractLowerLevels(INPUT_EMPTY_LOWERLEVELS), is(Optional.empty()));
    }

    @Test
    void testExtractLowerLevels_MatchOne() {
        final Optional<Map<String, String>> result = TemplateUtils.extractLowerLevels(INPUT_ONE_LOWER_LEVEL);

        assertThat(result, not(Optional.empty()));
        assertThat("Test: result is correct", result.orElseThrow().get("lowerlevels").contains("Demons"));
    }

    @Test
    void testExtractLowerLevels_MatchMultiple() {
        final Optional<Map<String, String>> result = TemplateUtils.extractLowerLevels(INPUT_MULTIPLE_LOWER_LEVELS);

        assertThat(result, not(Optional.empty()));
        assertThat("Test: result is correct", result.orElseThrow().get("lowerlevels").contains("Demons"));
        assertThat("Test: result is correct", result.orElseThrow().get("lowerlevels").contains("Demons2"));
        assertThat("Test: result is correct", result.orElseThrow().get("lowerlevels").contains("Demons3"));
    }

    @Test
    void testRemoveLowerLevels_Empty() {
        assertThat(TemplateUtils.removeLowerLevels(INPUT_EMPTY_LOWERLEVELS), is(INPUT_EMPTY_LOWERLEVELS));
    }

    @Test
    void testRemoveLowerLevels_One() {
        final String result = TemplateUtils.removeLowerLevels(INPUT_ONE_LOWER_LEVEL);
        assertThat(result, not(INPUT_ONE_LOWER_LEVEL));
        assertThat(result, not(containsString("lowerlevels")));
    }

    @Test
    void testRemoveLowerLevels_Multiple() {
        final String result = TemplateUtils.removeLowerLevels(INPUT_MULTIPLE_LOWER_LEVELS);
        assertThat(result, not(INPUT_MULTIPLE_LOWER_LEVELS));
        assertThat(result, not(containsString("lowerlevels")));
    }

    private static final String INFOBOX__ACHIEVEMENT_TEXT = """
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
            }}""";

    private static final String INPUT_EMPTY_LOWERLEVELS = """
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
            """;

    private static final String INPUT_ONE_LOWER_LEVEL = """
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
            """;

    private static final String INPUT_MULTIPLE_LOWER_LEVELS = """
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
            """;

}