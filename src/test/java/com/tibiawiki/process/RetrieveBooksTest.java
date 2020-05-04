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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class RetrieveBooksTest {

    private static final String SOME_PAGE_NAME = "Foobar";
    private static final String SOME_ARTICLE_CONTENT = "";
    private static final JSONObject SOME_JSON_OBJECT = new JSONObject();
    private static final String SOME_BOOK_NAME = "Dungeon Survival Guide (Book)";
    private static final String SOME_OTHER_BOOK_NAME = "Explorer Journal of Wilmot Dustheart (Book)";
    private static final String SOME_LIST_NAME = "BookList";

    private RetrieveBooks target;
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
        target = new RetrieveBooks(articleRepository, articleFactory, jsonFactory);

        doReturn(SOME_ARTICLE_CONTENT).when(articleFactory).extractInfoboxPartOfArticle(any(String.class));
        doReturn(SOME_JSON_OBJECT).when(jsonFactory).convertInfoboxPartOfArticleToJson(any(String.class));
    }

    @Test
    public void testGetBooksJSON_ZeroResults() {
        final List<String> lists = Collections.emptyList();
        final List<String> achievements = Collections.emptyList();

        doReturn(achievements).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.BOOK.getCategoryName());
        doReturn(lists).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);

        List<JSONObject> result = target.getBooksJSON()
                .collect(Collectors.toList());

        assertThat(result, hasSize(0));
    }

    @Test
    public void testGetBooksJSON_TwoResults() {
        final List<String> lists = Collections.singletonList(SOME_LIST_NAME);
        final List<String> achievements = Arrays.asList(SOME_BOOK_NAME, SOME_OTHER_BOOK_NAME);
        final Map<String, String> pagenamesAndArticlesMap = Map.of(SOME_BOOK_NAME, SOME_ARTICLE_CONTENT,
                SOME_OTHER_BOOK_NAME, SOME_ARTICLE_CONTENT);

        doReturn(achievements).when(articleRepository).getPageNamesFromCategory(InfoboxTemplate.BOOK.getCategoryName());
        doReturn(lists).when(articleRepository).getPageNamesFromCategory(CATEGORY_LISTS);
        doReturn(pagenamesAndArticlesMap).when(articleRepository).getArticlesFromCategory(anyList());

        List<JSONObject> result = target.getBooksJSON()
                .collect(Collectors.toList());

        assertThat(result, hasSize(2));
    }

    @Test
    public void testGetBookJSON() {
        doReturn("").when(articleRepository).getArticle(SOME_PAGE_NAME);

        Optional<JSONObject> result = target.getBookJSON(SOME_PAGE_NAME);

        assertThat(result.get(), is(SOME_JSON_OBJECT));
    }

}