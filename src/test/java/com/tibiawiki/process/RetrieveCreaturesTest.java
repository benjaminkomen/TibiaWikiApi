package com.tibiawiki.process;

import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tibiawiki.process.RetrieveAny.CATEGORY_LISTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class RetrieveCreaturesTest {

    private static final String SOME_PAGE_NAME = "Foobar";
    private static final String SOME_ARTICLE_CONTENT = "";
    private static final JSONObject SOME_JSON_OBJECT = new JSONObject();
    private static final String SOME_CREATURE_NAME = "Drillworm";
    private static final String SOME_OTHER_CREATURE_NAME = "Ghost of a Planegazer";
    private static final String SOME_LIST_NAME = "CreatureList";

    private RetrieveCreatures target;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ArticleFactory articleFactory;
    @Mock
    private JsonFactory jsonFactory;

    @BeforeEach
    void setup() {
        articleRepository = mock(ArticleRepository.class);
        articleFactory = mock(ArticleFactory.class);
        jsonFactory = mock(JsonFactory.class);
        target = new RetrieveCreatures(articleRepository, articleFactory, jsonFactory);

        doReturn(SOME_ARTICLE_CONTENT).when(articleFactory).extractInfoboxPartOfArticle(any(String.class));
        doReturn(SOME_JSON_OBJECT).when(jsonFactory).convertInfoboxPartOfArticleToJson(any(String.class));
    }

    @Test
    void testGetCreaturesJSON_ZeroResults() {
        final List<String> lists = Collections.emptyList();
        final List<String> achievements = Collections.emptyList();

        doReturn(achievements).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.CREATURE.getCategoryName());
        doReturn(lists).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);

        List<JSONObject> result = target.getCreaturesJSON()
                .collect(Collectors.toList());

        assertThat(result, hasSize(0));
    }

    @Test
    void testGetCreaturesJSON_TwoResults() {
        final List<String> lists = Collections.singletonList(SOME_LIST_NAME);
        final List<String> achievements = Arrays.asList(SOME_CREATURE_NAME, SOME_OTHER_CREATURE_NAME);
        final Map<String, String> pagenamesAndArticlesMap = Map.of(SOME_CREATURE_NAME, SOME_ARTICLE_CONTENT,
                SOME_OTHER_CREATURE_NAME, SOME_ARTICLE_CONTENT);

        doReturn(achievements).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.CREATURE.getCategoryName());
        doReturn(lists).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(pagenamesAndArticlesMap).when(articleRepository).getArticlesFromCategory(anyList());

        List<JSONObject> result = target.getCreaturesJSON()
                .collect(Collectors.toList());

        assertThat(result, hasSize(2));
    }

    @Test
    void testGetCreatureJSON() {
        doReturn("").when(articleRepository).getArticle(SOME_PAGE_NAME);

        Optional<JSONObject> result = target.getCreatureJSON(SOME_PAGE_NAME);

        assertThat(result.orElseThrow(), is(SOME_JSON_OBJECT));
    }
}