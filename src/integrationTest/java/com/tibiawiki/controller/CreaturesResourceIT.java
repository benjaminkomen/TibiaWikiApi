package com.tibiawiki.controller;

import com.tibiawiki.domain.enums.Article;
import com.tibiawiki.domain.enums.BestiaryClass;
import com.tibiawiki.domain.enums.BestiaryLevel;
import com.tibiawiki.domain.enums.BestiaryOccurrence;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.enums.Rarity;
import com.tibiawiki.domain.enums.Spawntype;
import com.tibiawiki.domain.enums.YesNo;
import com.tibiawiki.domain.objects.Creature;
import com.tibiawiki.domain.objects.LootItem;
import com.tibiawiki.domain.objects.Percentage;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.tibiawiki.TestUtils.makeHttpHeaders;
import static com.tibiawiki.process.RetrieveAny.CATEGORY_LISTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreaturesResourceIT {

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
    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private ArticleRepository articleRepository;

    @Test
    void givenGetCreaturesNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoCreatureNames() {
        doReturn(Collections.singletonList("baz")).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(Arrays.asList("foo", "bar", "baz")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.CREATURE.getCategoryName());

        final ResponseEntity<List> result = restTemplate.getForEntity("/api/creatures?expand=false", List.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().size(), is(2));
        assertThat(result.getBody().get(0), is("foo"));
        assertThat(result.getBody().get(1), is("bar"));
    }

    @Test
    void givenGetCreaturesExpanded_whenCorrectRequest_thenResponseIsOkAndContainsOneCreature() {
        doReturn(Collections.emptyList()).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(Collections.singletonList("Dragon")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.CREATURE.getCategoryName());
        doReturn(Map.of("Dragon", INFOBOX_CREATURE_TEXT)).when(articleRepository).getArticlesFromCategory(Collections.singletonList("Dragon"));

        final ResponseEntity<List> result = restTemplate.getForEntity("/api/creatures?expand=true", List.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().size(), is(1));
        assertThat(((Map) result.getBody().get(0)).get("templateType"), is("Creature"));
        assertThat(((Map) result.getBody().get(0)).get("name"), is("Dragon"));
        assertThat(((Map) result.getBody().get(0)).get("article"), is("a"));
        assertThat(((Map) result.getBody().get(0)).get("actualname"), is("dragon"));
        assertThat(((Map) result.getBody().get(0)).get("plural"), is("dragons"));
        assertThat(((Map) result.getBody().get(0)).get("hp"), is("1000"));
        assertThat(((Map) result.getBody().get(0)).get("exp"), is("700"));
        assertThat(((Map) result.getBody().get(0)).get("armor"), is("25"));
        assertThat(((Map) result.getBody().get(0)).get("summon"), is("--"));
        assertThat(((Map) result.getBody().get(0)).get("convince"), is("--"));
        assertThat(((Map) result.getBody().get(0)).get("illusionable"), is("yes"));
        assertThat(((Map) result.getBody().get(0)).get("creatureclass"), is("Reptiles"));
        assertThat(((Map) result.getBody().get(0)).get("primarytype"), is("Dragons"));
        assertThat(((Map) result.getBody().get(0)).get("bestiaryclass"), is("Dragon"));
        assertThat(((Map) result.getBody().get(0)).get("bestiarylevel"), is("Medium"));
    }

    @Test
    void givenGetCreaturesByName_whenCorrectRequest_thenResponseIsOkAndContainsTheCreature() {
        doReturn(INFOBOX_CREATURE_TEXT).when(articleRepository).getArticle("Dragon");

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/creatures/Dragon", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        final JSONObject resultAsJSON = new JSONObject(result.getBody());
        assertThat(resultAsJSON.get("templateType"), is("Creature"));
        assertThat(resultAsJSON.get("name"), is("Dragon"));
        assertThat(resultAsJSON.get("article"), is("a"));
        assertThat(resultAsJSON.get("actualname"), is("dragon"));
        assertThat(resultAsJSON.get("plural"), is("dragons"));
        assertThat(resultAsJSON.get("hp"), is("1000"));
        assertThat(resultAsJSON.get("exp"), is("700"));
        assertThat(resultAsJSON.get("armor"), is("25"));
        assertThat(resultAsJSON.get("summon"), is("--"));
        assertThat(resultAsJSON.get("convince"), is("--"));
        assertThat(resultAsJSON.get("illusionable"), is("yes"));
        assertThat(resultAsJSON.get("creatureclass"), is("Reptiles"));
        assertThat(resultAsJSON.get("primarytype"), is("Dragons"));
        assertThat(resultAsJSON.get("bestiaryclass"), is("Dragon"));
        assertThat(resultAsJSON.get("bestiarylevel"), is("Medium"));
    }

    @Test
    void givenGetCreaturesByName_whenWrongRequest_thenResponseIsNotFound() {
        doReturn(null).when(articleRepository).getArticle("Foobar");

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/creatures/Foobar", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void givenPutCreature_whenCorrectRequest_thenResponseIsOkAndContainsTheModifiedCreature() {
        final String editSummary = "[bot] editing during integration test";

        doReturn(INFOBOX_CREATURE_TEXT).when(articleRepository).getArticle("Dragon");
        doReturn(true).when(articleRepository).modifyArticle(anyString(), anyString(), anyString());

        final HttpHeaders httpHeaders = makeHttpHeaders(editSummary);

        final ResponseEntity<Void> result = restTemplate.exchange("/api/creatures", HttpMethod.PUT, new HttpEntity<>(makeCreature(), httpHeaders), Void.class);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void givenPutCreature_whenCorrectRequestButUnableToEditWiki_thenResponseIsBadRequest() {
        final String editSummary = "[bot] editing during integration test";

        doReturn(INFOBOX_CREATURE_TEXT).when(articleRepository).getArticle("Dragon");
        doReturn(false).when(articleRepository).modifyArticle(anyString(), anyString(), anyString());

        final HttpHeaders httpHeaders = makeHttpHeaders(editSummary);

        final ResponseEntity<Void> result = restTemplate.exchange("/api/creatures", HttpMethod.PUT, new HttpEntity<>(makeCreature(), httpHeaders), Void.class);
        assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST));
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
}
