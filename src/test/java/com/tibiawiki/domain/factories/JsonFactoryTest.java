package com.tibiawiki.domain.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibiawiki.domain.enums.Article;
import com.tibiawiki.domain.enums.BestiaryClass;
import com.tibiawiki.domain.enums.BestiaryLevel;
import com.tibiawiki.domain.enums.BestiaryOccurrence;
import com.tibiawiki.domain.enums.BookType;
import com.tibiawiki.domain.enums.BuildingType;
import com.tibiawiki.domain.enums.City;
import com.tibiawiki.domain.enums.Gender;
import com.tibiawiki.domain.enums.Grade;
import com.tibiawiki.domain.enums.Hands;
import com.tibiawiki.domain.enums.ItemClass;
import com.tibiawiki.domain.enums.KeyType;
import com.tibiawiki.domain.enums.Rarity;
import com.tibiawiki.domain.enums.Spawntype;
import com.tibiawiki.domain.enums.SpellSubclass;
import com.tibiawiki.domain.enums.SpellType;
import com.tibiawiki.domain.enums.WeaponType;
import com.tibiawiki.domain.enums.YesNo;
import com.tibiawiki.domain.objects.Achievement;
import com.tibiawiki.domain.objects.Book;
import com.tibiawiki.domain.objects.Building;
import com.tibiawiki.domain.objects.Corpse;
import com.tibiawiki.domain.objects.Creature;
import com.tibiawiki.domain.objects.Effect;
import com.tibiawiki.domain.objects.HuntingPlace;
import com.tibiawiki.domain.objects.HuntingPlaceSkills;
import com.tibiawiki.domain.objects.Item;
import com.tibiawiki.domain.objects.Key;
import com.tibiawiki.domain.objects.Location;
import com.tibiawiki.domain.objects.LootItem;
import com.tibiawiki.domain.objects.Missile;
import com.tibiawiki.domain.objects.Mount;
import com.tibiawiki.domain.objects.NPC;
import com.tibiawiki.domain.objects.Outfit;
import com.tibiawiki.domain.objects.Percentage;
import com.tibiawiki.domain.objects.Quest;
import com.tibiawiki.domain.objects.Spell;
import com.tibiawiki.domain.objects.Street;
import com.tibiawiki.domain.objects.TibiaObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
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

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Building() {
        final Building building = makeBuilding();
        String result = target.convertJsonToInfoboxPartOfArticle(makeBuildingJson(building), building.fieldOrder());
        assertThat(result, is(INFOBOX_BUILDING_TEXT));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Corpse() {
        final Corpse corpse = makeCorpse();
        String result = target.convertJsonToInfoboxPartOfArticle(makeCorpseJson(corpse), corpse.fieldOrder());
        assertThat(result, is(INFOBOX_CORPSE_TEXT));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Creature() {
        final Creature creature = makeCreature();
        String result = target.convertJsonToInfoboxPartOfArticle(makeCreatureJson(creature), creature.fieldOrder());
        assertThat(result, is(INFOBOX_CREATURE_TEXT));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Effect() {
        final Effect effect = makeEffect();
        String result = target.convertJsonToInfoboxPartOfArticle(makeEffectJson(effect), effect.fieldOrder());
        assertThat(result, is(INFOBOX_EFFECT_TEXT));
    }

    private static final String INFOBOX_CREATURE_TEXT = "{{Infobox Creature|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name           = Dragon\n" +
            "| article        = a\n" +
            "| actualname     = dragon\n" +
            "| plural         = dragons\n" +
            "| hp             = 1000\n" +
            "| exp            = 700\n" +
            "| armor          = 25\n" +
            "| summon         = --\n" +
            "| convince       = --\n" +
            "| illusionable   = yes\n" +
            "| creatureclass  = Reptiles\n" +
            "| primarytype    = Dragons\n" +
            "| bestiaryclass  = Dragon\n" +
            "| bestiarylevel  = Medium\n" +
            "| occurrence     = Common\n" +
            "| spawntype      = Regular, Raid\n" +
            "| isboss         = no\n" +
            "| isarenaboss    = no\n" +
            "| abilities      = [[Melee]] (0-120), [[Fire Wave]] (100-170), [[Great Fireball]] (60-140), [[Self-Healing]] (40-70)\n" +
            "| maxdmg         = 430\n" +
            "| pushable       = no\n" +
            "| pushobjects    = yes\n" +
            "| walksaround    = None\n" +
            "| walksthrough   = Fire, Energy, Poison\n" +
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
            "| bestiarytext   = Dragons were\n" +
            "| sounds         = {{Sound List|FCHHHHH|GROOAAARRR}}\n" +
            "| implemented    = Pre-6.0\n" +
            "| notes          = Dragons are\n" +
            "| behaviour      = Dragons are\n" +
            "| runsat         = 300\n" +
            "| speed          = 86\n" +
            "| strategy       = '''All''' [[player]]s\n" +
            "| location       = [[Thais]] [[Ancient Temple]], [[Darashia Dragon Lair]], [[Mount Sternum Dragon Cave]]," +
            " [[Mintwallin]], deep in [[Fibula Dungeon]], [[Kazordoon Dragon Lair]] (near [[Dwarf Bridge]]), [[Plains" +
            " of Havoc]], [[Elven Bane]] castle, [[Maze of Lost Souls]], southern cave and dragon tower in" +
            " [[Shadowthorn]], [[Orc Fortress]], [[Venore]] [[Dragon Lair]], [[Pits of Inferno]], [[Behemoth Quest]]" +
            " room in [[Edron]], [[Hero Cave]], deep [[Cyclopolis]], [[Edron Dragon Lair]], [[Goroma]], [[Ankrahmun" +
            " Dragon Lair]]s, [[Draconia]], [[Dragonblaze Peaks]], some [[Ankrahmun Tombs]], underground of [[Fenrock]]" +
            " (on the way to [[Beregar]]), [[Krailos Steppe]] and [[Crystal Lakes]].\n" +
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
            "| history        = Dragons are\n" +
            "}}\n";

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Item() {
        final Item item = makeItem();
        String result = target.convertJsonToInfoboxPartOfArticle(makeItemJson(item), item.fieldOrder());
        assertThat(result, is(INFOBOX_ITEM_TEXT));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Key() {
        final Key key = makeKey();
        String result = target.convertJsonToInfoboxPartOfArticle(makeKeyJson(key), key.fieldOrder());
        assertThat(result, is(INFOBOX_KEY_TEXT));
    }

    private static final String INFOBOX_HUNT_TEXT = "{{Infobox Hunt|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Hero Cave\n" +
            "| image        = Hero\n" +
            "| implemented  = 6.4\n" +
            "| city         = Edron\n" +
            "| location     = North of [[Edron]], [http://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].\n" +
            "| vocation     = All vocations.\n" +
            "| lvlknights   = 70\n" +
            "| lvlpaladins  = 60\n" +
            "| lvlmages     = 50\n" +
            "| skknights    = 75\n" +
            "| skpaladins   = 80\n" +
            "| skmages      = 1\n" +
            "| defknights   = 75\n" +
            "| defpaladins  = 1\n" +
            "| defmages     = 1\n" +
            "| lowerlevels  = \n" +
            "    {{Infobox Hunt Skills\n" +
            "    | areaname    = Demons\n" +
            "    | lvlknights  = 130\n" +
            "    | lvlpaladins = 130\n" +
            "    | lvlmages    = 130\n" +
            "    | skknights   = 1\n" +
            "    | skpaladins  = 1\n" +
            "    | skmages     = 1\n" +
            "    | defknights  = 1\n" +
            "    | defpaladins = 1\n" +
            "    | defmages    = 1\n" +
            "    }}\n" +
            "    {{Infobox Hunt Skills\n" +
            "    | areaname    = Another Area (Past Teleporter)\n" +
            "    | lvlknights  = 230\n" +
            "    | lvlpaladins = 230\n" +
            "    | lvlmages    = 230\n" +
            "    | skknights   = 2\n" +
            "    | skpaladins  = 2\n" +
            "    | skmages     = 2\n" +
            "    | defknights  = 2\n" +
            "    | defpaladins = 2\n" +
            "    | defmages    = 2\n" +
            "    }}\n" +
            "| loot         = Good\n" +
            "| exp          = Good\n" +
            "| bestloot     = Reins\n" +
            "| bestloot2    = Foobar\n" +
            "| bestloot3    = Foobar\n" +
            "| bestloot4    = Foobar\n" +
            "| bestloot5    = Foobar\n" +
            "| map          = Hero Cave 3.png\n" +
            "| map2         = Hero Cave 6.png\n" +
            "}}\n";

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Missile() {
        final Missile missile = makeMissile();
        String result = target.convertJsonToInfoboxPartOfArticle(makeMissileJson(missile), missile.fieldOrder());
        assertThat(result, is(INFOBOX_MISSILE_TEXT));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Mount() {
        final Mount mount = makeMount();
        String result = target.convertJsonToInfoboxPartOfArticle(makeMountJson(mount), mount.fieldOrder());
        assertThat(result, is(INFOBOX_MOUNT_TEXT));
    }

    private static final String INFOBOX_ITEM_TEXT = "{{Infobox Item|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name          = Carlin Sword\n" +
            "| article       = a\n" +
            "| actualname    = carlin sword\n" +
            "| plural        = ?\n" +
            "| itemid        = 3283\n" +
            "| marketable    = yes\n" +
            "| usable        = yes\n" +
            "| sprites       = {{Frames|{{Frame Sprite|55266}}}}\n" +
            "| flavortext    = Foobar\n" +
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
            "| value         = 118\n" +
            "| npcvalue      = 118\n" +
            "| npcprice      = 473\n" +
            "| npcvaluerook  = 0\n" +
            "| npcpricerook  = 0\n" +
            "| buyfrom       = Baltim, Brengus, Cedrik,\n" +
            "| sellto        = Baltim, Brengus, Cedrik, Esrik,\n" +
            "| notes         = If you have one of these \n" +
            "}}\n";

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Object() {
        final TibiaObject tibiaObject = makeTibiaObject();
        String result = target.convertJsonToInfoboxPartOfArticle(makeTibiaObjectJson(tibiaObject), tibiaObject.fieldOrder());
        assertThat(result, is(INFOBOX_OBJECT_TEXT));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Outfit() {
        final Outfit outfit = makeOutfit();
        String result = target.convertJsonToInfoboxPartOfArticle(makeOutfitJson(outfit), outfit.fieldOrder());
        assertThat(result, is(INFOBOX_OUTFIT_TEXT));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Quest() {
        final Quest quest = makeQuest();
        String result = target.convertJsonToInfoboxPartOfArticle(makeQuestJson(quest), quest.fieldOrder());
        assertThat(result, is(INFOBOX_QUEST_TEXT));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Spell() {
        final Spell spell = makeSpell();
        String result = target.convertJsonToInfoboxPartOfArticle(makeSpellJson(spell), spell.fieldOrder());
        assertThat(result, is(INFOBOX_SPELL_TEXT));
    }

    private static final String INFOBOX_KEY_TEXT = "{{Infobox Key|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| number       = 4055\n" +
            "| aka          = Panpipe Quest Key\n" +
            "| primarytype  = Silver\n" +
            "| location     = [[Jakundaf Desert]]\n" +
            "| value        = Negotiable\n" +
            "| npcvalue     = 0\n" +
            "| npcprice     = 0\n" +
            "| buyfrom      = --\n" +
            "| sellto       = --\n" +
            "| origin       = Hidden in a rock south of the Desert Dungeon entrance.\n" +
            "| shortnotes   = Access to the [[Panpipe Quest]].\n" +
            "| longnotes    = Allows you to open the door ([http://tibia.wikia.com/wiki/Mapper?coords=127.131,125.129,8,3,1,1 here]) to the [[Panpipe Quest]].\n" +
            "}}\n";

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
            "| name         = Theater Avenue 8b\n" +
            "| implemented  = Pre-6.0\n" +
            "| type         = House\n" +
            "| location     = South-east of depot, two floors up.\n" +
            "| posx         = 126.101\n" +
            "| posy         = 124.48\n" +
            "| posz         = 5\n" +
            "| street       = Theater Avenue\n" +
            "| houseid      = 20315\n" +
            "| size         = 26\n" +
            "| beds         = 3\n" +
            "| rent         = 1370\n" +
            "| city         = Carlin\n" +
            "| openwindows  = 3\n" +
            "| floors       = 1\n" +
            "| rooms        = 1\n" +
            "| furnishings  = 1 [[Wall Lamp]].\n" +
            "| notes        = \n" +
            "| image        = [[File:Theater Avenue 8b.png]]\n" +
            "}}\n";

    private Building makeBuilding() {
        return Building.builder()
                .name("Theater Avenue 8b")
                .implemented("Pre-6.0")
                .type(BuildingType.House)
                .location("South-east of depot, two floors up.")
                .posx("126.101")
                .posy("124.48")
                .posz("5")
                .street("Theater Avenue")
                .houseid(20315)
                .size(26)
                .beds(3)
                .rent(1370)
                .city(City.CARLIN)
                .openwindows(3)
                .floors(1)
                .rooms(1)
                .furnishings("1 [[Wall Lamp]].")
                .notes("")
                .image("[[File:Theater Avenue 8b.png]]")
                .build();
    }

    private JSONObject makeBuildingJson(Building building) {
        return new JSONObject(objectMapper.convertValue(building, Map.class)).put("templateType", "Building");
    }

    private static final String INFOBOX_CORPSE_TEXT = "{{Infobox Corpse|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Dead Rat\n" +
            "| article      = a\n" +
            "| liquid       = [[Blood]]\n" +
            "| 1decaytime   = 5 minutes.\n" +
            "| 2decaytime   = 5 minutes.\n" +
            "| 3decaytime   = 60 seconds.\n" +
            "| 1volume      = 5\n" +
            "| 1weight      = 63.00\n" +
            "| 2weight      = 44.00\n" +
            "| 3weight      = 30.00\n" +
            "| corpseof     = [[Rat]], [[Cave Rat]], [[Munster]]\n" +
            "| sellto       = [[Tom]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Seymour]] ([[Rookgaard]]) '''2''' [[gp]]" +
            "<br>[[Billy]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Humgolf]] ([[Kazordoon]]) '''2''' [[gp]]<br>\n" +
            "[[Baxter]] ([[Thais]]) '''1''' [[gp]]<br>\n" +
            "| notes        = These corpses are commonly used by low level players on [[Rookgaard]] to earn some gold" +
            " for better [[equipment]]. Only fresh corpses are accepted, rotted corpses are ignored.\n" +
            "| implemented  = Pre-6.0\n" +
            "}}\n";

    private Corpse makeCorpse() {
        return Corpse.builder()
                .name("Dead Rat")
                .article(Article.A)
                .liquid("[[Blood]]")
                .firstVolume(5)
                .firstWeight(BigDecimal.valueOf(63.00).setScale(2, RoundingMode.HALF_UP))
                .secondWeight(BigDecimal.valueOf(44.00).setScale(2, RoundingMode.HALF_UP))
                .thirdWeight(BigDecimal.valueOf(30.00).setScale(2, RoundingMode.HALF_UP))
                .firstDecaytime("5 minutes.")
                .secondDecaytime("5 minutes.")
                .thirdDecaytime("60 seconds.")
                .corpseof("[[Rat]], [[Cave Rat]], [[Munster]]")
                .sellto("[[Tom]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Seymour]] ([[Rookgaard]]) '''2''' [[gp]]" +
                        "<br>[[Billy]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Humgolf]] ([[Kazordoon]]) '''2''' [[gp]]<br>" +
                        "\n[[Baxter]] ([[Thais]]) '''1''' [[gp]]<br>")
                .implemented("Pre-6.0")
                .notes("These corpses are commonly used by low level players on [[Rookgaard]] to earn some gold" +
                        " for better [[equipment]]. Only fresh corpses are accepted, rotted corpses are ignored.")
                .build();
    }

    private JSONObject makeCorpseJson(Corpse corpse) {
        return new JSONObject(objectMapper.convertValue(corpse, Map.class)).put("templateType", "Corpse");
    }

    private static final String INFOBOX_LOCATION_TEXT = "{{Infobox Geography\n" +
            "| implemented  = Pre-6.0\n" +
            "| ruler        = [[King Tibianus]]\n" +
            "| population   = {{PAGESINCATEGORY:Thais NPCs|pages}}\n" +
            "| near         = [[Fibula]], [[Mintwallin]], [[Greenshore]], [[Mount Sternum]]\n" +
            "| organization = [[Thieves Guild]], [[Tibian Bureau of Investigation]], [[Inquisition]]\n" +
            "| map          = [[File:Map_thais.jpg]]\n" +
            "| map2         = [[File:Thais.PNG]]\n" +
            "}}\n";
    private static final String INFOBOX_MISSILE_TEXT = "{{Infobox Missile|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Throwing Cake Missile\n" +
            "| implemented  = 7.9\n" +
            "| missileid    = 42\n" +
            "| primarytype  = Throwing Weapon\n" +
            "| shotby       = [[Undead Jester]]'s attack and probably by throwing a [[Throwing Cake]].\n" +
            "| notes        = This missile is followed by the [[Cream Cake Effect]]: [[File:Cream Cake Effect.gif]]\n" +
            "}}\n";

    private JSONObject makeCreatureJson(Creature creature) {
        return new JSONObject(objectMapper.convertValue(creature, Map.class)).put("templateType", "Creature");
    }

    private static final String INFOBOX_EFFECT_TEXT = "{{Infobox Effect|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Fireball Effect\n" +
            "| effectid     = 7\n" +
            "| primarytype  = Attack\n" +
            "| lightradius  = 6\n" +
            "| lightcolor   = 208\n" +
            "| causes       = *[[Fireball]] and [[Great Fireball]];\n" +
            "| effect       = [[Fire Damage]] on target or nothing.\n" +
            "}}\n";


    private Effect makeEffect() {
        return Effect.builder()
                .name("Fireball Effect")
                .effectid(7)
                .primarytype("Attack")
                .lightcolor(208)
                .lightradius(6)
                .causes("*[[Fireball]] and [[Great Fireball]];")
                .effect("[[Fire Damage]] on target or nothing.")
                .build();
    }

    private JSONObject makeEffectJson(Effect effect) {
        return new JSONObject(objectMapper.convertValue(effect, Map.class)).put("templateType", "Effect");
    }

    private static final String INFOBOX_MOUNT_TEXT = "{{Infobox Mount|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name          = Donkey\n" +
            "| speed         = 10\n" +
            "| taming_method = Use a [[Bag of Apple Slices]] on a creature transformed into Donkey.\n" +
            "| achievement   = Loyal Lad\n" +
            "| implemented   = 9.1\n" +
            "| notes         = Go to [[Incredibly Old Witch]]'s house,\n" +
            "}}\n";
    private static final String INFOBOX_NPC_TEXT = "{{Infobox NPC|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Sam\n" +
            "| job          = Artisan\n" +
            "| job2         = Weapon Shopkeeper\n" +
            "| job3         = Armor Shopkeeper\n" +
            "| location     = [[Temple Street]] in [[Thais]].\n" +
            "| city         = Thais\n" +
            "| posx         = 126.104\n" +
            "| posy         = 125.200\n" +
            "| posz         = 7\n" +
            "| gender       = Male\n" +
            "| race         = Human\n" +
            "| buysell      = yes\n" +
            "| buys         = {{Price to Sell |Axe\n" +
            "| sells        = {{Price to Buy |Axe\n" +
            "| sounds       = {{Sound List|Hello there, adventurer! Need a deal in weapons or armor? I'm your man!}}\n" +
            "| implemented  = Pre-6.0\n" +
            "| notes        = Sam is the Blacksmith of [[Thais]].\n" +
            "}}\n";

    private JSONObject makeHuntingPlaceJson(HuntingPlace huntingPlace) {
        return new JSONObject(objectMapper.convertValue(huntingPlace, Map.class)).put("templateType", "Hunt");
    }

    private static final String INFOBOX_OBJECT_TEXT = "{{Infobox Object|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Blueberry Bush\n" +
            "| article      = a\n" +
            "| objectclass  = Bushes\n" +
            "| implemented  = 7.1\n" +
            "| walkable     = no\n" +
            "| location     = Can be found all around [[Tibia]].\n" +
            "| notes        = They are the source of the [[blueberry|blueberries]].\n" +
            "| notes2       = <br />{{JSpoiler|After using [[Blueberry]] Bushes 500 times,\n" +
            "}}\n";
    private static final String INFOBOX_OUTFIT_TEXT = "{{Infobox Outfit|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Pirate\n" +
            "| primarytype  = Quest\n" +
            "| premium      = yes\n" +
            "| outfit       = premium, see [[Pirate Outfits Quest]].\n" +
            "| addons       = premium, see [[Pirate Outfits Quest]].\n" +
            "| achievement  = Swashbuckler\n" +
            "| implemented  = 7.8\n" +
            "| artwork      = Pirate Outfits Artwork.jpg\n" +
            "| notes        = Pirate outfits are perfect for swabbing the deck or walking the plank. Quite dashing and great for sailing.\n" +
            "}}\n";

    private JSONObject makeItemJson(Item item) {
        return new JSONObject(objectMapper.convertValue(item, Map.class)).put("templateType", "Item");
    }

    private static final String INFOBOX_QUEST_TEXT = "{{Infobox Quest|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = The Paradox Tower Quest\n" +
            "| aka          = Riddler Quest, Mathemagics Quest\n" +
            "| reward       = Up to two of the following:\n" +
            "| location     = [[Paradox Tower]] near [[Kazordoon]]\n" +
            "| lvl          = 30\n" +
            "| lvlrec       = 50\n" +
            "| log          = yes\n" +
            "| premium      = yes\n" +
            "| transcripts  = yes\n" +
            "| dangers      = [[Wyvern]]s<br /> ([[Mintwallin]]): [[Minotaur]]s,\n" +
            "| legend       = Surpass the wrath of a madman and subject yourself to his twisted taunting.\n" +
            "| implemented  = 6.61-6.97\n" +
            "}}\n";

    private Key makeKey() {
        return Key.builder()
                .number("4055")
                .aka("Panpipe Quest Key")
                .primarytype(KeyType.SILVER)
                .location("[[Jakundaf Desert]]")
                .value("Negotiable")
                .npcvalue(0)
                .npcprice(0)
                .buyfrom("--")
                .sellto("--")
                .origin("Hidden in a rock south of the Desert Dungeon entrance.")
                .shortnotes("Access to the [[Panpipe Quest]].")
                .longnotes("Allows you to open the door ([http://tibia.wikia.com/wiki/Mapper?coords=127.131,125.129,8,3,1,1 here]) to the [[Panpipe Quest]].")
                .build();
    }

    private JSONObject makeKeyJson(Key key) {
        return new JSONObject(objectMapper.convertValue(key, Map.class)).put("templateType", "Key");
    }

    private static final String INFOBOX_STREET_TEXT = "{{Infobox Street\n" +
            "| name         = Sugar Street\n" +
            "| implemented  = 7.8\n" +
            "| city         = Liberty Bay\n" +
            "| notes        = {{StreetStyles|Sugar Street}} is in west\n" +
            "}}\n";

    private Location makeLocation() {
        return Location.builder()
                .ruler("[[King Tibianus]]")
                .implemented("Pre-6.0")
                .population("{{PAGESINCATEGORY:Thais NPCs|pages}}")
                .organization("[[Thieves Guild]], [[Tibian Bureau of Investigation]], [[Inquisition]]")
                .near("[[Fibula]], [[Mintwallin]], [[Greenshore]], [[Mount Sternum]]")
                .map("[[File:Map_thais.jpg]]")
                .map2("[[File:Thais.PNG]]")
                .build();
    }

    private JSONObject makeLocationJson(Location location) {
        return new JSONObject(objectMapper.convertValue(location, Map.class)).put("templateType", "Geography");
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_HuntingPlace() {
        final HuntingPlace huntingPlace = makeHuntingPlace();
        String result = target.convertJsonToInfoboxPartOfArticle(makeHuntingPlaceJson(huntingPlace), huntingPlace.fieldOrder());
        assertThat(result, is(INFOBOX_HUNT_TEXT));
    }

    private Missile makeMissile() {
        return Missile.builder()
                .name("Throwing Cake Missile")
                .implemented("7.9")
                .missileid(42)
                .primarytype("Throwing Weapon")
                .shotby("[[Undead Jester]]'s attack and probably by throwing a [[Throwing Cake]].")
                .notes("This missile is followed by the [[Cream Cake Effect]]: [[File:Cream Cake Effect.gif]]")
                .build();
    }

    private JSONObject makeMissileJson(Missile missile) {
        return new JSONObject(objectMapper.convertValue(missile, Map.class)).put("templateType", "Missile");
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Location() {
        final Location location = makeLocation();
        String result = target.convertJsonToInfoboxPartOfArticle(makeLocationJson(location), location.fieldOrder());
        assertThat(result, is(INFOBOX_LOCATION_TEXT));
    }

    private Mount makeMount() {
        return Mount.builder()
                .name("Donkey")
                .speed(10)
                .tamingMethod("Use a [[Bag of Apple Slices]] on a creature transformed into Donkey.")
                .implemented("9.1")
                .achievement("Loyal Lad")
                .notes("Go to [[Incredibly Old Witch]]'s house,")
                .build();
    }

    private JSONObject makeMountJson(Mount mount) {
        return new JSONObject(objectMapper.convertValue(mount, Map.class)).put("templateType", "Mount");
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_NPC() {
        final NPC npc = makeNPC();
        String result = target.convertJsonToInfoboxPartOfArticle(makeNPCJson(npc), npc.fieldOrder());
        assertThat(result, is(INFOBOX_NPC_TEXT));
    }

    private NPC makeNPC() {
        return NPC.builder()
                .name("Sam")
                .implemented("Pre-6.0")
                .job("Artisan")
                .job2("Weapon Shopkeeper")
                .job3("Armor Shopkeeper")
                .location("[[Temple Street]] in [[Thais]].")
                .posx(BigDecimal.valueOf(126.104).setScale(3, RoundingMode.HALF_UP))
                .posy(BigDecimal.valueOf(125.200).setScale(3, RoundingMode.HALF_UP))
                .posz(7)
                .gender(Gender.MALE)
                .race("Human")
                .city(City.THAIS)
                .buysell(YesNo.YES_LOWERCASE)
                .sells("{{Price to Buy |Axe")
                .buys("{{Price to Sell |Axe")
                .sounds(Collections.singletonList("Hello there, adventurer! Need a deal in weapons or armor? I'm your man!"))
                .notes("Sam is the Blacksmith of [[Thais]].")
                .build();
    }

    private JSONObject makeNPCJson(NPC npc) {
        return new JSONObject(objectMapper.convertValue(npc, Map.class)).put("templateType", "NPC");
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Street() {
        final Street street = makeStreet();
        String result = target.convertJsonToInfoboxPartOfArticle(makeStreetJson(street), street.fieldOrder());
        assertThat(result, is(INFOBOX_STREET_TEXT));
    }

    private TibiaObject makeTibiaObject() {
        return TibiaObject.builder()
                .name("Blueberry Bush")
                .article(Article.A)
                .objectclass("Bushes")
                .walkable(YesNo.NO_LOWERCASE)
                .location("Can be found all around [[Tibia]].")
                .notes("They are the source of the [[blueberry|blueberries]].")
                .notes2("<br />{{JSpoiler|After using [[Blueberry]] Bushes 500 times,")
                .implemented("7.1")
                .build();
    }

    private JSONObject makeTibiaObjectJson(TibiaObject tibiaObject) {
        return new JSONObject(objectMapper.convertValue(tibiaObject, Map.class)).put("templateType", "Object");
    }

    private Creature makeCreature() {
        return Creature.builder()
                .name("Dragon")
                .article(Article.A)
                .actualname("dragon")
                .plural("dragons")
                .implemented("Pre-6.0")
                .hitPoints("1000")
                .experiencePoints("700")
                .summon("--")
                .convince("--")
                .illusionable(YesNo.YES_LOWERCASE)
                .creatureclass("Reptiles")
                .primarytype("Dragons")
                .bestiaryclass(BestiaryClass.DRAGON)
                .bestiarylevel(BestiaryLevel.Medium)
                .occurrence(BestiaryOccurrence.COMMON)
                .spawntype(Arrays.asList(Spawntype.REGULAR, Spawntype.RAID))
                .isboss(YesNo.NO_LOWERCASE)
                .isarenaboss(YesNo.NO_LOWERCASE)
                .abilities("[[Melee]] (0-120), [[Fire Wave]] (100-170), [[Great Fireball]] (60-140), [[Self-Healing]] (40-70)")
                .maxdmg("430")
                .armor("25")
                .pushable(YesNo.NO_LOWERCASE)
                .pushobjects(YesNo.YES_LOWERCASE)
                .walksthrough("Fire, Energy, Poison")
                .walksaround("None")
                .paraimmune(YesNo.YES_LOWERCASE)
                .senseinvis(YesNo.YES_LOWERCASE)
                .physicalDmgMod(Percentage.of(100))
                .holyDmgMod(Percentage.of(100))
                .deathDmgMod(Percentage.of(100))
                .fireDmgMod(Percentage.of(0))
                .energyDmgMod(Percentage.of(80))
                .iceDmgMod(Percentage.of(110))
                .earthDmgMod(Percentage.of(20))
                .drownDmgMod(Percentage.of("100%?"))
                .hpDrainDmgMod(Percentage.of("100%?"))
                .bestiaryname("dragon")
                .bestiarytext("Dragons were")
                .sounds(Arrays.asList("FCHHHHH", "GROOAAARRR"))
                .notes("Dragons are")
                .behaviour("Dragons are")
                .runsat("300")
                .speed("86")
                .location("[[Thais]] [[Ancient Temple]], [[Darashia Dragon Lair]], [[Mount Sternum Dragon Cave]]," +
                        " [[Mintwallin]], deep in [[Fibula Dungeon]], [[Kazordoon Dragon Lair]] (near [[Dwarf Bridge]]), [[Plains" +
                        " of Havoc]], [[Elven Bane]] castle, [[Maze of Lost Souls]], southern cave and dragon tower in" +
                        " [[Shadowthorn]], [[Orc Fortress]], [[Venore]] [[Dragon Lair]], [[Pits of Inferno]], [[Behemoth Quest]]" +
                        " room in [[Edron]], [[Hero Cave]], deep [[Cyclopolis]], [[Edron Dragon Lair]], [[Goroma]], [[Ankrahmun" +
                        " Dragon Lair]]s, [[Draconia]], [[Dragonblaze Peaks]], some [[Ankrahmun Tombs]], underground of [[Fenrock]]" +
                        " (on the way to [[Beregar]]), [[Krailos Steppe]] and [[Crystal Lakes]].")
                .strategy("'''All''' [[player]]s")
                .loot(Arrays.asList(
                        LootItem.builder().amount("0-105").itemName("Gold Coin").build(),
                        LootItem.builder().amount("0-3").itemName("Dragon Ham").build(),
                        LootItem.builder().itemName("Steel Shield").build(),
                        LootItem.builder().itemName("Crossbow").build(),
                        LootItem.builder().itemName("Dragon's Tail").build(),
                        LootItem.builder().amount("0-10").itemName("Burst Arrow").build(),
                        LootItem.builder().itemName("Longsword").rarity(Rarity.SEMI_RARE).build(),
                        LootItem.builder().itemName("Steel Helmet").rarity(Rarity.SEMI_RARE).build(),
                        LootItem.builder().itemName("Broadsword").rarity(Rarity.SEMI_RARE).build(),
                        LootItem.builder().itemName("Plate Legs").rarity(Rarity.SEMI_RARE).build(),
                        LootItem.builder().itemName("Green Dragon Leather").rarity(Rarity.RARE).build(),
                        LootItem.builder().itemName("Wand of Inferno").rarity(Rarity.RARE).build(),
                        LootItem.builder().itemName("Strong Health Potion").rarity(Rarity.RARE).build(),
                        LootItem.builder().itemName("Green Dragon Scale").rarity(Rarity.RARE).build(),
                        LootItem.builder().itemName("Double Axe").rarity(Rarity.RARE).build(),
                        LootItem.builder().itemName("Dragon Hammer").rarity(Rarity.RARE).build(),
                        LootItem.builder().itemName("Serpent Sword").rarity(Rarity.RARE).build(),
                        LootItem.builder().itemName("Small Diamond").rarity(Rarity.VERY_RARE).build(),
                        LootItem.builder().itemName("Dragon Shield").rarity(Rarity.VERY_RARE).build(),
                        LootItem.builder().itemName("Life Crystal").rarity(Rarity.VERY_RARE).build(),
                        LootItem.builder().itemName("Dragonbone Staff").rarity(Rarity.VERY_RARE).build()
                ))
                .history("Dragons are")
                .build();
    }

    private Outfit makeOutfit() {
        return Outfit.builder()
                .name("Pirate")
                .primarytype("Quest")
                .premium(YesNo.YES_LOWERCASE)
                .outfit("premium, see [[Pirate Outfits Quest]].")
                .addons("premium, see [[Pirate Outfits Quest]].")
                .achievement("Swashbuckler")
                .implemented("7.8")
                .artwork("Pirate Outfits Artwork.jpg")
                .notes("Pirate outfits are perfect for swabbing the deck or walking the plank. Quite dashing and great for sailing.")
                .build();
    }

    private JSONObject makeOutfitJson(Outfit outfit) {
        return new JSONObject(objectMapper.convertValue(outfit, Map.class)).put("templateType", "Outfit");
    }

    private HuntingPlace makeHuntingPlace() {
        return HuntingPlace.builder()
                .name("Hero Cave")
                .image("Hero")
                .implemented("6.4")
                .city(City.EDRON)
                .location("North of [[Edron]], [http://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].")
                .vocation("All vocations.")
                .lvlknights("70")
                .lvlpaladins("60")
                .lvlmages("50")
                .skknights("75")
                .skpaladins("80")
                .skmages("1")
                .defknights("75")
                .defpaladins("1")
                .defmages("1")
                .lowerlevels(Arrays.asList(HuntingPlaceSkills.builder()
                                .areaname("Demons")
                                .lvlknights("130")
                                .lvlpaladins("130")
                                .lvlmages("130")
                                .skknights("1")
                                .skpaladins("1")
                                .skmages("1")
                                .defknights("1")
                                .defpaladins("1")
                                .defmages("1")
                                .build(),
                        HuntingPlaceSkills.builder()
                                .areaname("Another Area (Past Teleporter)")
                                .lvlknights("230")
                                .lvlpaladins("230")
                                .lvlmages("230")
                                .skknights("2")
                                .skpaladins("2")
                                .skmages("2")
                                .defknights("2")
                                .defpaladins("2")
                                .defmages("2")
                                .build())
                )
                .exp("Good")
                .loot("Good")
                .bestloot("Reins")
                .bestloot2("Foobar")
                .bestloot3("Foobar")
                .bestloot4("Foobar")
                .bestloot5("Foobar")
                .map("Hero Cave 3.png")
                .map2("Hero Cave 6.png")
                .build();
    }

    private Item makeItem() {
        return Item.builder()
                .name("Carlin Sword")
                .marketable(YesNo.YES_LOWERCASE)
                .usable(YesNo.YES_LOWERCASE)
                .sprites("{{Frames|{{Frame Sprite|55266}}}}")
                .article(Article.A)
                .actualname("carlin sword")
                .plural("?")
                .itemid(Collections.singletonList(3283))
                .flavortext("Foobar")
                .itemclass(ItemClass.WEAPONS)
                .primarytype("Sword Weapons")
                .levelrequired(0)
                .hands(Hands.One)
                .type(WeaponType.Sword)
                .attack("15")
                .defense(13)
                .defensemod("+1")
                .enchantable(YesNo.NO_LOWERCASE)
                .weight(BigDecimal.valueOf(40.00).setScale(2, RoundingMode.HALF_UP))
                .droppedby(Arrays.asList("Grorlam", "Stone Golem"))
                .value("118")
                .npcvalue("118")
                .npcprice("473")
                .npcvaluerook("0")
                .npcpricerook("0")
                .buyfrom("Baltim, Brengus, Cedrik,")
                .sellto("Baltim, Brengus, Cedrik, Esrik,")
                .notes("If you have one of these ")
                .build();
    }

    private JSONObject makeQuestJson(Quest quest) {
        return new JSONObject(objectMapper.convertValue(quest, Map.class)).put("templateType", "Quest");
    }

    private static final String INFOBOX_SPELL_TEXT = "{{Infobox Spell|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name          = Light Healing\n" +
            "| type          = Instant\n" +
            "| subclass      = Healing\n" +
            "| words         = exura\n" +
            "| mana          = 20\n" +
            "| cooldown      = 1\n" +
            "| cooldowngroup = 1\n" +
            "| levelrequired = 8\n" +
            "| premium       = no\n" +
            "| voc           = [[Paladin]]s, [[Druid]]s and [[Sorcerer]]s\n" +
            "| d-abd         = [[Maealil]]\n" +
            "| p-abd         = [[Maealil]]\n" +
            "| spellcost     = 0\n" +
            "| effect        = Restores a small amount of [[HP|health]]. (Cures [[paralysis]].)\n" +
            "| notes         = A weak, but popular healing spell.\n" +
            "}}\n";

    private Spell makeSpell() {
        return Spell.builder()
                .name("Light Healing")
                .type(SpellType.Instant)
                .subclass(SpellSubclass.Healing)
                .words("exura")
                .premium(YesNo.NO_LOWERCASE)
                .mana(20)
                .levelrequired(8)
                .cooldown(1)
                .cooldowngroup(1)
                .voc("[[Paladin]]s, [[Druid]]s and [[Sorcerer]]s")
                .druidAbDendriel("[[Maealil]]")
                .paladinAbDendriel("[[Maealil]]")
                .spellcost(0)
                .effect("Restores a small amount of [[HP|health]]. (Cures [[paralysis]].)")
                .notes("A weak, but popular healing spell.")
                .build();
    }

    private JSONObject makeSpellJson(Spell spell) {
        return new JSONObject(objectMapper.convertValue(spell, Map.class)).put("templateType", "Spell");
    }

    private Quest makeQuest() {
        return Quest.builder()
                .name("The Paradox Tower Quest")
                .aka("Riddler Quest, Mathemagics Quest")
                .reward("Up to two of the following:")
                .location("[[Paradox Tower]] near [[Kazordoon]]")
                .lvl(30)
                .lvlrec(50)
                .log(YesNo.YES_LOWERCASE)
                .premium(YesNo.YES_LOWERCASE)
                .transcripts(YesNo.YES_LOWERCASE)
                .dangers("[[Wyvern]]s<br /> ([[Mintwallin]]): [[Minotaur]]s,")
                .legend("Surpass the wrath of a madman and subject yourself to his twisted taunting.")
                .implemented("6.61-6.97")
                .build();
    }

    private Street makeStreet() {
        return Street.builder()
                .name("Sugar Street")
                .implemented("7.8")
                .city(City.LIBERTY_BAY)
                .notes("{{StreetStyles|Sugar Street}} is in west")
                .build();
    }

    private JSONObject makeStreetJson(Street street) {
        return new JSONObject(objectMapper.convertValue(street, Map.class)).put("templateType", "Street");
    }
}
