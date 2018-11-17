package com.tibiawiki.domain.factories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArticleFactoryTest {

    private static final String SOME_TEXT_ONLY_INFOBOX = "{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Goo Goo Dancer\n" +
            "}}";
    private static final String SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER = "<noinclude>{{merge|blabla}}</noinclude><!--\n" +
            "-->{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Goo Goo Dancer\n" +
            "}}\n" +
            "[[Category:Achievements Made By Aliens]]";
    private static final String SOME_TEXT_ONLY_INFOBOX2 = "{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Goo Goo Dancer\n" +
            "| points       = 5\n" +
            "}}";
    private static final String SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER2 = "<noinclude>{{merge|blabla}}</noinclude><!--\n" +
            "-->{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}\n" +
            "| name         = Goo Goo Dancer\n" +
            "| points       = 5\n" +
            "}}\n" +
            "[[Category:Achievements Made By Aliens]]";
    private static final String SOME_TEXT_NO_INFOBOX = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. {{}}}}}{";
    private static final String SOME_TEXT_EMPTY = "";

    private ArticleFactory target;


    @BeforeEach
    public void setup() {
        target = new ArticleFactory();
    }

    @Test
    public void testExtractInfoboxPartOfArticle_EmptyText() {
        String result = target.extractInfoboxPartOfArticle(SOME_TEXT_EMPTY);

        assertThat(result, is(""));
    }

    @Test
    public void testExtractInfoboxPartOfArticle_NoInfobox() {
        String result = target.extractInfoboxPartOfArticle(SOME_TEXT_NO_INFOBOX);

        assertThat(result, is(""));
    }

    @Test
    public void testExtractInfoboxPartOfArticle_OnlyInfoboxInArticleText() {
        String result = target.extractInfoboxPartOfArticle(SOME_TEXT_ONLY_INFOBOX);

        assertThat(result, is(SOME_TEXT_ONLY_INFOBOX));
    }

    @Test
    void testInsertInfoboxPartOfArticle_Empty() {
        Executable closure = () -> target.insertInfoboxPartOfArticle(SOME_TEXT_EMPTY, "foobar");
        assertThrows(IllegalArgumentException.class, closure);
    }

    @Test
    void testInsertInfoboxPartOfArticle_NoInfobox() {
        Executable closure = () -> target.insertInfoboxPartOfArticle(SOME_TEXT_NO_INFOBOX, "foobar");
        assertThrows(IllegalArgumentException.class, closure);
    }

    @Test
    void testInsertInfoboxPartOfArticle_OnlyInfoboxInArticleText() {
        String result = target.insertInfoboxPartOfArticle(SOME_TEXT_ONLY_INFOBOX, SOME_TEXT_ONLY_INFOBOX2);
        assertThat(result, is(SOME_TEXT_ONLY_INFOBOX2));
    }

    @Test
    void testInsertInfoboxPartOfArticle_WithTextBeforeAndAfter() {
        String result = target.insertInfoboxPartOfArticle(SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER, SOME_TEXT_ONLY_INFOBOX2);
        assertThat(result, is(SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER2));
    }
}