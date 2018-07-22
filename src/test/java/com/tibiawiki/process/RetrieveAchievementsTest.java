package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersSimple;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tibiawiki.process.RetrieveAchievements.CATEGORY_ACHIEVEMENTS;
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
    @Mock
    private MediaWikiBot mediaWikiBot;

    @BeforeEach
    public void setup() {
        articleRepository = mock(ArticleRepository.class);
        articleFactory = mock(ArticleFactory.class);
        jsonFactory = mock(JsonFactory.class);
        mediaWikiBot = mock(MediaWikiBot.class);
        target = new RetrieveAchievements(articleRepository, articleFactory, jsonFactory);

        doReturn(makeArticle(SOME_PAGE_NAME)).when(articleRepository).getArticle(any(String.class));
        doReturn(SOME_ARTICLE_CONTENT).when(articleFactory).extractInfoboxPartOfArticle(any(Article.class));
        doReturn(SOME_JSON_OBJECT).when(jsonFactory).convertInfoboxPartOfArticleToJson(any(String.class));
    }

    @Test
    public void testGetAchievementsJSON_ZeroResults() {
        CategoryMembersSimple achievements = mock(CategoryMembersSimple.class);
        mockIterable(achievements);
        CategoryMembersSimple lists = mock(CategoryMembersSimple.class);
        mockIterable(lists);

        doReturn(achievements).when(articleRepository).getMembersFromCategory(CATEGORY_ACHIEVEMENTS);
        doReturn(lists).when(articleRepository).getMembersFromCategory(CATEGORY_LISTS);

        Stream<JSONObject> result = target.getAchievementsJSON();

        assertThat(result.collect(Collectors.toList()), hasSize(0));
    }

    @Test
    public void testGetAchievementsJSON_TwoResults() {
        CategoryMembersSimple achievements = mock(CategoryMembersSimple.class);
        mockIterable(achievements, SOME_ACHIEVEMENT_NAME, SOME_OTHER_ACHIEVEMENT_NAME, SOME_LIST_NAME);
        CategoryMembersSimple lists = mock(CategoryMembersSimple.class);
        mockIterable(lists, SOME_LIST_NAME);

        doReturn(achievements).when(articleRepository).getMembersFromCategory(CATEGORY_ACHIEVEMENTS);
        doReturn(lists).when(articleRepository).getMembersFromCategory(CATEGORY_LISTS);

        Stream<JSONObject> result = target.getAchievementsJSON();

        assertThat(result.collect(Collectors.toList()), hasSize(2));
    }

    @Test
    public void testGetAchievementsJSON_SeventyfiveResults() {
        CategoryMembersSimple achievements = mock(CategoryMembersSimple.class);
        mockIterable(achievements, makeTitleVarargs());
        CategoryMembersSimple lists = mock(CategoryMembersSimple.class);
        mockIterable(lists);

        doReturn(achievements).when(articleRepository).getMembersFromCategory(CATEGORY_ACHIEVEMENTS);
        doReturn(lists).when(articleRepository).getMembersFromCategory(CATEGORY_LISTS);
        doReturn(Arrays.asList(makeArticle(SOME_PAGE_NAME))).when(articleRepository).getArticles(anyList());

        Stream<JSONObject> result = target.getAchievementsJSON(false);

        assertThat(result.collect(Collectors.toList()), hasSize(2));
    }

    private String[] makeTitleVarargs() {
        String[] titles = new String[75];

        for (int i = 0; i < 75; i++) {
            titles[i] = "";
        }

        return titles;
    }

    @Test
    public void testGetAchievementJSON() {
        Optional<JSONObject> result = target.getAchievementJSON(SOME_PAGE_NAME);

        assertThat(result.get(), is(SOME_JSON_OBJECT));
    }

    private Article makeArticle(String title) {
        return new Article(mediaWikiBot, title);
    }

    /**
     * Copied from http://batey.info/mocking-iterable-objects-generically.html
     */
    public static <T> void mockIterable(Iterable<T> iterable, T... values) {
        Iterator<T> mockIterator = mock(Iterator.class);
        when(iterable.iterator()).thenReturn(mockIterator);

        if (values.length == 0) {
            when(mockIterator.hasNext()).thenReturn(false);
            return;
        } else if (values.length == 1) {
            when(mockIterator.hasNext()).thenReturn(true, false);
            when(mockIterator.next()).thenReturn(values[0]);
        } else {
            // build boolean array for hasNext()
            Boolean[] hasNextResponses = new Boolean[values.length];
            for (int i = 0; i < hasNextResponses.length - 1; i++) {
                hasNextResponses[i] = true;
            }
            hasNextResponses[hasNextResponses.length - 1] = false;
            when(mockIterator.hasNext()).thenReturn(true, hasNextResponses);
            T[] valuesMinusTheFirst = Arrays.copyOfRange(values, 1, values.length);
            when(mockIterator.next()).thenReturn(values[0], valuesMinusTheFirst);
        }
    }

}