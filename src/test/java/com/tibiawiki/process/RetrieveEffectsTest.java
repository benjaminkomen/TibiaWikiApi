package com.tibiawiki.process;

import com.tibiawiki.domain.mediawiki.ArticleRepository;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.JsonFactory;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class RetrieveEffectsTest {

    private static final String SOME_PAGE_NAME = "Foobar";
    private static final String SOME_ARTICLE_CONTENT = "";
    private static final JSONObject SOME_JSON_OBJECT = new JSONObject();
    private static final String SOME_EFFECT_NAME = "Yellow Poison Effect";
    private static final String SOME_OTHER_EFFECT_NAME = "Ice Missile Effect";
    private static final String SOME_LIST_NAME = "EffectList";

    private RetrieveEffects target;
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private JsonFactory jsonFactory;

    @BeforeEach
    public void setup() {
        articleRepository = mock(ArticleRepository.class);

        jsonFactory = mock(JsonFactory.class);
        target = new RetrieveEffects(articleRepository, jsonFactory);

        doReturn(SOME_ARTICLE_CONTENT).when(articleFactory).extractInfoboxPartOfArticle(any(String.class));
        doReturn(SOME_JSON_OBJECT).when(jsonFactory).convertInfoboxPartOfArticleToJson(any(String.class));
    }

    @Test
    public void testGetEffectsJSON_ZeroResults() {
        final List<String> lists = Collections.emptyList();
        final List<String> achievements = Collections.emptyList();

        doReturn(achievements).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.EFFECT.getCategoryName());
        doReturn(lists).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);

        List<JSONObject> result = target.getEffectsJSON();

        assertThat(result, hasSize(0));
    }

    @Test
    public void testGetEffectsJSON_TwoResults() {
        final List<String> lists = Collections.singletonList(SOME_LIST_NAME);
        final List<String> achievements = Arrays.asList(SOME_EFFECT_NAME, SOME_OTHER_EFFECT_NAME);
        final Map<String, String> pagenamesAndArticlesMap = Map.of(SOME_EFFECT_NAME, SOME_ARTICLE_CONTENT,
                SOME_OTHER_EFFECT_NAME, SOME_ARTICLE_CONTENT);

        doReturn(achievements).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.EFFECT.getCategoryName());
        doReturn(lists).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(pagenamesAndArticlesMap).when(articleRepository).getArticlesFromCategory(anyList());

        List<JSONObject> result = target.getEffectsJSON();

        assertThat(result, hasSize(2));
    }

    @Test
    public void testGetEffectJSON() {
        doReturn("").when(articleRepository).getArticle(SOME_PAGE_NAME);

        var result = target.getEffectJSON(SOME_PAGE_NAME);

        assertThat(result, is(SOME_JSON_OBJECT));
    }

}