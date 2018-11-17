package com.tibiawiki.domain.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibiawiki.domain.enums.BookType;
import com.tibiawiki.domain.enums.Grade;
import com.tibiawiki.domain.enums.YesNo;
import com.tibiawiki.domain.objects.Achievement;
import com.tibiawiki.domain.objects.Book;
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
        JSONObject result = target.convertInfoboxPartOfArticleToJson(INFOBOX_ACHIEVEMENT_TEXT);

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
    void testConvertJsonToInfoboxPartOfArticle_Achievement() {
        final Achievement achievement = makeAchievement();
        String result = target.convertJsonToInfoboxPartOfArticle(makeAchievementJson(achievement), achievement.fieldOrder());
        assertThat(result, is(INFOBOX_ACHIEVEMENT_TEXT));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Book() {
        final Book book = makeBook();
        String result = target.convertJsonToInfoboxPartOfArticle(makeBookJson(book), book.fieldOrder());
        assertThat(result, is(INFOBOX_BOOK_TEXT));
    }

    private static final String INFOBOX_TEXT_SPACE = "{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Goo Goo Dancer\n" +
            "}}";

    private static final String INFOBOX_TEXT_UNDERSCORE = "{{Infobox_Hunt|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Goo Goo Dancer\n" +
            "}}";

    private static final String INFOBOX_TEXT_WRONG = "{{Infobax_Hunt  \n|List={{{1|}}}|GetValue={{{GetValue|}}}";

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

    private static final String INFOBOX_BOOK_TEXT = "{{Infobox Book|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| booktype     = Book (Brown)\n" +
            "| title        = Dungeon Survival Guide\n" +
            "| pagename     = Dungeon Survival Guide (Book)\n" +
            "| location     = [[Rookgaard Academy]]\n" +
            "| blurb        = Tips for exploring dungeons, and warning against being reckless.\n" +
            "| returnpage   = Rookgaard Libraries\n" +
            "| relatedpages = [[Rope]], [[Shovel]]\n" +
            "| text         = Dungeon Survival Guide<br><br>Don't explore the dungeons before you tested your skills" +
            " in the training cellars of our academy. You will find dungeons somewhere in the wilderness. Don't enter" +
            " dungeons without equipment. Especially a rope and a shovel will prove valuable. Make sure you have a" +
            " supply of torches with you, while wandering into the unknown. It's wise to travel the dungeons in groups" +
            " and not alone. For more help read all the books of the academy before you begin exploring. Traveling in" +
            " the dungeons will reward the cautious and brave, but punish the reckless.\n" +
            "}}\n";

    private Book makeBook() {
        return Book.builder()
                .booktype(BookType.BOOK_BROWN)
                .title("Dungeon Survival Guide")
                .pagename("Dungeon Survival Guide (Book)")
                .location("[[Rookgaard Academy]]")
                .blurb("Tips for exploring dungeons, and warning against being reckless.")
                .returnpage("Rookgaard Libraries")
                .relatedpages("[[Rope]], [[Shovel]]")
                .text("Dungeon Survival Guide<br><br>Don't explore the dungeons before you tested your skills" +
                        " in the training cellars of our academy. You will find dungeons somewhere in the wilderness. Don't enter" +
                        " dungeons without equipment. Especially a rope and a shovel will prove valuable. Make sure you have a" +
                        " supply of torches with you, while wandering into the unknown. It's wise to travel the dungeons in groups" +
                        " and not alone. For more help read all the books of the academy before you begin exploring. Traveling in" +
                        " the dungeons will reward the cautious and brave, but punish the reckless.")
                .build();
    }

    private JSONObject makeBookJson(Book book) {
        return new JSONObject(objectMapper.convertValue(book, Map.class)).put("templateType", "Book");
    }

    private static final String INFOBOX_BUILDING_TEXT = "{{Infobox Building|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name          = Theater Avenue 8b\n" +
            "| implemented   = Pre-6.0\n" +
            "| type          = House\n" +
            "| location      = South-east of depot, two floors up.\n" +
            "| posx          = 126.101\n" +
            "| posy          = 124.48\n" +
            "| posz          = 5\n" +
            "| street        = Theater Avenue\n" +
            "| houseid       = 20315\n" +
            "| size          = 26\n" +
            "| beds          = 3\n" +
            "| rent          = 1370\n" +
            "| city          = Carlin\n" +
            "| openwindows   = 3\n" +
            "| floors        = 1\n" +
            "| rooms         = 1\n" +
            "| furnishings   = 1 [[Wall Lamp]].\n" +
            "| notes         = \n" +
            "| image         = [[File:Theater Avenue 8b.png]]\n" +
            "}}\n";

    private static final String INFOBOX_CORPSE_TEXT = "{{Infobox Corpse|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name           = Dead Rat\n" +
            "| article        = a\n" +
            "| liquid         = [[Blood]]\n" +
            "| 1volume        = 5\n" +
            "| 1weight        = 63.00\n" +
            "| 2weight        = 44.00\n" +
            "| 3weight        = 30.00\n" +
            "| 1decaytime     = 5 minutes.\n" +
            "| 2decaytime     = 5 minutes.\n" +
            "| 3decaytime     = 60 seconds.\n" +
            "| 1npcvalue      = 2\n" +
            "| corpseof       = [[Rat]], [[Cave Rat]], [[Munster]]\n" +
            "| sellto         = [[Tom]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Seymour]] ([[Rookgaard]]) '''2''' [[gp]]" +
            "<br>[[Billy]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Humgolf]] ([[Kazordoon]]) '''2''' [[gp]]<br>\n" +
            "[[Baxter]] ([[Thais]]) '''1''' [[gp]]<br>\n" +
            "| implemented    = Pre-6.0\n" +
            "| notes          = These corpses are commonly used by low level players on [[Rookgaard]] to earn some gold" +
            " for better [[equipment]]. Only fresh corpses are accepted, rotted corpses are ignored.\n" +
            "}}\n";

    private static final String INFOBOX_CREATURE_TEXT = "{{Infobox Creature|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name           = Dragon\n" +
            "| article        = a\n" +
            "| actualname     = dragon\n" +
            "| plural         = dragons\n" +
            "| implemented    = Pre-6.0\n" +
            "| hp             = 1000\n" +
            "| exp            = 700\n" +
            "| summon         = --\n" +
            "| convince       = --\n" +
            "| illusionable   = yes\n" +
            "| creatureclass  = Reptiles\n" +
            "| primarytype    = Dragons\n" +
            "| bestiaryclass  = Dragon\n" +
            "| bestiarylevel  = Medium\n" +
            "| occurrence     = Common\n" +
            "| isboss         = no\n" +
            "| isarenaboss    = no\n" +
            "| abilities      = [[Melee]] (0-120), [[Fire Wave]] (100-170), [[Great Fireball]] (60-140), [[Self-Healing]] (40-70)\n" +
            "| maxdmg         = 430\n" +
            "| armor          = 25\n" +
            "| pushable       = No\n" +
            "| pushobjects    = Yes\n" +
            "| walksthrough   = Fire, Energy, Poison\n" +
            "| walksaround    = None\n" +
            "| paraimmune     = yes\n" +
            "| senseinvis     = yes\n" +
            "| physicalDmgMod = 100%\n" +
            "| holyDmgMod     = 100%\n" +
            "| deathDmgMod    = 100%\n" +
            "| fireDmgMod     = 0%\n" +
            "| energyDmgMod   = 80%\n" +
            "| iceDmgMod      = 110%\n" +
            "| earthDmgMod    = 20%\n" +
            "| drownDmgMod    = 100%?\n" +
            "| hpDrainDmgMod  = 100%?\n" +
            "| bestiaryname   = dragon\n" +
            "| bestiarytext   = Dragons were among the first creatures of Tibia and once ruled the whole continent." +
            " Nowadays, there are only a few of them left which live deep in the dungeons. Nevertheless, they are very" +
            " powerful monsters and will strive for killing every intruder. Besides their immense strength, they shoot" +
            " fireballs at their victims and spit fire. Moreover, they can heal themselves.\n" +
            "| sounds         = {{Sound List|FCHHHHH|GROOAAARRR}}\n" +
            "| notes          = Dragons are very slow-moving, but have a potent set of attacks. A [[mage]] or" +
            " [[paladin]] can kill one without taking any damage once they master the art. These creatures can be" +
            " skinned with an [[Obsidian Knife]]. See also: [[Green Dragon Leather/Skinning]].\n" +
            "| behaviour      = Dragons are known to [[Retargeting|retarget]]. This often causes shooters in a team" +
            " hunt to be burned or killed.\n" +
            "| runsat         = 300\n" +
            "| speed          = 86\n" +
            "| location       = [[Thais]] [[Ancient Temple]], [[Darashia Dragon Lair]], [[Mount Sternum Dragon Cave]]," +
            " [[Mintwallin]], deep in [[Fibula Dungeon]], [[Kazordoon Dragon Lair]] (near [[Dwarf Bridge]]), [[Plains" +
            " of Havoc]], [[Elven Bane]] castle, [[Maze of Lost Souls]], southern cave and dragon tower in" +
            " [[Shadowthorn]], [[Orc Fortress]], [[Venore]] [[Dragon Lair]], [[Pits of Inferno]], [[Behemoth Quest]]" +
            " room in [[Edron]], [[Hero Cave]], deep [[Cyclopolis]], [[Edron Dragon Lair]], [[Goroma]], [[Ankrahmun" +
            " Dragon Lair]]s, [[Draconia]], [[Dragonblaze Peaks]], some [[Ankrahmun Tombs]], underground of [[Fenrock]]" +
            " (on the way to [[Beregar]]), [[Krailos Steppe]] and [[Crystal Lakes]].\n" +
            "| strategy       = '''All''' [[player]]s should stand diagonal from the dragon, whenever possible, to" +
            " avoid the [[Fire Wave]].\n" +
            " \n" +
            "'''[[Knight]]s''': Only one knight should be present in a team hunt, as they, the [[Blocking|blocker]]," +
            " must be able to move freely around the dragon and to maintain their diagonal position as the dragon" +
            " takes a step. It is quite easy for a knight of [[level]] 40 or higher to block a dragon without using" +
            " any Health Potions at all. Around level 60 a knight with good skills (70/70) can hunt dragons with little" +
            " waste and possibly profit. Remember to stand diagonal to it and always be prepared to use" +
            " [[Health Potion|potions]]. A level 80+ knight can hunt dragons using only food and" +
            " [[Pair of Soft Boots|Soft Boots]].<br />\n" +
            "'''[[Mage]]s''' of level 28 or higher can kill dragons without help from other players, but you need to be" +
            " very careful. They can [[Summon]] two [[Demon Skeleton]]s and drink a few extra [[Mana Potion]]s" +
            " afterwards. They should try to keep enough [[mana]] to heal, if needed. They should enter, lure the" +
            " dragon out, attack it with the demon skeletons, then move to a different floor so the dragon will target" +
            " the demon skeletons. It is advisable to move to a space about 3 squares diagonal and use a strike spell" +
            " ([[Ice Strike]] if possible) or [[Icicle Rune]]s to kill faster. Heal when your hit points drop below" +
            " 250-280. Druids level 35 or higher, can use [[Mass Healing]] to prevent their summons from dying," +
            " otherwise use [[Healing Runes]]. This is a reasonably cheap way to hunt dragons although the demon" +
            " skeletons also gain a share of the experience.<br />\n" +
            "'''[[Paladin]]s''' with a distance skill of 60+ and enough hit points to survive a fire attack are welcome" +
            " additions to a team dragon hunt. Just be sure to have the [[Divine Healing]] spell ready to use, and " +
            "stand where you can also escape if the dragon retargets. A paladin's ability to solo a dragon depends" +
            " greatly on the terrain. A dragon's melee is weaker than their area attacks, so it would be advisable to" +
            " stand diagonal the Dragon but only 1 sqm away, while shooting [[Royal Spear]]s or [[Enchanted Spear]]s." +
            " A level 20 paladin with skills 65+ may attempt to solo a single dragon spawn but will have to bring some" +
            " potions. Killing a dragon at this level will only prove your strength as a paladin will spend" +
            " approximately 500 [[gp]]s per dragon and the chance of dying is very high if not careful. It is advisable" +
            " to bring some [[Icicle Rune|Icicles]] or [[Avalanche Rune]]s if facing two or more of them.\n" +
            "| loot           = {{Loot Table\n" +
            " |{{Loot Item|0-105|Gold Coin}}\n" +
            " |{{Loot Item|0-3|Dragon Ham}}\n" +
            " |{{Loot Item|Steel Shield}}\n" +
            " |{{Loot Item|Crossbow}}\n" +
            " |{{Loot Item|Dragon's Tail}}\n" +
            " |{{Loot Item|0-10|Burst Arrow}}\n" +
            " |{{Loot Item|Longsword|semi-rare}}\n" +
            " |{{Loot Item|Steel Helmet|semi-rare}}\n" +
            " |{{Loot Item|Broadsword|semi-rare}}\n" +
            " |{{Loot Item|Plate Legs|semi-rare}}\n" +
            " |{{Loot Item|Green Dragon Leather|rare}}\n" +
            " |{{Loot Item|Wand of Inferno|rare}}\n" +
            " |{{Loot Item|Strong Health Potion|rare}}\n" +
            " |{{Loot Item|Green Dragon Scale|rare}}\n" +
            " |{{Loot Item|Double Axe|rare}}\n" +
            " |{{Loot Item|Dragon Hammer|rare}}\n" +
            " |{{Loot Item|Serpent Sword|rare}}\n" +
            " |{{Loot Item|Small Diamond|very rare}}\n" +
            " |{{Loot Item|Dragon Shield|very rare}}\n" +
            " |{{Loot Item|Life Crystal|very rare}}\n" +
            " |{{Loot Item|Dragonbone Staff|very rare}}\n" +
            "}}\n" +
            "| history            = Dragons are one of the oldest creatures in the game. In older times (prior to 2001" +
            " at least) dragons were [[Summon Creature|summonable]], and during these times it was possible to summon" +
            " up to 8 creatures at once (even through requiring very high [[mana]]) and only the highest leveled" +
            " [[mage]]s could summon them (back then the highest [[level]]s were only around level 60 or 70). It was a" +
            " somewhat common occurrence to see mages walking the streets of [[Thais]] with several dragons summoned" +
            " at one time. It was also possible to set summons free by logging out of the game, turning the summons" +
            " into wild creatures. Often mages would leave the game after summoning as many as 8 dragons in the middle" +
            " of major [[Hometowns|cities]], causing chaos.\n" +
            "}}\n";

    private static final String INFOBOX_EFFECT_TEXT = "{{Infobox Effect|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name          = Fireball Effect\n" +
            "| implemented   =\n" +
            "| effectid      = 7\n" +
            "| primarytype   = Attack\n" +
            "| secondarytype = \n" +
            "| lightcolor    = 208\n" +
            "| lightradius   = 6\n" +
            "| causes        = \n" +
            "*[[Fireball]] and [[Great Fireball]];\n" +
            "*Certain [[Creature Spells]];\n" +
            "*Making a [[Stuffed Dragon]] sneeze;\n" +
            "*Using a [[Demon Infant]] on [[Lava]] if tasked to.\n" +
            "| effect        = [[Fire Damage]] on target or nothing.\n" +
            "| notes         =\n" +
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

    private static final String INFOBOX_ITEM_TEXT = "{{Infobox Item|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name          = Carlin Sword\n" +
            "| marketable    = yes\n" +
            "| usable        = yes\n" +
            "| sprites       = {{Frames|{{Frame Sprite|55266}}}}\n" +
            "| article       = a\n" +
            "| actualname    = carlin sword\n" +
            "| plural        = ?\n" +
            "| itemid        = 3283\n" +
            "| flavortext    =\n" +
            "| itemclass     = Weapons\n" +
            "| primarytype   = Sword Weapons\n" +
            "| levelrequired = 0\n" +
            "| hands         = One\n" +
            "| type          = Sword\n" +
            "| attack        = 15\n" +
            "| defense       = 13\n" +
            "| defensemod    = +1\n" +
            "| enchantable   = no\n" +
            "| weight        = 40.00\n" +
            "| droppedby     = {{Dropped By|Grorlam|Stone Golem}}\n" +
            "| value         = 118 \n" +
            "| npcvalue      = 118 \n" +
            "| npcprice      = 473 \n" +
            "| npcvaluerook  = 0\n" +
            "| npcpricerook  = 0\n" +
            "| buyfrom       = Baltim, Brengus, Cedrik, Esrik, Flint, Gamel, Habdel, Hardek, Memech, Morpel, Robert," +
            " Rock In A Hard Place, Romella, Rowenna, Sam, Shanar, Turvy, Ulrik, Uzgod, Willard\n" +
            "| sellto        = Baltim, Brengus, Cedrik, Esrik, Flint, Gamel, H.L.: 5, Habdel, Hardek, Memech, Morpel," +
            " Robert, Rock In A Hard Place, Romella, Rowenna, Sam, Shanar, Turvy, Ulrik, Uzgod, Willard\n" +
            "| notes         = If you have one of these in [[Rookgaard]] and already reached level 8 you may want to" +
            " keep it, if you are going to [[Carlin]]. A common strategy in [[Rookgaard]] was to buy as many carlin" +
            " swords as the Rookgaardian can carry and sell all their [[equipment]] to other Rookgaardians and make a" +
            " hefty profit on the carlin swords in [[Mainland]], although this may not be recommendable now that one" +
            " goes to the [[Island of Destiny]] at [[level]] 8 ([[Raffael]] doesn't buy Carlin Swords).\n" +
            "{{JSpoiler|Obtainable in [[Rookgaard]] through the [[Minotaur Hell Quest]].}}\n" +
            "}}\n";

    private static final String INFOBOX_KEY_TEXT = "{{Infobox Key|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| number         = 4055\n" +
            "| aka            = Panpipe Quest Key\n" +
            "| primarytype    = Silver\n" +
            "| location       = [[Jakundaf Desert]]\n" +
            "| value          = Negotiable\n" +
            "| npcvalue       = 0\n" +
            "| npcprice       = 0\n" +
            "| buyfrom        = --\n" +
            "| sellto         = --\n" +
            "| origin         = Hidden in a rock south of the Desert Dungeon entrance.\n" +
            "| shortnotes     = Access to the [[Panpipe Quest]].\n" +
            "| longnotes      = Allows you to open the door ([http://tibia.wikia.com/wiki/Mapper?coords=127.131,125.129,8,3,1,1 here]) to the [[Panpipe Quest]].\n" +
            "}}\n";

    private static final String INFOBOX_MISSILE_TEXT = "{{Infobox Missile|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name          = Throwing Cake Missile\n" +
            "| implemented   = 7.9\n" +
            "| missileid     = 42\n" +
            "| primarytype   = Throwing Weapon\n" +
            "| secondarytype = \n" +
            "| lightcolor    = \n" +
            "| lightradius   = \n" +
            "| shotby        = [[Undead Jester]]'s attack and probably by throwing a [[Throwing Cake]].\n" +
            "| notes         = This missile is followed by the [[Cream Cake Effect]]: [[File:Cream Cake Effect.gif]]\n" +
            "}}\n";

    private static final String INFOBOX_MOUNT_TEXT = "{{Infobox_Mount|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name            = Donkey\n" +
            "| speed           = 10\n" +
            "| taming_method   = Use a [[Bag of Apple Slices]] on a creature transformed into Donkey.\n" +
            "| implemented     = 9.1\n" +
            "| achievement     = Loyal Lad\n" +
            "| notes           = Go to [[Incredibly Old Witch]]'s house, [http://tibia.wikia.com/wiki/Mapper?coords=" +
            "125.250,126.95,7,2,1,1 here], then lure any wild creature and wait until the witch turns it into a Donkey." +
            " Use a [[Bag of Apple Slices]] on the changed creature before it's changed again. You can also trap the" +
            " Incredibly Old Witch and use the Bag when she transforms herself. It's recommended to put the Bag of" +
            " Apple Slices on a hotkey so when a creature is changed to Donkey you just need to press it instead of" +
            " clicking the item and then the Donkey.<br />\n" +
            "If you tame the donkey by using the item on the witch herself (while she is transformed into Donkey), she" +
            " will temporarily vanish, and respawns about 10 minutes later.<br />\n" +
            "On most game worlds it's the cheapest mount to obtain, what makes it popular among low or mid levels who" +
            " want to gain additional 10 points of speed.\n" +
            "\n" +
            "<gallery captionalign=\"center\">Donkey (Transformation).gif|Donkey (transformed creature)</gallery>\n" +
            "}}\n";

    private static final String INFOBOX_NPC_TEXT = "{{Infobox NPC|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name        = Sam\n" +
            "| implemented = Pre-6.0\n" +
            "| job         = Artisan\n" +
            "| job2        = Weapon Shopkeeper\n" +
            "| job3        = Armor Shopkeeper\n" +
            "| location    = [[Temple Street]] in [[Thais]].\n" +
            "| posx        = 126.104\n" +
            "| posy        = 125.200\n" +
            "| posz        = 7\n" +
            "| gender      = Male\n" +
            "| race        = Human\n" +
            "| city        = Thais\n" +
            "| buysell     = yes\n" +
            "| sells       = {{Price to Buy |Axe |Battle Axe |Battle Hammer |Bone Sword |Brass Armor |Brass Helmet" +
            " |Brass Legs |Brass Shield |Carlin Sword |Chain Armor |Chain Helmet |Chain Legs |Club |Coat |Crowbar " +
            "|Dagger |Doublet |Dwarven Shield |Hand Axe |Iron Helmet |Jacket |Leather Armor |Leather Boots: 10 |Leather" +
            " Helmet |Leather Legs |Longsword |Mace |Morning Star |Plate Armor |Plate Shield |Rapier |Sabre |Scale Armor" +
            " |Short Sword |Sickle |Soldier Helmet |Spike Sword |Steel Helmet |Steel Shield |Studded Armor |Studded" +
            " Helmet |Studded Legs |Studded Shield |Sword |Throwing Knife |Two Handed Sword |Viking Helmet |Viking " +
            "Shield |War Hammer |Wooden Shield}}\n" +
            "| buys        = {{Price to Sell |Axe |Battle Axe |Battle Hammer |Battle Shield |Bone Club |Bone Sword" +
            " |Brass Armor |Brass Helmet |Brass Legs |Brass Shield |Carlin Sword |Chain Armor |Chain Helmet |Chain Legs" +
            " |Club |Coat |Copper Shield |Crowbar |Dagger |Double Axe |Doublet |Dwarven Shield |Fire Sword: 1000" +
            " |Halberd |Hand Axe: 4 |Hatchet |Iron Helmet |Jacket |Katana |Leather Armor |Leather Boots |Leather Helmet" +
            " |Leather Legs |Legion Helmet |Longsword |Mace |Magic Plate Armor: 6400;sayname |Morning Star |Orcish Axe" +
            " |Plate Armor |Plate Legs |Plate Shield |Rapier |Sabre |Scale Armor |Short Sword |Sickle |Small Axe " +
            "|Soldier Helmet |Spike Sword: 240 |Steel Helmet |Steel Shield |Studded Armor |Studded Club |Studded Helmet" +
            " |Studded Legs |Studded Shield |Swampling Club |Sword |Throwing Knife |Two Handed Sword |Viking Helmet " +
            "|Viking Shield |War Hammer: 470 |Wooden Shield}}\n" +
            "| sounds      = {{Sound List|Hello there, adventurer! Need a deal in weapons or armor? I'm your man!}}\n" +
            "| notes       = Sam is the Blacksmith of [[Thais]]. His real name is Samuel, but he prefers to be called " +
            "Sam. He was named after his grandfather.\n" +
            "He sells and buys many weapons and armors. Sam is one of the few [[NPC]]s that buys magic plate armors. " +
            "The only way to sell it to him is by typing \"Sell magic plate armor\".\n" +
            "His neighbor in Thais is [[Frodo]], innkeeper of the Frodo's Hut, which is clearly an [[Allusions#Samwise" +
            " Gamgee|allusion]] to the J.R.R. Tolkien's novel [[wikipedia:The Lord of the Rings|The Lord of The Rings]]" +
            " where [[Frodo]] and Sam are the main plot characters. He was the first NPC to see the Tibian light of day.\n" +
            "{{JSpoiler|Part of the [[Sam's Old Backpack Quest]], the [[Knight Outfits Quest]], and mission 9 of" +
            " [[What a Foolish Quest]].}}\n" +
            "}}\n";

    private static final String INFOBOX_OBJECT_TEXT = "{{Infobox Object|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name           = Blueberry Bush\n" +
            "| article        = a\n" +
            "| objectclass    = Bushes\n" +
            "| walkable       = no\n" +
            "| location       = Can be found all around [[Tibia]]. There are many Blueberry bushes in [[Greenshore]]," +
            " east from the wheat field. The [[Dryad Gardens]] also contain a lot of bushes.\n" +
            "| notes          = They are the source of the [[blueberry|blueberries]]. 'Use' the [[bush]] first, then " +
            "take the three remaining blueberries, however sometimes other players \"use\" the Blueberry Bush and don't" +
            " take them with themselves. Thus, there can be up to six blueberries in a bush. It takes one hour for the" +
            " blueberry bush to regenerate.\n" +
            "| notes2         = <br />{{JSpoiler|After using [[Blueberry]] Bushes 500 times, you will earn the" +
            " achievement [[Bluebarian]].}}\n" +
            "| implemented   = 7.1\n" +
            "}}\n";

    private static final String INFOBOX_OUTFIT_TEXT = "{{Infobox_Outfit|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name            = Pirate\n" +
            "| primarytype     = Quest\n" +
            "| premium         = yes\n" +
            "| outfit          = premium, see [[Pirate Outfits Quest]].\n" +
            "| addons          = premium, see [[Pirate Outfits Quest]].\n" +
            "| achievement     = Swashbuckler\n" +
            "| implemented     = 7.8\n" +
            "| artwork         = Pirate Outfits Artwork.jpg\n" +
            "| notes           = Pirate outfits are perfect for swabbing the deck or walking the plank. Quite dashing and great for sailing.\n" +
            "}}\n";

    private static final String INFOBOX_QUEST_TEXT = "{{Infobox Quest|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| implemented    = 6.61-6.97\n" +
            "| premium        = yes\n" +
            "| name           = The Paradox Tower Quest\n" +
            "| aka            = Riddler Quest, Mathemagics Quest\n" +
            "| reward         = Up to two of the following: 10k [[gp]], [[Wand of Cosmic Energy]],  32 [[Talon]]s," +
            " [[Phoenix Egg]] and the [[achievement]] [[Mathemagician]]\n" +
            "| location       = [[Paradox Tower]] near [[Kazordoon]]\n" +
            "| lvl            = 30\n" +
            "| lvlrec         = 50+\n" +
            "| log            = yes\n" +
            "| transcripts    = yes\n" +
            "| dangers        = [[Wyvern]]s<br /> ([[Mintwallin]]): [[Minotaur]]s, [[Minotaur Archer]]s, [[Minotaur" +
            " Guard]]s, [[Minotaur Mage]]s<br>([[Hellgate]]): [[Skeleton]]s, [[Ghoul]]s, [[Bonelord]]s, maybe [[Elder" +
            " Bonelord]]<br />\n" +
            "([[Plains of Havoc]]): [[Skeleton]]s, [[Ghoul]]s, [[Demon Skeleton]]s, [[Orc Berserker]], [[Orc" +
            " Spearman]], maybe [[Cyclops]] and [[Giant Spider]]\n" +
            "| legend         = Surpass the wrath of a madman and subject yourself to his twisted taunting.\n" +
            "}}\n";

    private static final String INFOBOX_SPELL_TEXT = "{{Infobox Spell|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name          = Light Healing\n" +
            "| type          = Instant\n" +
            "| subclass      = Healing\n" +
            "| words         = exura\n" +
            "| premium       = no\n" +
            "| mana          = 20\n" +
            "| levelrequired = 8\n" +
            "| cooldown      = 1\n" +
            "| cooldowngroup = 1\n" +
            "| voc           = [[Paladin]]s, [[Druid]]s and [[Sorcerer]]s\n" +
            "| d-abd         = [[Maealil]]\n" +
            "| p-abd         = [[Maealil]]\n" +
            "| spellcost     = 0\n" +
            "| effect        = Restores a small amount of [[HP|health]]. (Cures [[paralysis]].)\n" +
            "| notes         = A weak, but popular healing spell. It was the only healing spell for knights for many" +
            " years until [[Wound Cleansing]] was introduced in late 2007. The number of [[hp]] it can heal depends " +
            "upon your level and magic level (and is around 1/10th of your UH power). It is useful if you are trying" +
            " to raise your [[Magic level]] or to get rid of [[Paralysis]]. [[Knight]]s can't use it since " +
            "[[Updates/8.7|Winter Update 2010]]. Since [[Updates/9.8|Winter Update 2012]] this spell is free. It used " +
            "to cost 170 [[gp]].\n" +
            "[[File:Exura in low hp.png|thumb|left|168px|A player performing the Light Healing spell]]\n" +
            "}}\n";

    private static final String INFOBOX_STREET_TEXT = "{{Infobox Street\n" +
            "| name           = Sugar Street\n" +
            "| implemented    = 7.8\n" +
            "| city           = Liberty Bay\n" +
            "| floor          = \n" +
            "| notes          = {{StreetStyles|Sugar Street}} is in west and central [[Liberty Bay]]. It touches " +
            "{{StreetStyles|Harvester's Haven}} to the north, '''Smuggler Backyard''' and {{StreetStyles|Shady Trail}}" +
            " to the south, and {{StreetStyles|Marble Lane}} and {{StreetStyles|Admiral's Avenue}} to the east.\n" +
            "\n" +
            "Buildings and NPCs from south to north and west to east:<br/>\n" +
            "'''South-West:'''\n" +
            "* [[Sugar Street 1]]\n" +
            "* [[Sugar Street 2]]\n" +
            "* [[Sugar Street 3a]]\n" +
            "* [[Sugar Street 4a]]\n" +
            "\n" +
            "'''North-West:'''\n" +
            "* [[Sugar Street 3b]]\n" +
            "\n" +
            "'''North:'''\n" +
            "* [[Ivy Cottage]]\n" +
            "\n" +
            "'''Central:'''\n" +
            "* [[Sugar Street 4b]]\n" +
            "* [[Sugar Street 4c]]\n" +
            "* [[Sugar Street 4d]]\n" +
            "\n" +
            "'''East:'''\n" +
            "* [[Peggy]], [[Furniture|Furniture Store]]\n" +
            "* [[Sugar Street 5]]\n" +
            "}}\n";
}