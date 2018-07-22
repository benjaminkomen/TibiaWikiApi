package com.tibiawiki.domain.repositories;

import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersSimple;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class ArticleRepositoryTest {

    private static final String SOME_CATEGORY_NAME = "Achievements";
    private static final String SOME_PAGE_NAME = "Goo Goo Dancer";
    private static final String SOME_OTHER_PAGE_NAME = "Foobar";
    private ArticleRepository target;
    @Mock
    private MediaWikiBot mediaWikiBot;

    @BeforeEach
    public void setup() {
        mediaWikiBot = mock(MediaWikiBot.class);
        target = new ArticleRepository(mediaWikiBot);
    }

    @Test
    public void testGetMembersFromCategory() {
        CategoryMembersSimple result = target.getMembersFromCategory(SOME_CATEGORY_NAME);

        assertThat(result, notNullValue());
    }

    @Test
    public void testGetArticle() {
        Article article = new Article(mediaWikiBot, SOME_PAGE_NAME);
        Mockito.doReturn(article).when(mediaWikiBot).getArticle(any());

        Article result = target.getArticle(SOME_PAGE_NAME);

        assertThat(result, notNullValue());
        assertThat(result.getTitle(), is(SOME_PAGE_NAME));
    }

    @Test
    public void testGetArticles_EmptyList() {
        Mockito.doReturn(ImmutableList.of()).when(mediaWikiBot).readData();

        List<Article> result = target.getArticles(Collections.emptyList());

        assertThat(result, notNullValue());
        assertThat(result, hasSize(0));
    }

    @Test
    public void testGetArticles_ListOfTwo() {
        SimpleArticle simpleArticle1 = new SimpleArticle(SOME_PAGE_NAME);
        SimpleArticle simpleArticle2 = new SimpleArticle(SOME_OTHER_PAGE_NAME);
        Mockito.doReturn(ImmutableList.of(simpleArticle1, simpleArticle2).asList())
                .when(mediaWikiBot).readData(new String[]{SOME_PAGE_NAME, SOME_OTHER_PAGE_NAME});

        List<Article> result = target.getArticles(Arrays.asList(SOME_PAGE_NAME, SOME_OTHER_PAGE_NAME));

        assertThat(result, notNullValue());
        assertThat(result, hasSize(2));
    }

    @Test
    public void testGetArticles_EmptyVarArgs() {
        Mockito.doReturn(ImmutableList.of()).when(mediaWikiBot).readData();

        List<Article> result = target.getArticles(new String[0]);

        assertThat(result, notNullValue());
        assertThat(result, hasSize(0));
    }

    @Test
    public void testGetArticles_VarArgsOfTwo() {
        SimpleArticle simpleArticle1 = new SimpleArticle(SOME_PAGE_NAME);
        SimpleArticle simpleArticle2 = new SimpleArticle(SOME_OTHER_PAGE_NAME);
        Mockito.doReturn(ImmutableList.of(simpleArticle1, simpleArticle2).asList())
                .when(mediaWikiBot).readData(new String[]{SOME_PAGE_NAME, SOME_OTHER_PAGE_NAME});

        List<Article> result = target.getArticles(new String[]{SOME_PAGE_NAME, SOME_OTHER_PAGE_NAME});

        assertThat(result, notNullValue());
        assertThat(result, hasSize(2));
    }

}