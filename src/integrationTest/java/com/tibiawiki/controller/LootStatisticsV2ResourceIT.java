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
    private ArticleRepository articleRepository;

    private static final String LOOT_AMAZON_TEXT = """
            {{Loot2
            |version=8.6
            |kills=22009
            |name=Amazon
            |Empty, times:253
            |Dagger, times:17626, amount:1, total:17626
            |Skull, times:17604, amount:1-2, total:26348
            |Gold Coin, times:8829, amount:1-20, total:93176
            |Brown Bread, times:6496, amount:1, total:6496
            |Sabre, times:5098, amount:1, total:5098
            |Girlish Hair Decoration, times:2179, amount:1, total:2179
            |Protective Charm, times:1154, amount:1, total:1154
            |Torch, times:223, amount:1, total:223
            |Crystal Necklace, times:56, amount:1, total:56
            |Small Ruby, times:27, amount:1, total:27
            }}
            """;

    private static final String LOOT_FERUMBRAS_TEXT = """
            __NOWYSIWYG__

            {{Loot2
            |version=8.6
            |kills=49
            |name=Ferumbras
            |Ferumbras' Hat, times:49, total:3
            |Gold Coin, times:48, amount:18-184, total:4751
            |Gold Ingot, times:37, amount:1-2, total:52
            |Great Shield, times:13, amount:1, total:13
            |Spellbook of Lost Souls, times:13, amount:1, total:13
            |Golden Armor, times:12, amount:1, total:12
            }}

            {{Loot2_RC
            |version=8.6
            |kills=1
            |name=Ferumbras
            |Blue Gem, times:1, amount:1, total:1
            |Giant Shimmering Pearl, times:1, amount:1, total:1
            |Gold Coin, times:1, amount:100, total:100
            |Golden Armor, times:1, amount:1, total:1
            |Lightning Legs, times:1
            |Runed Sword, times:1, amount:1, total:1
            |Small Emerald, times:1, amount:10, total:10
            }}

            {{Loot
            |version=8.54
            |kills=4
            |name=Ferumbras
            |[[Gold Coin]], 399
            |[[Small Ruby]], 126
            |[[Small Diamond]], 45
            |[[Gold Ingot]], 6
            |[[Ferumbras' Hat]], 4
            |[[Small Topaz]], 3
            |[[Spellbook of Lost Souls]], 3
            }}
            <br/>Average gold: 99.75""";

    @Test
    void givenGetLootsNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoLootNames() {
        doReturn(Arrays.asList("foo", "bar")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.LOOT.getCategoryName(), LOOT_NAMESPACE);

        final ResponseEntity<List> result = restTemplate.getForEntity("/api/v2/loot?expand=false", List.class);

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

        final ResponseEntity<List> result = restTemplate.getForEntity("/api/v2/loot?expand=true", List.class);

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

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/v2/loot/Amazon", String.class);
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

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/v2/loot/Ferumbras", String.class);
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

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/v2/loot/Foobar", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }
}
