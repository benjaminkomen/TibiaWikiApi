package com.tibiawiki.domain.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibiawiki.domain.enums.Grade;
import com.tibiawiki.domain.enums.YesNo;
import com.tibiawiki.domain.objects.Achievement;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class JsonFactoryTest {

    private JsonFactory target;
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setup() {
        target = new JsonFactory();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testConvertInfoboxPartOfArticleToJson_NullOrEmpty() {
        assertThat(target.convertInfoboxPartOfArticleToJson(null), instanceOf(JSONObject.class));
        assertThat(target.convertInfoboxPartOfArticleToJson(""), instanceOf(JSONObject.class));
    }

    @Test
    public void testConvertInfoboxPartOfArticleToJson_InfoboxAchievement() {
        JSONObject result = target.convertInfoboxPartOfArticleToJson(INFOBOX__ACHIEVEMENT_TEXT);

        assertThat(result.get("templateType"), is("Achievement"));
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
    public void testConvertInfoboxPartOfArticleToJson_InfoboxHunt() {
        JSONObject result = target.convertInfoboxPartOfArticleToJson(INFOBOX_HUNT_TEXT);

        assertThat(result.get("templateType"), is("Hunt"));
        assertThat(result.get("name"), is("Hero Cave"));
        assertThat(result.get("image"), is("Hero"));
        assertThat(result.get("implemented"), is("6.4"));
        assertThat(result.get("city"), is("Edron"));
        assertThat(result.get("location"), is("North of [[Edron]], [http://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here]."));
        assertThat(result.get("vocation"), is("All vocations."));
        assertThat(result.get("lvlknights"), is("70"));
        assertThat(result.get("lvlpaladins"), is("60"));
        assertThat(result.get("lvlmages"), is("50"));
        assertThat(result.get("skknights"), is("75"));
        assertThat(result.get("skpaladins"), is("80"));
        assertThat(result.get("defknights"), is("75"));
        assertThat(result.get("lowerlevels"), instanceOf(JSONArray.class));
        assertThat(((JSONObject) ((JSONArray) result.get("lowerlevels")).get(0)).get("areaname"), is("Demons"));
        assertThat(((JSONObject) ((JSONArray) result.get("lowerlevels")).get(0)).get("lvlknights"), is("130"));
        assertThat(((JSONObject) ((JSONArray) result.get("lowerlevels")).get(0)).get("lvlpaladins"), is("130"));
        assertThat(((JSONObject) ((JSONArray) result.get("lowerlevels")).get(0)).get("lvlmages"), is("130"));
        assertThat(result.get("exp"), is("Good"));
        assertThat(result.get("loot"), is("Good"));
        assertThat(result.get("bestloot"), is("Reins"));
        assertThat(result.get("map"), is("Hero Cave 3.png"));
        assertThat(result.get("map2"), is("Hero Cave 6.png"));
    }

    @Test
    public void testGetTemplateType_NullOrEmpty() {
        assertThat(target.getTemplateType(null), is("Unknown"));
        assertThat(target.getTemplateType(""), is("Unknown"));
    }

    @Test
    public void testGetTemplateType_Succes() {
        assertThat(target.getTemplateType(INFOBOX_TEXT_SPACE), is("Achievement"));
        assertThat(target.getTemplateType(INFOBOX_TEXT_UNDERSCORE), is("Hunt"));
    }

    @Test
    public void testGetTemplateType_Failure() {
        assertThat(target.getTemplateType(INFOBOX_TEXT_WRONG), is("Unknown"));
    }

    @Test
    public void testEnhanceJsonObject_FailureNoName() {
        final JSONObject someJsonObject = new JSONObject(Collections.emptyMap());
        assertThat(target.enhanceJsonObject(someJsonObject), is(someJsonObject));
    }

    @Test
    public void testEnhanceJsonObject_Failure_Sounds() {
        final JSONObject inputJsonObject = new JSONObject(Map.of(
                "name", "Dragon",
                "templateType", "Creature",
                "sounds", "FCHHHHH, GROOAAARRR"
        ));
        JSONObject result = target.enhanceJsonObject(inputJsonObject);
        assertThat(((JSONArray) result.get("sounds")).length(), is(0));
    }

    @Test
    public void testEnhanceJsonObject_Succes_Sounds() {
        final JSONObject inputJsonObject = new JSONObject(Map.of(
                "name", "Dragon",
                "templateType", "Creature",
                "sounds", "{{Sound List|FCHHHHH|GROOAAARRR}}"
        ));
        JSONObject result = target.enhanceJsonObject(inputJsonObject);
        assertThat(((JSONArray) result.get("sounds")).get(0), is("FCHHHHH"));
        assertThat(((JSONArray) result.get("sounds")).get(1), is("GROOAAARRR"));
    }

    @Test
    public void testEnhanceJsonObject_Spawntype_Empty() {
        final JSONObject inputJsonObject = new JSONObject(Map.of(
                "name", "Demon",
                "templateType", "Creature",
                "spawntype", " "
        ));
        JSONObject result = target.enhanceJsonObject(inputJsonObject);
        assertThat(((JSONArray) result.get("spawntype")).length(), is(0));
    }

    @Test
    public void testEnhanceJsonObject_Spawntype_Succes() {
        final JSONObject inputJsonObject = new JSONObject(Map.of(
                "name", "Demon",
                "templateType", "Creature",
                "spawntype", "Regular, Raid"
        ));
        JSONObject result = target.enhanceJsonObject(inputJsonObject);
        assertThat(((JSONArray) result.get("spawntype")).get(0), is("Regular"));
        assertThat(((JSONArray) result.get("spawntype")).get(1), is("Raid"));
    }

    @Test
    public void testDetermineArticleName_EmptyOrNull() {
        assertThat(target.determineArticleName(null, null), is("Unknown"));
        assertThat(target.determineArticleName(new JSONObject(), ""), is("Unknown"));
    }

    @Test
    public void testDetermineArticleName_Book() {
        JSONObject input = new JSONObject(Map.of("pagename", "Foobar"));

        assertThat(target.determineArticleName(input, JsonFactory.TEMPLATE_TYPE_BOOK), is("Foobar"));
    }

    @Test
    public void testDetermineArticleName_Location() {
        JSONObject input = new JSONObject();

        assertThat(target.determineArticleName(input, JsonFactory.TEMPLATE_TYPE_LOCATION), is("Unknown"));
    }

    @Test
    public void testDetermineArticleName_Key() {
        JSONObject input = new JSONObject(Map.of("number", "1234"));

        assertThat(target.determineArticleName(input, JsonFactory.TEMPLATE_TYPE_KEY), is("Key 1234"));
    }

    @Test
    public void testDetermineArticleName_Achievement() {
        JSONObject input = new JSONObject(Map.of("name", "Foobar"));

        assertThat(target.determineArticleName(input, JsonFactory.TEMPLATE_TYPE_ACHIEVEMENT), is("Foobar"));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Empty() {
        assertThat(target.convertJsonToInfoboxPartOfArticle(null, Collections.emptyList()), is(""));
        assertThat(target.convertJsonToInfoboxPartOfArticle(new JSONObject(), Collections.emptyList()), is(""));

        final JSONObject jsonWithNoTemplateType = makeAchievementJson(makeAchievement());
        jsonWithNoTemplateType.remove("templateType");
        assertThat(target.convertJsonToInfoboxPartOfArticle(jsonWithNoTemplateType, Collections.emptyList()), is(""));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Simple() {
        String result = target.convertJsonToInfoboxPartOfArticle(makeAchievementJson(makeAchievement()), makeAchievement().fieldOrder());
        assertThat(result, is(INFOBOX__ACHIEVEMENT_TEXT));
    }

    private static final String INFOBOX_TEXT_SPACE = "{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Goo Goo Dancer\n" +
            "}}";

    private static final String INFOBOX_TEXT_UNDERSCORE = "{{Infobox_Hunt|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Goo Goo Dancer\n" +
            "}}";

    private static final String INFOBOX_TEXT_WRONG = "{{Infobax_Hunt  \n|List={{{1|}}}|GetValue={{{GetValue|}}}";

    private static final String INFOBOX__ACHIEVEMENT_TEXT = "{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
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

    private static final String INFOBOX_HUNT_TEXT = "{{Infobox Hunt|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name            = Hero Cave\n" +
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
            "| map2            = Hero Cave 6.png\n" +
            "}}";

    private Achievement makeAchievement() {
        return Achievement.builder()
                    .grade(Grade.ONE)
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

    private JSONObject makeAchievementJson(Achievement achievement) {
        return new JSONObject(objectMapper.convertValue(achievement, Map.class)).put("templateType", "Achievement");
    }
}