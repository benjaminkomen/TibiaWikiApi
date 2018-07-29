package com.tibiawiki.domain.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

public class ArticleRepositoryTest {

    private static final String SOME_CATEGORY_NAME = "Achievements";
    private static final String SOME_PAGE_NAME = "Goo Goo Dancer";
    private static final String SOME_OTHER_PAGE_NAME = "Foobar";
    private ArticleRepository target;

//    @BeforeEach
//    public void setup() {
//        target = new ArticleRepository();
//    }
//
//    @Test
//    public void testGetMembersFromCategory() {
//        List<String> result = target.getPageNamesFromCategory(SOME_CATEGORY_NAME);
//
//        assertThat(result, notNullValue());
//    }
//
//    @Test
//    public void testGetArticle() {
//        String result = target.getArticle(SOME_PAGE_NAME);
//
//        assertThat(result, notNullValue());
//    }
//
//    @Test
//    public void testGetArticles_EmptyList() {
//        List<String> result = target.getArticles(Collections.emptyList());
//
//        assertThat(result, notNullValue());
//        assertThat(result, hasSize(0));
//    }
//
//    @Test
//    public void testGetArticles_ListOfTwo() {
//        List<String> result = target.getArticles(Arrays.asList(SOME_PAGE_NAME, SOME_OTHER_PAGE_NAME));
//
//        assertThat(result, notNullValue());
//        assertThat(result, hasSize(2));
//    }

}