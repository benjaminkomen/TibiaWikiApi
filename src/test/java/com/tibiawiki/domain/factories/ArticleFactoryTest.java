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
    private static final String SOME_TEXT_ONLY_LOOT2_TEMPLATE = "{{Loot2\n" +
            "|version=8.6\n" +
            "|kills=52807\n" +
            "|name=Bear\n" +
            "|Empty, times:24777\n" +
            "|Meat, times:21065\n" +
            "|Ham, times:10581\n" +
            "|Bear Paw, times:1043, amount:1, total:1043\n" +
            "|Honeycomb, times:250, amount:1, total:249\n" +
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
    private static final String SOME_TEXT_WITH_LOOT2_TEMPLATE = "__NOWYSIWYG__\n" +
            "\n" +
            "{{Loot2\n" +
            "|version=8.6\n" +
            "|kills=52807\n" +
            "|name=Bear\n" +
            "|Empty, times:24777\n" +
            "|Meat, times:21065\n" +
            "|Ham, times:10581\n" +
            "|Bear Paw, times:1043, amount:1, total:1043\n" +
            "|Honeycomb, times:250, amount:1, total:249\n" +
            "}}\n" +
            "\n" +
            "{{Loot\n" +
            "|version=8.54\n" +
            "|kills=526\n" +
            "|name=Bear\n" +
            "|Empty, 252\n" +
            "|[[Meat]], 233\n" +
            "|[[Ham]], 95\n" +
            "|[[Bear Paw]], 7\n" +
            "|[[Honeycomb]], 3\n" +
            "}}\n" +
            "<br/>Average gold: 0\n" +
            "\n" +
            "{{Loot\n" +
            "|version=8.5\n" +
            "|kills = 60\n" +
            "|name = Bear\n" +
            "|[[Bear Paw]], 2\n" +
            "|[[Ham]], 45\n" +
            "|[[Meat]], 112\n" +
            "|[[Worm]], 2\n" +
            "|[[Honeycomb]], 1\n" +
            "|Empty, 6\n" +
            "}}\n";
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
    public void testExtractLootPartOfArticle_EmptyText() {
        String result = target.extractLootPartOfArticle(SOME_TEXT_EMPTY);

        assertThat(result, is(""));
    }

    @Test
    public void testExtractLootPartOfArticle_NoLoot2Template() {
        String result = target.extractLootPartOfArticle(SOME_TEXT_NO_INFOBOX);

        assertThat(result, is(""));
    }

    @Test
    public void testExtractLootPartOfArticle_OnlyLoot2TemplateInArticleText() {
        String result = target.extractLootPartOfArticle(SOME_TEXT_ONLY_LOOT2_TEMPLATE);

        assertThat(result, is(SOME_TEXT_ONLY_LOOT2_TEMPLATE));
    }

    @Test
    public void testExtractLootPartOfArticle_ALotOfStuffInArticleText() {
        String result = target.extractLootPartOfArticle(SOME_TEXT_WITH_LOOT2_TEMPLATE);

        assertThat(result, is(SOME_TEXT_ONLY_LOOT2_TEMPLATE));
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