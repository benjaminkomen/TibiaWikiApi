package com.tibiawiki.domain.utils;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TemplateUtilsTest {

    @Test
    public void testRemoveFirstAndLastLine() {
        assertThat(TemplateUtils.removeFirstAndLastLine(null), is(""));
        assertThat(TemplateUtils.removeFirstAndLastLine(""), is(""));
        assertThat(TemplateUtils.removeFirstAndLastLine("foo\nbar\nbaz\n}}foo"), is("bar\nbaz\n"));
        assertThat(TemplateUtils.removeFirstAndLastLine(INFOBOX__ACHIEVEMENT_TEXT), is("| grade        = 1\n" +
                "| name         = Goo Goo Dancer\n" +
                "| description  = Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!\n" +
                "| spoiler      = Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.\n" +
                "| premium      = yes\n" +
                "| points       = 1\n" +
                "| secret       = yes\n" +
                "| implemented  = 9.6\n" +
                "| achievementid = 319\n" +
                "| relatedpages = [[Muck Remover]], [[Mucus Plug]]\n"));
    }

    @Test
    public void testSplitByParameter_EmptyOrNull() {
        assertThat(TemplateUtils.splitInfoboxByParameter(null).size(), is(0));
        assertThat(TemplateUtils.splitInfoboxByParameter("").size(), is(0));
    }

    @Test
    public void testSplitByParameter_InfoboxAchievement() {
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
    public void testSplitByCommaAndTrim() {
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
    public void testExtractLowerLevels_Empty() {
        assertThat(TemplateUtils.extractLowerLevels(null), is(Optional.empty()));
        assertThat(TemplateUtils.extractLowerLevels(""), is(Optional.empty()));
        assertThat(TemplateUtils.extractLowerLevels(INPUT_EMPTY_LOWERLEVELS), is(Optional.empty()));
    }

    @Test
    public void testExtractLowerLevels_MatchOne() {
        final Optional<Map<String, String>> result = TemplateUtils.extractLowerLevels(INPUT_ONE_LOWER_LEVEL);

        assertThat(result, not(Optional.empty()));
        assertThat("Test: result is correct", result.get().get("lowerlevels").contains("Demons"));
    }

    @Test
    public void testExtractLowerLevels_MatchMultiple() {
        final Optional<Map<String, String>> result = TemplateUtils.extractLowerLevels(INPUT_MULTIPLE_LOWER_LEVELS);

        assertThat(result, not(Optional.empty()));
        assertThat("Test: result is correct", result.get().get("lowerlevels").contains("Demons"));
        assertThat("Test: result is correct", result.get().get("lowerlevels").contains("Demons2"));
        assertThat("Test: result is correct", result.get().get("lowerlevels").contains("Demons3"));
    }

    @Test
    public void testRemoveLowerLevels_Empty() {
        assertThat(TemplateUtils.removeLowerLevels(INPUT_EMPTY_LOWERLEVELS), is(INPUT_EMPTY_LOWERLEVELS));
    }

    @Test
    public void testRemoveLowerLevels_One() {
        final String result = TemplateUtils.removeLowerLevels(INPUT_ONE_LOWER_LEVEL);
        assertThat(result, not(INPUT_ONE_LOWER_LEVEL));
        assertThat(result, not(containsString("lowerlevels")));
    }

    @Test
    public void testRemoveLowerLevels_Multiple() {
        final String result = TemplateUtils.removeLowerLevels(INPUT_MULTIPLE_LOWER_LEVELS);
        assertThat(result, not(INPUT_MULTIPLE_LOWER_LEVELS));
        assertThat(result, not(containsString("lowerlevels")));
    }

    private static final String INFOBOX__ACHIEVEMENT_TEXT = "{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| grade        = 1\n" +
            "| name         = Goo Goo Dancer\n" +
            "| description  = Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!\n" +
            "| spoiler      = Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.\n" +
            "| premium      = yes\n" +
            "| points       = 1\n" +
            "| secret       = yes\n" +
            "| implemented  = 9.6\n" +
            "| achievementid = 319\n" +
            "| relatedpages = [[Muck Remover]], [[Mucus Plug]]\n" +
            "}}";

    private static final String INPUT_EMPTY_LOWERLEVELS = "| name            = Hero Cave\n" +
            "| image           = Hero\n" +
            "| implemented     = 6.4\n" +
            "| city            = Edron\n" +
            "| location        = North of [[Edron]], [http://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].\n" +
            "| vocation        = All vocations.\n" +
            "| lvlknights      = 70\n" +
            "| lvlpaladins     = 60\n" +
            "| lvlmages        = 50\n" +
            "| skknights       = 75\n" +
            "| skpaladins      = 80\n" +
            "| skmages         = \n" +
            "| defknights      = 75\n" +
            "| defpaladins     = \n" +
            "| defmages        =\n" +
            "| lowerlevels     = \n" +
            "| exp             = Good\n" +
            "| loot            = Good\n" +
            "| bestloot        = Reins\n" +
            "| bestloot2       = \n" +
            "| bestloot3       = \n" +
            "| bestloot4       = \n" +
            "| bestloot5       = \n" +
            "| map             = Hero Cave 3.png\n" +
            "| map2            = Hero Cave 6.png\n";

    private static final String INPUT_ONE_LOWER_LEVEL = "| name            = Hero Cave\n" +
            "| image           = Hero\n" +
            "| implemented     = 6.4\n" +
            "| city            = Edron\n" +
            "| location        = North of [[Edron]], [http://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].\n" +
            "| vocation        = All vocations.\n" +
            "| lvlknights      = 70\n" +
            "| lvlpaladins     = 60\n" +
            "| lvlmages        = 50\n" +
            "| skknights       = 75\n" +
            "| skpaladins      = 80\n" +
            "| skmages         = \n" +
            "| defknights      = 75\n" +
            "| defpaladins     = \n" +
            "| defmages        =\n" +
            "| lowerlevels     = \n" +
            "    {{Infobox Hunt Skills\n" +
            "    | areaname        = Demons\n" +
            "    | lvlknights      = 130\n" +
            "    | lvlpaladins     = 130\n" +
            "    | lvlmages        = 130\n" +
            "    | skknights       = \n" +
            "    | skpaladins      = \n" +
            "    | skmages         = \n" +
            "    | defknights      = \n" +
            "    | defpaladins     =\n" +
            "    | defmages        =\n" +
            "    }}\n" +
            "| exp             = Good\n" +
            "| loot            = Good\n" +
            "| bestloot        = Reins\n" +
            "| bestloot2       = \n" +
            "| bestloot3       = \n" +
            "| bestloot4       = \n" +
            "| bestloot5       = \n" +
            "| map             = Hero Cave 3.png\n" +
            "| map2            = Hero Cave 6.png\n";

    private static final String INPUT_MULTIPLE_LOWER_LEVELS = "| name            = Hero Cave\n" +
            "| image           = Hero\n" +
            "| implemented     = 6.4\n" +
            "| city            = Edron\n" +
            "| location        = North of [[Edron]], [http://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].\n" +
            "| vocation        = All vocations.\n" +
            "| lvlknights      = 70\n" +
            "| lvlpaladins     = 60\n" +
            "| lvlmages        = 50\n" +
            "| skknights       = 75\n" +
            "| skpaladins      = 80\n" +
            "| skmages         = \n" +
            "| defknights      = 75\n" +
            "| defpaladins     = \n" +
            "| defmages        =\n" +
            "| lowerlevels     = \n" +
            "    {{Infobox Hunt Skills\n" +
            "    | areaname        = Demons\n" +
            "    | lvlknights      = 130\n" +
            "    | lvlpaladins     = 130\n" +
            "    | lvlmages        = 130\n" +
            "    | skknights       = \n" +
            "    | skpaladins      = \n" +
            "    | skmages         = \n" +
            "    | defknights      = \n" +
            "    | defpaladins     =\n" +
            "    | defmages        =\n" +
            "    }}\n" +
            "    {{Infobox Hunt Skills\n" +
            "    | areaname        = Demons2\n" +
            "    | lvlknights      = 139\n" +
            "    | lvlpaladins     = 139\n" +
            "    | lvlmages        = 139\n" +
            "    | skknights       = \n" +
            "    | skpaladins      = \n" +
            "    | skmages         = \n" +
            "    | defknights      = \n" +
            "    | defpaladins     =\n" +
            "    | defmages        =\n" +
            "    }}\n" +
            "    {{Infobox Hunt Skills\n" +
            "    | areaname        = Demons3\n" +
            "    | lvlknights      = 149\n" +
            "    | lvlpaladins     = 149\n" +
            "    | lvlmages        = 149\n" +
            "    | skknights       = \n" +
            "    | skpaladins      = \n" +
            "    | skmages         = \n" +
            "    | defknights      = \n" +
            "    | defpaladins     =\n" +
            "    | defmages        =\n" +
            "    }}\n" +
            "| exp             = Good\n" +
            "| loot            = Good\n" +
            "| bestloot        = Reins\n" +
            "| bestloot2       = \n" +
            "| bestloot3       = \n" +
            "| bestloot4       = \n" +
            "| bestloot5       = \n" +
            "| map             = Hero Cave 3.png\n" +
            "| map2            = Hero Cave 6.png\n";

}