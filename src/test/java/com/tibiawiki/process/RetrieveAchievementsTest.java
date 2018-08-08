package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tibiawiki.process.RetrieveAchievements.CATEGORY;
import static com.tibiawiki.process.RetrieveAchievements.CATEGORY_LISTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class RetrieveAchievementsTest {

    private static final String SOME_PAGE_NAME = "Foobar";
    private static final String SOME_ARTICLE_CONTENT = "";
    private static final JSONObject SOME_JSON_OBJECT = new JSONObject();
    private static final String SOME_ACHIEVEMENT_NAME = "Goo Goo Dancer";
    private static final String SOME_OTHER_ACHIEVEMENT_NAME = "Fire Devil";
    private static final String SOME_LIST_NAME = "Achievements/DPL";

    private RetrieveAchievements target;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ArticleFactory articleFactory;
    @Mock
    private JsonFactory jsonFactory;

    @BeforeEach
    public void setup() {
        articleRepository = mock(ArticleRepository.class);
        articleFactory = mock(ArticleFactory.class);
        jsonFactory = mock(JsonFactory.class);
        target = new RetrieveAchievements(articleRepository, articleFactory, jsonFactory);

        doReturn(SOME_ARTICLE_CONTENT).when(articleFactory).extractInfoboxPartOfArticle(any(String.class));
        doReturn(SOME_JSON_OBJECT).when(jsonFactory).convertInfoboxPartOfArticleToJson(any(String.class));
    }

    @Test
    public void testGetAchievementsJSON_ZeroResults() {
        final List<String> lists = Collections.emptyList();
        final List<String> achievements = Collections.emptyList();

        doReturn(achievements).when(articleRepository).getPageNamesFromCategory(CATEGORY);
        doReturn(lists).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);

        Stream<JSONObject> result = target.getAchievementsJSON();

        assertThat(result.collect(Collectors.toList()), hasSize(0));
    }

    @Disabled
    @Test
    public void testGetAchievementsJSON_TwoResults() {
        final List<String> lists = Collections.singletonList(SOME_LIST_NAME);
        final List<String> achievements = Arrays.asList(SOME_ACHIEVEMENT_NAME, SOME_OTHER_ACHIEVEMENT_NAME);

        doReturn(achievements).when(articleRepository).getPageNamesFromCategory(CATEGORY);
        doReturn(lists).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);

        Stream<JSONObject> result = target.getAchievementsJSON();

        assertThat(result.collect(Collectors.toList()), hasSize(2));
    }

    @Disabled
    @Test
    public void testGetAchievementsJSON_SeventyfiveResults() {
        final List<String> lists = Collections.singletonList(SOME_LIST_NAME);
        final List<String> achievements = Arrays.asList(makeTitleVarargs());

        doReturn(achievements).when(articleRepository).getPageNamesFromCategory(CATEGORY);
        doReturn(lists).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);

        Stream<JSONObject> result = target.getAchievementsJSON();

        assertThat(result.collect(Collectors.toList()), hasSize(2));
    }

    private String[] makeTitleVarargs() {
        String[] titles = new String[75];

        for (int i = 0; i < 75; i++) {
            titles[i] = "";
        }

        return titles;
    }

    @Disabled
    @Test
    public void testGetAchievementJSON() {
        Optional<JSONObject> result = target.getAchievementJSON(SOME_PAGE_NAME);

        assertThat(result.get(), is(SOME_JSON_OBJECT));
    }
}