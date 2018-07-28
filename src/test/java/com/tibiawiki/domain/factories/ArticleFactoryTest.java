package com.tibiawiki.domain.factories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ArticleFactoryTest {

    private static final String SOME_TITLE = "Goo Goo Dancer";
    private static final String SOME_TEXT_ONLY_INFOBOX = "{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Goo Goo Dancer\n" +
            "}}";
    private static final String SOME_TEXT_NO_INFOBOX = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. {{}}}}}{";
    private static final String SOME_TEXT_EMPTY = "";

    private ArticleFactory target;


//    @BeforeEach
//    public void setup() {
//        target = new ArticleFactory();
//    }
//
//    @Test
//    public void testExtractInfoboxPartOfArticle_EmptyText() {
//        String result = target.extractInfoboxPartOfArticle(SOME_TEXT_EMPTY);
//
//        assertThat(result, is(""));
//    }
//
//    @Test
//    public void testExtractInfoboxPartOfArticle_NoInfobox() {
//        String result = target.extractInfoboxPartOfArticle(SOME_TEXT_NO_INFOBOX);
//
//        assertThat(result, is(""));
//    }
//
//    @Test
//    public void testExtractInfoboxPartOfArticle_OnlyInfoboxInArticleText() {
//        String result = target.extractInfoboxPartOfArticle(SOME_TEXT_ONLY_INFOBOX);
//
//        assertThat(result, is(SOME_TEXT_ONLY_INFOBOX));
//    }
}