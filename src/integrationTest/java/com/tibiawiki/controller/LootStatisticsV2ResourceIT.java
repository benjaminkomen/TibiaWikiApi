package com.tibiawiki.controller;

import benjaminkomen.jwiki.core.NS;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.tibiawiki.process.RetrieveAny.CATEGORY_LISTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LootStatisticsV2ResourceIT {

    private static final NS LOOT_NAMESPACE = new NS(112);
    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ArticleRepository articleRepository; // don't instantiate this real class, but use a mock implementation

    private static final String LOOT_AMAZON_TEXT = "{{Loot2\n" +
            "|version=8.6\n" +
            "|kills=22009\n" +
            "|name=Amazon\n" +
            "|Empty, times:253\n" +
            "|Dagger, times:17626, amount:1, total:17626\n" +
            "|Skull, times:17604, amount:1-2, total:26348\n" +
            "|Gold Coin, times:8829, amount:1-20, total:93176\n" +
            "|Brown Bread, times:6496, amount:1, total:6496\n" +
            "|Sabre, times:5098, amount:1, total:5098\n" +
            "|Girlish Hair Decoration, times:2179, amount:1, total:2179\n" +
            "|Protective Charm, times:1154, amount:1, total:1154\n" +
            "|Torch, times:223, amount:1, total:223\n" +
            "|Crystal Necklace, times:56, amount:1, total:56\n" +
            "|Small Ruby, times:27, amount:1, total:27\n" +
            "}}\n";

    private static final String LOOT_FERUMBRAS_TEXT = "__NOWYSIWYG__\n\n" +
            "{{Loot2\n" +
            "|version=8.6\n" +
            "|kills=49\n" +
            "|name=Ferumbras\n" +
            "|Ferumbras' Hat, times:49, total:3\n" +
            "|Gold Coin, times:48, amount:18-184, total:4751\n" +
            "|Gold Ingot, times:37, amount:1-2, total:52\n" +
            "|Great Shield, times:13, amount:1, total:13\n" +
            "|Spellbook of Lost Souls, times:13, amount:1, total:13\n" +
            "|Golden Armor, times:12, amount:1, total:12\n" +
            "}}\n" +
            "\n" +
            "{{Loot2_RC\n" +
            "|version=8.6\n" +
            "|kills=1\n" +
            "|name=Ferumbras\n" +
            "|Blue Gem, times:1, amount:1, total:1\n" +
            "|Giant Shimmering Pearl, times:1, amount:1, total:1\n" +
            "|Gold Coin, times:1, amount:100, total:100\n" +
            "|Golden Armor, times:1, amount:1, total:1\n" +
            "|Lightning Legs, times:1\n" +
            "|Runed Sword, times:1, amount:1, total:1\n" +
            "|Small Emerald, times:1, amount:10, total:10\n" +
            "}}\n" +
            "\n" +
            "{{Loot\n" +
            "|version=8.54\n" +
            "|kills=4\n" +
            "|name=Ferumbras\n" +
            "|[[Gold Coin]], 399\n" +
            "|[[Small Ruby]], 126\n" +
            "|[[Small Diamond]], 45\n" +
            "|[[Gold Ingot]], 6\n" +
            "|[[Ferumbras' Hat]], 4\n" +
            "|[[Small Topaz]], 3\n" +
            "|[[Spellbook of Lost Souls]], 3\n" +
            "}}\n" +
            "<br/>Average gold: 99.75";

    @Test
    void givenGetLootsNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoLootNames() {
        doReturn(Arrays.asList("foo", "bar")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.LOOT.getCategoryName(), LOOT_NAMESPACE);

        final ResponseEntity<List> result = restTemplate.getForEntity("/v2/loot?expand=false", List.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().size(), is(2));
        assertThat(result.getBody().get(0), is("foo"));
        assertThat(result.getBody().get(1), is("bar"));
    }

    @Test
    void givenGetLootsExpanded_whenCorrectRequest_thenResponseIsOkAndContainsOneLoot() {
        doReturn(Collections.emptyList()).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(Collections.singletonList("Loot:Amazon")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.LOOT.getCategoryName(), LOOT_NAMESPACE);
        doReturn(Map.of("Loot:Amazon", LOOT_AMAZON_TEXT)).when(articleRepository).getArticlesFromCategory(Collections.singletonList("Loot:Amazon"));

        final ResponseEntity<List> result = restTemplate.getForEntity("/v2/loot?expand=true", List.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().size(), is(1));

        var loot2 = ((Map) ((Map) result.getBody().get(0)).get("loot2"));

        assertThat(loot2.get("kills"), is("22009"));
        assertThat(loot2.get("name"), is("Amazon"));
        assertThat(loot2.get("version"), is("8.6"));
        assertThat(loot2.get("pageName"), is("Loot:Amazon"));
    }

    @Test
    void givenGetLootsByName_whenCorrectRequest_thenResponseIsOkAndContainsTheLoot() {
        doReturn(LOOT_AMAZON_TEXT).when(articleRepository).getArticle("Loot_Statistics:Amazon");

        final ResponseEntity<String> result = restTemplate.getForEntity("/v2/loot/Amazon", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        final JSONObject resultAsJSON = new JSONObject(result.getBody()).getJSONObject("loot2");
        assertThat(resultAsJSON.get("kills"), is("22009"));
        assertThat(resultAsJSON.get("name"), is("Amazon"));
        assertThat(resultAsJSON.get("version"), is("8.6"));
        assertThat(resultAsJSON.get("pageName"), is("Loot_Statistics:Amazon"));
    }

    @Test
    void givenGetLootsByName_whenCorrectRequest_thenResponseIsOkAndContainsTwoLootEntities() {
        doReturn(LOOT_FERUMBRAS_TEXT).when(articleRepository).getArticle("Loot_Statistics:Ferumbras");

        final ResponseEntity<String> result = restTemplate.getForEntity("/v2/loot/Ferumbras", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        final JSONObject loot2Result = new JSONObject(result.getBody()).getJSONObject("loot2");
        assertThat(loot2Result.get("kills"), is("49"));
        assertThat(loot2Result.get("name"), is("Ferumbras"));
        assertThat(loot2Result.get("version"), is("8.6"));
        assertThat(loot2Result.get("pageName"), is("Loot_Statistics:Ferumbras"));

        final JSONObject loot2RewardChestResult = new JSONObject(result.getBody()).getJSONObject("loot2_rc");
        assertThat(loot2RewardChestResult.get("kills"), is("1"));
        assertThat(loot2RewardChestResult.get("name"), is("Ferumbras"));
        assertThat(loot2RewardChestResult.get("version"), is("8.6"));
        assertThat(loot2RewardChestResult.get("pageName"), is("Loot_Statistics:Ferumbras"));
    }

    @Test
    void givenGetLootsByName_whenWrongRequest_thenResponseIsNotFound() {
        doReturn(null).when(articleRepository).getArticle("Loot:Foobar");

        final ResponseEntity<String> result = restTemplate.getForEntity("/v2/loot/Foobar", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }
}
