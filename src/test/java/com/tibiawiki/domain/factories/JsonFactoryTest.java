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
import com.tibiawiki.domain.enums.Hands;
import com.tibiawiki.domain.enums.KeyType;
import com.tibiawiki.domain.enums.ObjectClass;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonFactoryTest {

    private JsonFactory target;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setup() {
        target = new JsonFactory();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testConvertInfoboxPartOfArticleToJson_NullOrEmpty() {
        assertThat(target.convertInfoboxPartOfArticleToJson(null), instanceOf(JSONObject.class));
        assertThat(target.convertInfoboxPartOfArticleToJson(""), instanceOf(JSONObject.class));
    }

    @Test
    void testConvertInfoboxPartOfArticleToJson_InfoboxAchievement() {
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
    void testConvertInfoboxPartOfArticleToJson_InfoboxHunt() {
        JSONObject result = target.convertInfoboxPartOfArticleToJson(INFOBOX_HUNT_TEXT);

        assertThat(result.get("templateType"), is("Hunt"));
        assertThat(result.get("name"), is("Hero Cave"));
        assertThat(result.get("image"), is("Hero"));
        assertThat(result.get("implemented"), is("6.4"));
        assertThat(result.get("city"), is("Edron"));
        assertThat(result.get("location"), is("North of [[Edron]], [https://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here]."));
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

    private static final String LOOT_BEAR_TEXT = """
            {{Loot2
            |version=8.6
            |kills=52807
            |name=Bear
            |Empty, times:24777
            |Meat, times:21065
            |Ham, times:10581
            |Bear Paw, times:1043, amount:1, total:1043
            |Honeycomb, times:250, amount:1, total:249
            }}""";

    @Test
    void testConvertLootPartOfArticleToJson_NullOrEmpty() {
        assertThat(target.convertLootPartOfArticleToJson("", null), instanceOf(JSONObject.class));
        assertThat(target.convertLootPartOfArticleToJson("", ""), instanceOf(JSONObject.class));
    }

    @Test
    void testGetTemplateType_NullOrEmpty() {
        assertThat(target.getTemplateType(null), is("Unknown"));
        assertThat(target.getTemplateType(""), is("Unknown"));
    }

    @Test
    void testGetTemplateType_Succes() {
        assertThat(target.getTemplateType(INFOBOX_TEXT_SPACE), is("Achievement"));
        assertThat(target.getTemplateType(INFOBOX_TEXT_UNDERSCORE), is("Hunt"));
    }

    @Test
    void testGetTemplateType_Failure() {
        assertThat(target.getTemplateType(INFOBOX_TEXT_WRONG), is("Unknown"));
    }

    @Test
    void testEnhanceJsonObject_FailureNoName() {
        final JSONObject someJsonObject = new JSONObject(Collections.emptyMap());
        assertThat(target.enhanceJsonObject(someJsonObject), is(someJsonObject));
    }

    @Test
    void testEnhanceJsonObject_Failure_Sounds() {
        final JSONObject inputJsonObject = new JSONObject(Map.of(
                "name", "Dragon",
                "templateType", "Creature",
                "sounds", "FCHHHHH, GROOAAARRR"
        ));
        JSONObject result = target.enhanceJsonObject(inputJsonObject);
        assertThat(((JSONArray) result.get("sounds")).length(), is(0));
    }

    @Test
    void testEnhanceJsonObject_Failure_Empty_SoundsList() {
        final JSONObject inputJsonObject = new JSONObject(Map.of(
                "name", "Dragon",
                "templateType", "Creature",
                "sounds", "{{Sound List}}"
        ));
        JSONObject result = target.enhanceJsonObject(inputJsonObject);
        assertThat(((JSONArray) result.get("sounds")).length(), is(0));
    }

    @Test
    void testEnhanceJsonObject_Succes_Sounds() {
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
    void testEnhanceJsonObject_Spawntype_Empty() {
        final JSONObject inputJsonObject = new JSONObject(Map.of(
                "name", "Demon",
                "templateType", "Creature",
                "spawntype", " "
        ));
        JSONObject result = target.enhanceJsonObject(inputJsonObject);
        assertThat(((JSONArray) result.get("spawntype")).length(), is(0));
    }

    @Test
    void testEnhanceJsonObject_Spawntype_Succes() {
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
    void testDetermineArticleName_EmptyOrNull() {
        assertThat(target.determineArticleName(null, null), is("Unknown"));
        assertThat(target.determineArticleName(new JSONObject(), ""), is("Unknown"));
    }

    @Test
    void testDetermineArticleName_Book() {
        JSONObject input = new JSONObject(Map.of("pagename", "Foobar"));

        assertThat(target.determineArticleName(input, JsonFactory.TEMPLATE_TYPE_BOOK), is("Foobar"));
    }

    @Test
    void testDetermineArticleName_Location() {
        JSONObject input = new JSONObject();

        assertThat(target.determineArticleName(input, JsonFactory.TEMPLATE_TYPE_LOCATION), is("Unknown"));
    }

    @Test
    void testDetermineArticleName_Key() {
        JSONObject input = new JSONObject(Map.of("number", "1234"));

        assertThat(target.determineArticleName(input, JsonFactory.TEMPLATE_TYPE_KEY), is("Key 1234"));
    }

    @Test
    void testDetermineArticleName_Achievement() {
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

    private static final String INFOBOX_CREATURE_TEXT = """
            {{Infobox Creature|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name           = Dragon
            | article        = a
            | actualname     = dragon
            | plural         = dragons
            | hp             = 1000
            | exp            = 700
            | armor          = 25
            | summon         = --
            | convince       = --
            | illusionable   = yes
            | creatureclass  = Reptiles
            | primarytype    = Dragons
            | bestiaryclass  = Dragon
            | bestiarylevel  = Medium
            | occurrence     = Common
            | spawntype      = Regular, Raid
            | isboss         = no
            | isarenaboss    = no
            | abilities      = [[Melee]] (0-120), [[Fire Wave]] (100-170), [[Great Fireball]] (60-140), [[Self-Healing]] (40-70)
            | maxdmg         = 430
            | pushable       = no
            | pushobjects    = yes
            | walksaround    = None
            | walksthrough   = Fire, Energy, Poison
            | paraimmune     = yes
            | senseinvis     = yes
            | physicalDmgMod = 100%
            | earthDmgMod    = 20%
            | fireDmgMod     = 0%
            | deathDmgMod    = 100%
            | energyDmgMod   = 80%
            | holyDmgMod     = 100%
            | iceDmgMod      = 110%
            | hpDrainDmgMod  = 100%?
            | drownDmgMod    = 100%?
            | bestiaryname   = dragon
            | bestiarytext   = Dragons were
            | sounds         = {{Sound List|FCHHHHH|GROOAAARRR}}
            | implemented    = Pre-6.0
            | notes          = Dragons are
            | behaviour      = Dragons are
            | runsat         = 300
            | speed          = 86
            | strategy       = '''All''' [[player]]s
            | location       = [[Thais]] [[Ancient Temple]], [[Darashia Dragon Lair]], [[Mount Sternum Dragon Cave]], [[Mintwallin]], deep in [[Fibula Dungeon]], [[Kazordoon Dragon Lair]] (near [[Dwarf Bridge]]), [[Plains of Havoc]], [[Elven Bane]] castle, [[Maze of Lost Souls]], southern cave and dragon tower in [[Shadowthorn]], [[Orc Fortress]], [[Venore]] [[Dragon Lair]], [[Pits of Inferno]], [[Behemoth Quest]] room in [[Edron]], [[Hero Cave]], deep [[Cyclopolis]], [[Edron Dragon Lair]], [[Goroma]], [[Ankrahmun Dragon Lair]]s, [[Draconia]], [[Dragonblaze Peaks]], some [[Ankrahmun Tombs]], underground of [[Fenrock]] (on the way to [[Beregar]]), [[Krailos Steppe]] and [[Crystal Lakes]].
            | loot           = {{Loot Table
             |{{Loot Item|0-105|Gold Coin}}
             |{{Loot Item|0-3|Dragon Ham}}
             |{{Loot Item|Steel Shield}}
             |{{Loot Item|Crossbow}}
             |{{Loot Item|Dragon's Tail}}
             |{{Loot Item|0-10|Burst Arrow}}
             |{{Loot Item|Longsword|semi-rare}}
             |{{Loot Item|Steel Helmet|semi-rare}}
             |{{Loot Item|Broadsword|semi-rare}}
             |{{Loot Item|Plate Legs|semi-rare}}
             |{{Loot Item|Green Dragon Leather|rare}}
             |{{Loot Item|Wand of Inferno|rare}}
             |{{Loot Item|Strong Health Potion|rare}}
             |{{Loot Item|Green Dragon Scale|rare}}
             |{{Loot Item|Double Axe|rare}}
             |{{Loot Item|Dragon Hammer|rare}}
             |{{Loot Item|Serpent Sword|rare}}
             |{{Loot Item|Small Diamond|very rare}}
             |{{Loot Item|Dragon Shield|very rare}}
             |{{Loot Item|Life Crystal|very rare}}
             |{{Loot Item|Dragonbone Staff|very rare}}
            }}
            | history        = Dragons are
            }}
            """;

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Effect() {
        final Effect effect = makeEffect();
        String result = target.convertJsonToInfoboxPartOfArticle(makeEffectJson(effect), effect.fieldOrder());
        assertThat(result, is(INFOBOX_EFFECT_TEXT));
    }

    private static final String INFOBOX_CREATURE_EMPTY_LOOT_TEXT = """
            {{Infobox Creature|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name          = Freed Soul
            | article       = a
            | actualname    = Freed Soul
            | plural        = Freed Soul
            | hp            = ?
            | exp           = ?
            | summon        = --
            | convince      = --
            | illusionable  = no
            | creatureclass =\s
            | primarytype   =\s
            | isboss        = no
            | abilities     = [[Melee]] (0-?), [[Drown Damage|Drown Bomb]] on self (4000-8000) (damages boss only)
            | implemented   = 11.40
            | behaviour     = They fight in close combat.
            | strategy      = Do not kill them since you need their help in order to kill the boss.
            | location      = [[The Souldespoiler]]'s room.
            | loot          = {{Loot Table}}
            }}
            """;

    @Test
    void testConvertJsonToInfoboxPartOfArticle_CreatureWithEmptyLootTable() {
        final Creature creature = makeCreatureWithEmptyLootTable();
        String result = target.convertJsonToInfoboxPartOfArticle(makeCreatureJson(creature), creature.fieldOrder());
        assertThat(result, is(INFOBOX_CREATURE_EMPTY_LOOT_TEXT));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Item() {
        var item = makeItem();
        String result = target.convertJsonToInfoboxPartOfArticle(makeItemJson(item), item.fieldOrder());
        assertThat(result, is(INFOBOX_ITEM_TEXT));
    }

    @Test
    void testConvertJsonToInfoboxPartOfArticle_Key() {
        final Key key = makeKey();
        String result = target.convertJsonToInfoboxPartOfArticle(makeKeyJson(key), key.fieldOrder());
        assertThat(result, is(INFOBOX_KEY_TEXT));
    }

    private static final String INFOBOX_HUNT_TEXT = """
            {{Infobox Hunt|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Hero Cave
            | image        = Hero
            | implemented  = 6.4
            | city         = Edron
            | location     = North of [[Edron]], [https://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].
            | vocation     = All vocations.
            | lvlknights   = 70
            | lvlpaladins  = 60
            | lvlmages     = 50
            | skknights    = 75
            | skpaladins   = 80
            | skmages      = 1
            | defknights   = 75
            | defpaladins  = 1
            | defmages     = 1
            | lowerlevels  =\s
                {{Infobox Hunt Skills
                | areaname    = Demons
                | lvlknights  = 130
                | lvlpaladins = 130
                | lvlmages    = 130
                | skknights   = 1
                | skpaladins  = 1
                | skmages     = 1
                | defknights  = 1
                | defpaladins = 1
                | defmages    = 1
                }}
                {{Infobox Hunt Skills
                | areaname    = Another Area (Past Teleporter)
                | lvlknights  = 230
                | lvlpaladins = 230
                | lvlmages    = 230
                | skknights   = 2
                | skpaladins  = 2
                | skmages     = 2
                | defknights  = 2
                | defpaladins = 2
                | defmages    = 2
                }}
            | loot         = Good
            | exp          = Good
            | bestloot     = Reins
            | bestloot2    = Foobar
            | bestloot3    = Foobar
            | bestloot4    = Foobar
            | bestloot5    = Foobar
            | map          = Hero Cave 3.png
            | map2         = Hero Cave 6.png
            }}
            """;

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

    private static final String INFOBOX_ITEM_TEXT = """
            {{Infobox Object|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name          = Carlin Sword
            | article       = a
            | actualname    = carlin sword
            | plural        = ?
            | itemid        = 3283
            | objectclass   = Weapons
            | flavortext    = Foobar
            | pickupable    = yes
            | usable        = yes
            | levelrequired = 0
            | hands         = One
            | weapontype    = Sword
            | attack        = 15
            | defense       = 13
            | defensemod    = +1
            | enchantable   = no
            | weight        = 40.00
            | marketable    = yes
            | droppedby     = {{Dropped By|Grorlam|Stone Golem}}
            | value         = 118
            | npcvalue      = 118
            | npcprice      = 473
            | npcvaluerook  = 0
            | npcpricerook  = 0
            | buyfrom       = Baltim, Brengus, Cedrik,
            | sellto        = Baltim, Brengus, Cedrik, Esrik,
            | notes         = If you have one of these\s
            }}
            """;

    @Disabled // TODO: fix test
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

    private static final String INFOBOX_KEY_TEXT = """
            {{Infobox Key|List={{{1|}}}|GetValue={{{GetValue|}}}
            | number       = 4055
            | aka          = Panpipe Quest Key
            | primarytype  = Silver
            | location     = [[Jakundaf Desert]]
            | value        = Negotiable
            | npcvalue     = 0
            | npcprice     = 0
            | buyfrom      = --
            | sellto       = --
            | origin       = Hidden in a rock south of the Desert Dungeon entrance.
            | shortnotes   = Access to the [[Panpipe Quest]].
            | longnotes    = Allows you to open the door ([https://tibia.wikia.com/wiki/Mapper?coords=127.131,125.129,8,3,1,1 here]) to the [[Panpipe Quest]].
            }}
            """;

    private static final String INFOBOX_TEXT_SPACE = """
            {{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Goo Goo Dancer
            }}""";

    private static final String INFOBOX_TEXT_UNDERSCORE = """
            {{Infobox_Hunt|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Goo Goo Dancer
            }}""";

    private static final String INFOBOX_TEXT_WRONG = "{{Infobax_Hunt  \n|List={{{1|}}}|GetValue={{{GetValue|}}}";

    private static final String INFOBOX_ACHIEVEMENT_TEXT = """
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
            """;

    @Test
    void testConvertLootPartOfArticleToJson_Loot2Bear() {
        JSONObject result = target.convertLootPartOfArticleToJson("Loot Statistics:Bear", LOOT_BEAR_TEXT);

        assertThat(result.get("version"), is("8.6"));
        assertThat(result.get("kills"), is("52807"));
        assertThat(result.get("name"), is("Bear"));

        Object loot = result.get("loot");
        assertThat(loot, instanceOf(JSONArray.class));

        var lootArray = (JSONArray) loot;
        assertThat(((JSONObject) lootArray.get(0)).get("itemName"), is("Empty"));
        assertThat(((JSONObject) lootArray.get(0)).get("times"), is("24777"));

        assertThat(((JSONObject) lootArray.get(1)).get("itemName"), is("Ham"));
        assertThat(((JSONObject) lootArray.get(1)).get("times"), is("10581"));

        assertThat(((JSONObject) lootArray.get(2)).get("itemName"), is("Bear Paw"));
        assertThat(((JSONObject) lootArray.get(2)).get("times"), is("1043"));
        assertThat(((JSONObject) lootArray.get(2)).get("amount"), is("1"));
        assertThat(((JSONObject) lootArray.get(2)).get("total"), is("1043"));

        assertThat(((JSONObject) lootArray.get(3)).get("itemName"), is("Honeycomb"));
        assertThat(((JSONObject) lootArray.get(3)).get("times"), is("250"));
        assertThat(((JSONObject) lootArray.get(3)).get("amount"), is("1"));
        assertThat(((JSONObject) lootArray.get(3)).get("total"), is("249"));

        assertThat(((JSONObject) lootArray.get(4)).get("itemName"), is("Meat"));
        assertThat(((JSONObject) lootArray.get(4)).get("times"), is("21065"));
    }

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

    private JSONObject makeAchievementJson(Achievement achievement) {
        return new JSONObject(objectMapper.convertValue(achievement, Map.class)).put("templateType", "Achievement");
    }

    private static final String INFOBOX_BOOK_TEXT = """
            {{Infobox Book|List={{{1|}}}|GetValue={{{GetValue|}}}
            | booktype     = Book (Brown)
            | title        = Dungeon Survival Guide
            | pagename     = Dungeon Survival Guide (Book)
            | location     = [[Rookgaard Academy]]
            | blurb        = Tips for exploring dungeons, and warning against being reckless.
            | returnpage   = Rookgaard Libraries
            | relatedpages = [[Rope]], [[Shovel]]
            | text         = Dungeon Survival Guide<br><br>Don't explore the dungeons before you tested your skills in the training cellars of our academy. You will find dungeons somewhere in the wilderness. Don't enter dungeons without equipment. Especially a rope and a shovel will prove valuable. Make sure you have a supply of torches with you, while wandering into the unknown. It's wise to travel the dungeons in groups and not alone. For more help read all the books of the academy before you begin exploring. Traveling in the dungeons will reward the cautious and brave, but punish the reckless.
            }}
            """;

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

    private static final String INFOBOX_BUILDING_TEXT = """
            {{Infobox Building|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Theater Avenue 8b
            | implemented  = Pre-6.0
            | type         = House
            | location     = South-east of depot, two floors up.
            | posx         = 126.101
            | posy         = 124.48
            | posz         = 5
            | street       = Theater Avenue
            | houseid      = 20315
            | size         = 26
            | beds         = 3
            | rent         = 1370
            | city         = Carlin
            | openwindows  = 3
            | floors       = 1
            | rooms        = 1
            | furnishings  = 1 [[Wall Lamp]].
            | notes        =\s
            | image        = [[File:Theater Avenue 8b.png]]
            }}
            """;

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

    private static final String INFOBOX_CORPSE_TEXT = """
            {{Infobox Corpse|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Dead Rat
            | article      = a
            | liquid       = [[Blood]]
            | 1decaytime   = 5 minutes.
            | 2decaytime   = 5 minutes.
            | 3decaytime   = 60 seconds.
            | 1volume      = 5
            | 1weight      = 63.00
            | 2weight      = 44.00
            | 3weight      = 30.00
            | corpseof     = [[Rat]], [[Cave Rat]], [[Munster]]
            | sellto       = [[Tom]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Seymour]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Billy]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Humgolf]] ([[Kazordoon]]) '''2''' [[gp]]<br>
            [[Baxter]] ([[Thais]]) '''1''' [[gp]]<br>
            | notes        = These corpses are commonly used by low level players on [[Rookgaard]] to earn some gold for better [[equipment]]. Only fresh corpses are accepted, rotted corpses are ignored.
            | implemented  = Pre-6.0
            }}
            """;

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

    private static final String INFOBOX_LOCATION_TEXT = """
            {{Infobox Geography
            | implemented  = Pre-6.0
            | ruler        = [[King Tibianus]]
            | population   = {{PAGESINCATEGORY:Thais NPCs|pages}}
            | near         = [[Fibula]], [[Mintwallin]], [[Greenshore]], [[Mount Sternum]]
            | organization = [[Thieves Guild]], [[Tibian Bureau of Investigation]], [[Inquisition]]
            | map          = [[File:Map_thais.jpg]]
            | map2         = [[File:Thais.PNG]]
            }}
            """;
    private static final String INFOBOX_MISSILE_TEXT = """
            {{Infobox Missile|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Throwing Cake Missile
            | implemented  = 7.9
            | missileid    = 42
            | primarytype  = Throwing Weapon
            | shotby       = [[Undead Jester]]'s attack and probably by throwing a [[Throwing Cake]].
            | notes        = This missile is followed by the [[Cream Cake Effect]]: [[File:Cream Cake Effect.gif]]
            }}
            """;

    private JSONObject makeCreatureJson(Creature creature) {
        return new JSONObject(objectMapper.convertValue(creature, Map.class)).put("templateType", "Creature");
    }

    private static final String INFOBOX_EFFECT_TEXT = """
            {{Infobox Effect|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Fireball Effect
            | effectid     = 7, 82
            | primarytype  = Attack
            | lightradius  = 6
            | lightcolor   = 208
            | causes       = *[[Fireball]] and [[Great Fireball]];
            | effect       = [[Fire Damage]] on target or nothing.
            }}
            """;


    private Effect makeEffect() {
        return Effect.builder()
                .name("Fireball Effect")
                .effectid(Arrays.asList(7, 82))
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

    private static final String INFOBOX_MOUNT_TEXT = """
            {{Infobox Mount|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name          = Donkey
            | speed         = 10
            | taming_method = Use a [[Bag of Apple Slices]] on a creature transformed into Donkey.
            | achievement   = Loyal Lad
            | implemented   = 9.1
            | notes         = Go to [[Incredibly Old Witch]]'s house,
            }}
            """;
    private static final String INFOBOX_NPC_TEXT = """
            {{Infobox NPC|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Sam
            | job          = Artisan
            | job2         = Weapon Shopkeeper
            | job3         = Armor Shopkeeper
            | location     = [[Temple Street]] in [[Thais]].
            | city         = Thais
            | posx         = 126.104
            | posy         = 125.200
            | posz         = 7
            | gender       = Male
            | race         = Human
            | buysell      = yes
            | buys         = {{Price to Sell |Axe
            | sells        = {{Price to Buy |Axe
            | sounds       = {{Sound List|Hello there, adventurer! Need a deal in weapons or armor? I'm your man!}}
            | implemented  = Pre-6.0
            | notes        = Sam is the Blacksmith of [[Thais]].
            }}
            """;

    private JSONObject makeHuntingPlaceJson(HuntingPlace huntingPlace) {
        return new JSONObject(objectMapper.convertValue(huntingPlace, Map.class)).put("templateType", "Hunt");
    }

    private static final String INFOBOX_OBJECT_TEXT = """
            {{Infobox Object|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Blueberry Bush
            | article      = a
            | objectclass  = Bushes
            | implemented  = 7.1
            | walkable     = no
            | location     = Can be found all around [[Tibia]].
            | notes        = They are the source of the [[blueberry|blueberries]].
            | notes2       = <br />{{JSpoiler|After using [[Blueberry]] Bushes 500 times,
            }}
            """;
    private static final String INFOBOX_OUTFIT_TEXT = """
            {{Infobox Outfit|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Pirate
            | primarytype  = Quest
            | premium      = yes
            | outfit       = premium, see [[Pirate Outfits Quest]].
            | addons       = premium, see [[Pirate Outfits Quest]].
            | achievement  = Swashbuckler
            | implemented  = 7.8
            | artwork      = Pirate Outfits Artwork.jpg
            | notes        = Pirate outfits are perfect for swabbing the deck or walking the plank. Quite dashing and great for sailing.
            }}
            """;

    private JSONObject makeItemJson(TibiaObject item) {
        return new JSONObject(objectMapper.convertValue(item, Map.class)).put("templateType", "Object");
    }

    private static final String INFOBOX_QUEST_TEXT = """
            {{Infobox Quest|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = The Paradox Tower Quest
            | aka          = Riddler Quest, Mathemagics Quest
            | reward       = Up to two of the following:
            | location     = [[Paradox Tower]] near [[Kazordoon]]
            | lvl          = 30
            | lvlrec       = 50
            | log          = yes
            | premium      = yes
            | transcripts  = yes
            | dangers      = [[Wyvern]]s<br /> ([[Mintwallin]]): [[Minotaur]]s,
            | legend       = Surpass the wrath of a madman and subject yourself to his twisted taunting.
            | implemented  = 6.61-6.97
            }}
            """;

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
                .longnotes("Allows you to open the door ([https://tibia.wikia.com/wiki/Mapper?coords=127.131,125.129,8,3,1,1 here]) to the [[Panpipe Quest]].")
                .build();
    }

    private JSONObject makeKeyJson(Key key) {
        return new JSONObject(objectMapper.convertValue(key, Map.class)).put("templateType", "Key");
    }

    private static final String INFOBOX_STREET_TEXT = """
            {{Infobox Street
            | name         = Sugar Street
            | implemented  = 7.8
            | city         = Liberty Bay
            | notes        = {{StreetStyles|Sugar Street}} is in west
            }}
            """;

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
        var mock = mock(TibiaObject.class);
        when(mock.getName()).thenReturn("Blueberry Bush");
        when(mock.getArticle()).thenReturn(Article.A);
        when(mock.getObjectclass()).thenReturn("Bushes");
        when(mock.getWalkable()).thenReturn(YesNo.NO_LOWERCASE);
        when(mock.getLocation()).thenReturn("Can be found all around [[Tibia]].");
        when(mock.getNotes()).thenReturn("They are the source of the [[blueberry|blueberries]].");
        when(mock.getNotes2()).thenReturn("<br />{{JSpoiler|After using [[Blueberry]] Bushes 500 times,");
        when(mock.getImplemented()).thenReturn("7.1");
        return mock;
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

    private Creature makeCreatureWithEmptyLootTable() {
        return Creature.builder()
                .name("Freed Soul")
                .article(Article.A)
                .actualname("Freed Soul")
                .plural("Freed Soul")
                .implemented("11.40")
                .hitPoints("?")
                .experiencePoints("?")
                .summon("--")
                .convince("--")
                .illusionable(YesNo.NO_LOWERCASE)
                .creatureclass("")
                .primarytype("")
                .isboss(YesNo.NO_LOWERCASE)
                .abilities("[[Melee]] (0-?), [[Drown Damage|Drown Bomb]] on self (4000-8000) (damages boss only)")
                .behaviour("They fight in close combat.")
                .location("[[The Souldespoiler]]'s room.")
                .strategy("Do not kill them since you need their help in order to kill the boss.")
                .loot(Collections.emptyList())
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
                .location("North of [[Edron]], [https://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].")
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

    private TibiaObject makeItem() {
        var result = new TibiaObject(
                Collections.singletonList(3283),
                ObjectClass.WEAPONS.getDescription(),
                null,
                null,
                "Foobar",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                YesNo.YES_LOWERCASE,
                null,
                YesNo.YES_LOWERCASE,
                null,
                null,
                null,
                0,
                null,
                null,
                Hands.One,
                WeaponType.Sword,
                "15",
                null,
                null,
                null,
                null,
                null,
                13,
                "+1",
                null,
                null,
                YesNo.NO_LOWERCASE,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                BigDecimal.valueOf(40.00).setScale(2, RoundingMode.HALF_UP),
                null,
                YesNo.YES_LOWERCASE,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of("Grorlam", "Stone Golem"),
                "118",
                null,
                "118",
                "473",
                "0",
                "0",
                "Baltim, Brengus, Cedrik,",
                "Baltim, Brengus, Cedrik, Esrik,",
                null,
                null,
                null);
        ReflectionTestUtils.setField(result, "name", "Carlin Sword");
        ReflectionTestUtils.setField(result, "article", Article.A);
        ReflectionTestUtils.setField(result, "actualname", "carlin sword");
        ReflectionTestUtils.setField(result, "plural", "?");
        ReflectionTestUtils.setField(result, "notes", "If you have one of these ");
        return result;
    }

    private JSONObject makeQuestJson(Quest quest) {
        return new JSONObject(objectMapper.convertValue(quest, Map.class)).put("templateType", "Quest");
    }

    private static final String INFOBOX_SPELL_TEXT = """
            {{Infobox Spell|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name          = Light Healing
            | type          = Instant
            | subclass      = Healing
            | words         = exura
            | mana          = 20
            | cooldown      = 1
            | cooldowngroup = 1
            | levelrequired = 8
            | premium       = no
            | voc           = [[Paladin]]s, [[Druid]]s and [[Sorcerer]]s
            | d-abd         = [[Maealil]]
            | p-abd         = [[Maealil]]
            | spellcost     = 0
            | effect        = Restores a small amount of [[HP|health]]. (Cures [[paralysis]].)
            | notes         = A weak, but popular healing spell.
            }}
            """;

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
