package com.tibiawiki.serviceinterface;

import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.enums.YesNo;
import com.tibiawiki.domain.objects.Achievement;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.jetbrains.annotations.NotNull;
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

import static com.tibiawiki.process.RetrieveAny.CATEGORY_LISTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AchievementsResourceIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ArticleRepository articleRepository; // don't instantiate this real class, but use a mock implementation

    private static final String INFOBOX_ACHIEVEMENT_TEXT = "{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| grade        = 1\n" +
            "| name         = Goo Goo Dancer\n" +
            "| description  = Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!\n" +
            "| spoiler      = Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.\n" +
            "| premium      = yes\n" +
            "| points       = 1\n" +
            "| secret       = yes\n" +
            "| implemented  = 9.6\n" +
            "| achievementid = 319\n" +
            "| relatedpages = [[Muck Remover]], [[Mucus Plug]]\n" +
            "}}\n";

    @Test
    void givenGetAchievementsNotExpanded_whenCorrectRequest_thenResponseIsOkAndContainsTwoAchievementNames() {
        doReturn(Collections.singletonList("baz")).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(Arrays.asList("foo", "bar", "baz")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ACHIEVEMENT.getCategoryName());

        final ResponseEntity<List> result = restTemplate.getForEntity("/api/achievements?expand=false", List.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().size(), is(2));
        assertThat(result.getBody().get(0), is("foo"));
        assertThat(result.getBody().get(1), is("bar"));
    }

    @Test
    void givenGetAchievementsExpanded_whenCorrectRequest_thenResponseIsOkAndContainsOneAchievement() {
        doReturn(Collections.emptyList()).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(Collections.singletonList("Goo Goo Dancer")).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.ACHIEVEMENT.getCategoryName());
        doReturn(Map.of("Goo Goo Dancer", INFOBOX_ACHIEVEMENT_TEXT)).when(articleRepository).getArticlesFromCategory(Collections.singletonList("Goo Goo Dancer"));

        final ResponseEntity<List> result = restTemplate.getForEntity("/api/achievements?expand=true", List.class);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().size(), is(1));
        assertThat(((Map) result.getBody().get(0)).get("templateType"), is("Achievement"));
        assertThat(((Map) result.getBody().get(0)).get("premium"), is("yes"));
        assertThat(((Map) result.getBody().get(0)).get("relatedpages"), is("[[Muck Remover]], [[Mucus Plug]]"));
        assertThat(((Map) result.getBody().get(0)).get("grade"), is("1"));
        assertThat(((Map) result.getBody().get(0)).get("name"), is("Goo Goo Dancer"));
        assertThat(((Map) result.getBody().get(0)).get("implemented"), is("9.6"));
        assertThat(((Map) result.getBody().get(0)).get("description"), is("Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!"));
        assertThat(((Map) result.getBody().get(0)).get("achievementid"), is("319"));
        assertThat(((Map) result.getBody().get(0)).get("spoiler"), is("Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s."));
        assertThat(((Map) result.getBody().get(0)).get("secret"), is("yes"));
        assertThat(((Map) result.getBody().get(0)).get("points"), is("1"));
    }

    @Test
    void givenGetAchievementsByName_whenCorrectRequest_thenResponseIsOkAndContainsTheAchievement() {
        doReturn(INFOBOX_ACHIEVEMENT_TEXT).when(articleRepository).getArticle("Goo Goo Dancer");

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/achievements/Goo Goo Dancer", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        final JSONObject resultAsJSON = new JSONObject(result.getBody());
        assertThat(resultAsJSON.get("templateType"), is("Achievement"));
        assertThat(resultAsJSON.get("premium"), is("yes"));
        assertThat(resultAsJSON.get("relatedpages"), is("[[Muck Remover]], [[Mucus Plug]]"));
        assertThat(resultAsJSON.get("grade"), is("1"));
        assertThat(resultAsJSON.get("name"), is("Goo Goo Dancer"));
        assertThat(resultAsJSON.get("implemented"), is("9.6"));
        assertThat(resultAsJSON.get("description"), is("Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!"));
        assertThat(resultAsJSON.get("achievementid"), is("319"));
        assertThat(resultAsJSON.get("spoiler"), is("Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s."));
        assertThat(resultAsJSON.get("secret"), is("yes"));
        assertThat(resultAsJSON.get("points"), is("1"));
    }

    @Test
    void givenGetAchievementsByName_whenWrongRequest_thenResponseIsNotFound() {
        doReturn(null).when(articleRepository).getArticle("Foobar");

        final ResponseEntity<String> result = restTemplate.getForEntity("/api/achievements/Foobar", String.class);
        assertThat(result.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void givenPutAchievement_whenCorrectRequest_thenResponseIsOkAndContainsTheModifiedAchievement() {
        final String editSummary = "[bot] editing during integration test";

        doReturn(INFOBOX_ACHIEVEMENT_TEXT).when(articleRepository).getArticle("Goo Goo Dancer");
        doReturn(true).when(articleRepository).modifyArticle(anyString(), anyString(), anyString());

        final HttpHeaders httpHeaders = makeHttpHeaders(editSummary);

        final ResponseEntity<Void> result = restTemplate.exchange("/api/achievements", HttpMethod.PUT, new HttpEntity<>(makeAchievement(), httpHeaders), Void.class);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    void givenPutAchievement_whenCorrectRequestButUnableToEditWiki_thenResponseIsBadRequest() {
        final String editSummary = "[bot] editing during integration test";

        doReturn(INFOBOX_ACHIEVEMENT_TEXT).when(articleRepository).getArticle("Goo Goo Dancer");
        doReturn(false).when(articleRepository).modifyArticle(anyString(), anyString(), anyString());

        final HttpHeaders httpHeaders = makeHttpHeaders(editSummary);

        final ResponseEntity<Void> result = restTemplate.exchange("/api/achievements", HttpMethod.PUT, new HttpEntity<>(makeAchievement(), httpHeaders), Void.class);
        assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST));
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

    @NotNull
    private HttpHeaders makeHttpHeaders(String editSummary) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-WIKI-Edit-Summary", editSummary);
        return httpHeaders;
    }
}
