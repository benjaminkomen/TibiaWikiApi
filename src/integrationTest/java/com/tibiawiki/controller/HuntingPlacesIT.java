package com.tibiawiki.controller;

import com.tibiawiki.domain.mediawiki.ArticleRepository;
import com.tibiawiki.domain.enums.InfoboxTemplate;
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

import static com.tibiawiki.process.RetrieveAny.CATEGORY_LISTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HuntingPlacesIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ArticleRepository articleRepository;

    private static final String INFOBOX_HUNT_TEXT = """
            {{Infobox Hunt
            | name            = Tiquanda Bandit Caves
            | image           = Bandit
            | implemented     = 7.5
            | city            = Port Hope
            | location        = [[Tiquanda]], north of [[Port Hope]], {{Mapper Coords|127.43|127.167|7|3|text=here}}.
            | vocation        = All vocations.
            | lvlknights      = 20
            | lvlpaladins     = 20
            | lvlmages        = 15
            | skknights       = 50
            | skpaladins      = 50
            | skmages         = 
            | defknights      = 45
            | defpaladins     = 40
            | defmages        =
            | exp             = Bad
            | expstar         = 2
            | loot            = Very Bad
            | lootstar        = 1
            | bestloot        = Deer Trophy
            | bestloot2       = Iron Helmet
            | bestloot3       = Brass Armor
            | bestloot4       = Wolf Paw
            | map             = Tiquanda Bandit,Reptile,Crustacean Caves-1.gif
            | map2            = Tiquanda Bandit,Reptile,Crustacean Caves-2.gif
            | map3            = Tiquanda Bandit,Reptile,Crustacean Caves-3.gif
            | mapwidth        = 250px
            }}
            """;

    @Test
    void givenGetHuntingPlacesNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoHuntingPlaceNames() {
        doReturn(Collections.singletonList("baz")).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(Arrays.asList("foo", "bar", "baz")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.HUNT.getCategoryName());

        final ResponseEntity<List> result = restTemplate.getForEntity("/api/huntingplaces?expand=false", List.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody(), is(notNullValue()));
        assertThat(result.getBody().size(), is(2));
        assertThat(result.getBody().get(0), is("foo"));
        assertThat(result.getBody().get(1), is("bar"));
    }

    @Test
    void givenGetHuntingPlaceByName_whenCorrectRequest_thenResponseIsOkAndContainsTheHuntingPlaceWithSlashInName() {
        doReturn(INFOBOX_HUNT_TEXT).when(articleRepository).getArticle("Tiquanda/Bandit Caves");

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/huntingplaces/Tiquanda/Bandit Caves", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        final JSONObject resultAsJSON = new JSONObject(result.getBody());
        assertThat(resultAsJSON.get("templateType"), is("Hunt"));
        assertThat(resultAsJSON.get("city"), is("Port Hope"));
        assertThat(resultAsJSON.get("lvlmages"), is("15"));
        assertThat(resultAsJSON.get("lvlknights"), is("20"));
        assertThat(resultAsJSON.get("defpaladins"), is("40"));
        assertThat(resultAsJSON.get("mapwidth"), is("250px"));
        assertThat(resultAsJSON.get("expstar"), is("2"));
        assertThat(resultAsJSON.get("lvlpaladins"), is("20"));
        assertThat(resultAsJSON.get("bestloot"), is("Deer Trophy"));
        assertThat(resultAsJSON.get("implemented"), is("7.5"));
        assertThat(resultAsJSON.get("loot"), is("Very Bad"));
        assertThat(resultAsJSON.get("exp"), is("Bad"));
        assertThat(resultAsJSON.get("map"), is("Tiquanda Bandit,Reptile,Crustacean Caves-1.gif"));
        assertThat(resultAsJSON.get("image"), is("Bandit"));
        assertThat(resultAsJSON.get("skpaladins"), is("50"));
        assertThat(resultAsJSON.get("map3"), is("Tiquanda Bandit,Reptile,Crustacean Caves-3.gif"));
        assertThat(resultAsJSON.get("map2"), is("Tiquanda Bandit,Reptile,Crustacean Caves-2.gif"));
        assertThat(resultAsJSON.get("bestloot4"), is("Wolf Paw"));
        assertThat(resultAsJSON.get("bestloot2"), is("Iron Helmet"));
        assertThat(resultAsJSON.get("bestloot3"), is("Brass Armor"));
        assertThat(resultAsJSON.get("skknights"), is("50"));
        assertThat(resultAsJSON.get("defknights"), is("45"));
        assertThat(resultAsJSON.get("lootstar"), is("1"));
        assertThat(resultAsJSON.get("vocation"), is("All vocations."));
        assertThat(resultAsJSON.get("name"), is("Tiquanda Bandit Caves"));
        assertThat(resultAsJSON.get("location"), is("[[Tiquanda]], north of [[Port Hope]], {{Mapper Coords|127.43|127.167|7|3|text=here}}."));
    }

    @Test
    void givenGetHuntingPlaceByName_whenWrongRequest_thenResponseIsNotFound() {
        doReturn(null).when(articleRepository).getArticle("Foobar");

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/huntingplaces/Foobar", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }
}
