package com.tibiawiki.domain.factories;

import com.tibiawiki.domain.enums.YesNo;
import com.tibiawiki.domain.objects.Achievement;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * We want to be sure that the deserialization of a wiki article to a java object,
 * and serialisation back to the wiki article, do not cause unwanted side-effects in the article's content. Some effects
 * are however not side-effects, but completely legit:
 * - removal of undocumentend parameters;
 * - removal of empty parameters;
 * - ...
 */
public class JsonFactoryIT {

    private JsonFactory target;

    @BeforeEach
    void setup() {
        target = new JsonFactory();
    }

    @Test
    void testDeserializeAndSerialiseSomeAchievement() {
        JSONObject achievementAsJson = target.convertInfoboxPartOfArticleToJson(INFOBOX_ACHIEVEMENT_TEXT);

        String result = target.convertJsonToInfoboxPartOfArticle(achievementAsJson, makeAchievement().fieldOrder());

        assertThat(result, is(INFOBOX_ACHIEVEMENT_TEXT));
    }

    private static final String INFOBOX_ACHIEVEMENT_TEXT = "{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| grade         = 1\n" +
            "| name          = Goo Goo Dancer\n" +
            "| description   = Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!\n" +
            "| spoiler       = Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.\n" +
            "| premium       = yes\n" +
            "| points        = 1\n" +
            "| secret        = yes\n" +
            "| implemented   = 9.6\n" +
            "| achievementid = 319\n" +
            "| relatedpages  = [[Muck Remover]], [[Mucus Plug]]\n" +
            "}}\n";

    private Achievement makeAchievement() {
        return Achievement.builder()
                .grade(1)
                .name("Goo Goo Dancer")
                .description("Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!")
                .spoiler("Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.")
                .premium(YesNo.YES_LOWERCASE)
                .points(1)
                .secret(YesNo.YES_LOWERCASE)
                .implemented("9.6")
                .achievementid(319)
                .relatedpages("[[Muck Remover]], [[Mucus Plug]]")
                .build();
    }
}
