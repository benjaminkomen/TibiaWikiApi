package com.tibiawiki.domain.utils;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class TemplateUtilsTest {

    @Test
    public void testExtractLowerLevels_Empty() {
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