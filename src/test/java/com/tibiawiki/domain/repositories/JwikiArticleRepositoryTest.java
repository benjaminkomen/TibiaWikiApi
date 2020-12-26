package com.tibiawiki.domain.repositories;

import org.fastily.jwiki.core.NS;
import org.fastily.jwiki.core.Wiki;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class JwikiArticleRepositoryTest {

    private static final String SOME_CATEGORY_NAME = "Achievements";
    private static final String SOME_PAGE_NAME = "Goo Goo Dancer";
    private static final String SOME_TEMPLATE_NAME = "Template:Infobox_Item";
    private JwikiArticleRepository target;
    private Wiki wiki;

    @BeforeEach
    public void setup() {
        wiki = mock(Wiki.class);
        target = new JwikiArticleRepository(wiki);
    }

    @Test
    void testGetPageNamesFromCategory() {
        ArrayList<String> someList = new ArrayList<>();
        someList.add("foo");
        someList.add("bar");
        doReturn(someList).when(wiki).getCategoryMembers(SOME_CATEGORY_NAME, NS.MAIN);
        List<String> result = target.getPageNamesFromCategory(SOME_CATEGORY_NAME);

        assertThat(result, notNullValue());
        assertThat(result.get(0), is("foo"));
        assertThat(result.get(1), is("bar"));
    }

    @Test
    void testGetPageNamesUsingTemplate() {
        ArrayList<String> someList = new ArrayList<>();
        someList.add("foo");
        someList.add("bar");
        doReturn(someList).when(wiki).whatTranscludesHere(SOME_TEMPLATE_NAME, NS.MAIN);
        List<String> result = target.getPageNamesUsingTemplate(SOME_TEMPLATE_NAME);

        assertThat(result, notNullValue());
        assertThat(result.get(0), is("foo"));
        assertThat(result.get(1), is("bar"));
    }

    @Test
    void testGetArticle_Success() {
        doReturn("Foobar").when(wiki).getPageText(SOME_PAGE_NAME);
        String result = target.getArticle(SOME_PAGE_NAME);

        assertThat(result, is("Foobar"));
    }

    @Test
    void testGetArticle_NullWhenEmpty() {
        doReturn("").when(wiki).getPageText(SOME_PAGE_NAME);
        String result = target.getArticle(SOME_PAGE_NAME);

        assertThat(result, is(nullValue()));
    }

    @Test
    void testModifyArticle_DryRunBecauseDebugEnabled() {
        target.enableDebug();
        boolean result = target.modifyArticle(SOME_PAGE_NAME, "Foobar", "[bot] formatting pages in uniform way");

        assertThat(result, is(true));
    }

    @Test
    void testModifyArticle_SuccessDebugDisabled() {
        target.disableDebug();
        doReturn(true).when(wiki).edit(anyString(), anyString(), anyString());
        boolean result = target.modifyArticle(SOME_PAGE_NAME, "Foobar", "[bot] formatting pages in uniform way");

        assertThat(result, is(true));
    }

    @Test
    void testModifyArticle_FailureDebugDisabled() {
        target.disableDebug();
        doReturn(false).when(wiki).edit(anyString(), anyString(), anyString());
        boolean result = target.modifyArticle(SOME_PAGE_NAME, "Foobar", "[bot] formatting pages in uniform way");

        assertThat(result, is(false));
    }

    @Test
    void testLoginSuccess() {
        doReturn(true).when(wiki).login(anyString(), anyString());
        boolean result = target.login(wiki);

        assertThat(result, is(true));
    }

    @Test
    void testLoginFailure() {
        doReturn(false).when(wiki).login(anyString(), anyString());
        boolean result = target.login(wiki);

        assertThat(result, is(false));
    }
}