package com.tibiawiki.controller;

import com.tibiawiki.domain.enums.Article;
import com.tibiawiki.domain.enums.Hands;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.enums.ObjectClass;
import com.tibiawiki.domain.enums.WeaponType;
import com.tibiawiki.domain.enums.YesNo;
import com.tibiawiki.domain.objects.Item;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class ItemsResourceIT {

    private static final String INFOBOX_ITEM_TEXT = """
            {{Infobox Item|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name          = Carlin Sword
            | article       = a
            | actualname    = carlin sword
            | plural        = ?
            | itemid        = 3283
            | marketable    = yes
            | usable        = yes
            | sprites       = {{Frames|{{Frame Sprite|55266}}}}
            | flavortext    = Foobar
            | itemclass     = Weapons
            | primarytype   = Sword Weapons
            | levelrequired = 0
            | hands         = One
            | type          = Sword
            | attack        = 15
            | defense       = 13
            | defensemod    = +1
            | enchantable   = no
            | weight        = 40.00
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
    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private ArticleRepository articleRepository;

    @Test
    void givenGetItemsNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoItemNames() {
        doReturn(Collections.singletonList("baz")).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(Arrays.asList("foo", "bar", "baz")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ITEM.getCategoryName());

        final ResponseEntity<List> result = restTemplate.getForEntity("/api/items?expand=false", List.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().size(), is(2));
        assertThat(result.getBody().get(0), is("foo"));
        assertThat(result.getBody().get(1), is("bar"));
    }

    @Test
    void givenGetItemsExpanded_whenCorrectRequest_thenResponseIsOkAndContainsOneItem() {
        doReturn(Collections.emptyList()).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(Collections.singletonList("Carlin Sword")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ITEM.getCategoryName());
        doReturn(Map.of("Carlin Sword", INFOBOX_ITEM_TEXT)).when(articleRepository).getArticlesFromCategory(Collections.singletonList("Carlin Sword"));

        final ResponseEntity<List> result = restTemplate.getForEntity("/api/items?expand=true", List.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().size(), is(1));
        assertThat(((Map) result.getBody().get(0)).get("templateType"), is("Item"));
        assertThat(((Map) result.getBody().get(0)).get("name"), is("Carlin Sword"));
        assertThat(((Map) result.getBody().get(0)).get("article"), is("a"));
        assertThat(((Map) result.getBody().get(0)).get("actualname"), is("carlin sword"));
        assertThat(((Map) result.getBody().get(0)).get("plural"), is("?"));
        assertThat(((Map) result.getBody().get(0)).get("itemid"), is(Collections.singletonList("3283")));
        assertThat(((Map) result.getBody().get(0)).get("marketable"), is("yes"));
        assertThat(((Map) result.getBody().get(0)).get("usable"), is("yes"));
        assertThat(((Map) result.getBody().get(0)).get("sprites"), is("{{Frames|{{Frame Sprite|55266}}}}"));
        assertThat(((Map) result.getBody().get(0)).get("flavortext"), is("Foobar"));
        assertThat(((Map) result.getBody().get(0)).get("itemclass"), is("Weapons"));
        assertThat(((Map) result.getBody().get(0)).get("primarytype"), is("Sword Weapons"));
        assertThat(((Map) result.getBody().get(0)).get("levelrequired"), is("0"));
        assertThat(((Map) result.getBody().get(0)).get("hands"), is("One"));
        assertThat(((Map) result.getBody().get(0)).get("type"), is("Sword"));
        assertThat(((Map) result.getBody().get(0)).get("attack"), is("15"));
        assertThat(((Map) result.getBody().get(0)).get("defense"), is("13"));
        assertThat(((Map) result.getBody().get(0)).get("defensemod"), is("+1"));
        assertThat(((Map) result.getBody().get(0)).get("enchantable"), is("no"));
        assertThat(((Map) result.getBody().get(0)).get("weight"), is("40.00"));
        assertThat(((Map) result.getBody().get(0)).get("droppedby"), is(Arrays.asList("Grorlam", "Stone Golem")));
        assertThat(((Map) result.getBody().get(0)).get("value"), is("118"));
        assertThat(((Map) result.getBody().get(0)).get("npcvalue"), is("118"));
        assertThat(((Map) result.getBody().get(0)).get("npcprice"), is("473"));
        assertThat(((Map) result.getBody().get(0)).get("npcvaluerook"), is("0"));
        assertThat(((Map) result.getBody().get(0)).get("npcpricerook"), is("0"));
        assertThat(((Map) result.getBody().get(0)).get("buyfrom"), is("Baltim, Brengus, Cedrik,"));
        assertThat(((Map) result.getBody().get(0)).get("sellto"), is("Baltim, Brengus, Cedrik, Esrik,"));
        assertThat(((Map) result.getBody().get(0)).get("notes"), is("If you have one of these"));
    }

    @Test
    void givenGetItemsByName_whenCorrectRequest_thenResponseIsOkAndContainsTheItem() {
        doReturn(INFOBOX_ITEM_TEXT).when(articleRepository).getArticle("Carlin Sword");

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/items/Carlin Sword", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        final JSONObject resultAsJSON = new JSONObject(result.getBody());
        assertThat(resultAsJSON.get("templateType"), is("Item"));
        assertThat(resultAsJSON.get("name"), is("Carlin Sword"));
        assertThat(resultAsJSON.get("article"), is("a"));
        assertThat(resultAsJSON.get("actualname"), is("carlin sword"));
        assertThat(resultAsJSON.get("plural"), is("?"));
        assertThat(resultAsJSON.get("itemid").toString(), is("[\"3283\"]"));
        assertThat(resultAsJSON.get("marketable"), is("yes"));
        assertThat(resultAsJSON.get("usable"), is("yes"));
        assertThat(resultAsJSON.get("sprites"), is("{{Frames|{{Frame Sprite|55266}}}}"));
        assertThat(resultAsJSON.get("flavortext"), is("Foobar"));
        assertThat(resultAsJSON.get("itemclass"), is("Weapons"));
        assertThat(resultAsJSON.get("primarytype"), is("Sword Weapons"));
        assertThat(resultAsJSON.get("levelrequired"), is("0"));
        assertThat(resultAsJSON.get("hands"), is("One"));
        assertThat(resultAsJSON.get("type"), is("Sword"));
        assertThat(resultAsJSON.get("attack"), is("15"));
        assertThat(resultAsJSON.get("defense"), is("13"));
        assertThat(resultAsJSON.get("defensemod"), is("+1"));
        assertThat(resultAsJSON.get("enchantable"), is("no"));
        assertThat(resultAsJSON.get("weight"), is("40.00"));
        assertThat(resultAsJSON.get("droppedby").toString(), is("[\"Grorlam\",\"Stone Golem\"]"));
        assertThat(resultAsJSON.get("value"), is("118"));
        assertThat(resultAsJSON.get("npcvalue"), is("118"));
        assertThat(resultAsJSON.get("npcprice"), is("473"));
        assertThat(resultAsJSON.get("npcvaluerook"), is("0"));
        assertThat(resultAsJSON.get("npcpricerook"), is("0"));
        assertThat(resultAsJSON.get("buyfrom"), is("Baltim, Brengus, Cedrik,"));
        assertThat(resultAsJSON.get("sellto"), is("Baltim, Brengus, Cedrik, Esrik,"));
        assertThat(resultAsJSON.get("notes"), is("If you have one of these"));
    }

    @Test
    void givenGetItemsByName_whenWrongRequest_thenResponseIsNotFound() {
        doReturn(null).when(articleRepository).getArticle("Foobar");

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/items/Foobar", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void givenPutItem_whenCorrectRequest_thenResponseIsOkAndContainsTheModifiedItem() {
        final String editSummary = "[bot] editing during integration test";

        doReturn(INFOBOX_ITEM_TEXT).when(articleRepository).getArticle("Carlin Sword");
        doReturn(true).when(articleRepository).modifyArticle(anyString(), anyString(), anyString());

        final HttpHeaders httpHeaders = makeHttpHeaders(editSummary);

        final ResponseEntity<Void> result = restTemplate.exchange("/api/items", HttpMethod.PUT, new HttpEntity<>(makeItem(), httpHeaders), Void.class);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void givenPutItem_whenCorrectRequestButUnableToEditWiki_thenResponseIsBadRequest() {
        final String editSummary = "[bot] editing during integration test";

        doReturn(INFOBOX_ITEM_TEXT).when(articleRepository).getArticle("Carlin Sword");
        doReturn(false).when(articleRepository).modifyArticle(anyString(), anyString(), anyString());

        final HttpHeaders httpHeaders = makeHttpHeaders(editSummary);

        final ResponseEntity<Void> result = restTemplate.exchange("/api/items", HttpMethod.PUT, new HttpEntity<>(makeItem(), httpHeaders), Void.class);
        assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST));
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
                .objectclass(ObjectClass.WEAPONS)
                .primarytype("Sword Weapons")
                .levelrequired(0)
                .hands(Hands.One)
                .weapontype(WeaponType.Sword)
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
}
