package com.tibiawiki.domain.factories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class ArticleFactoryTest {

    private static final String SOME_TEXT_ONLY_INFOBOX = """
            {{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Goo Goo Dancer
            }}""";
    private static final String SOME_TEXT_ONLY_LOOT2_TEMPLATE = """
            {{Loot2
            |version=8.6
            |kills=52807
            |name=Bear
            |Empty, times:24777
            |Meat, times:21065
            |Ham, times:10581
            |Bear Paw, times:1043, amount:1, total:1043
            |Honeycomb, times:250, amount:1, total:249
            }}""";
    private static final String SOME_TEXT_ONLY_LOOT2_RC_TEMPLATE = """
            {{Loot2_RC
            |version=8.6
            |kills=52807
            |name=Bear
            |Empty, times:24777
            |Meat, times:21065
            |Ham, times:10581
            |Bear Paw, times:1043, amount:1, total:1043
            |Honeycomb, times:250, amount:1, total:249
            }}""";
    private static final String SOME_TEXT_BOTH_LOOT2_AND_LOOT2_RC_TEMPLATE = """
            __NOWYSIWYG__

            {{Loot2
            |version=8.6
            |kills=52807
            |name=Bear
            |Empty, times:24777
            |Meat, times:21065
            |Ham, times:10581
            |Bear Paw, times:1043, amount:1, total:1043
            |Honeycomb, times:250, amount:1, total:249
            }}

            {{Loot2_RC
            |version=8.6
            |kills=52807
            |name=Bear
            |Empty, times:24777
            |Meat, times:21065
            |Ham, times:10581
            |Bear Paw, times:1043, amount:1, total:1043
            |Honeycomb, times:250, amount:1, total:249
            }}""";
    private static final String SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER = """
            <noinclude>{{merge|blabla}}</noinclude><!--
            -->{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Goo Goo Dancer
            }}
            [[Category:Achievements Made By Aliens]]""";
    private static final String SOME_TEXT_ONLY_INFOBOX2 = """
            {{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Goo Goo Dancer
            | points       = 5
            }}""";
    private static final String SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER2 = """
            <noinclude>{{merge|blabla}}</noinclude><!--
            -->{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Goo Goo Dancer
            | points       = 5
            }}
            [[Category:Achievements Made By Aliens]]""";
    private static final String SOME_TEXT_WITH_LOOT2_TEMPLATE = """
            __NOWYSIWYG__

            {{Loot2
            |version=8.6
            |kills=52807
            |name=Bear
            |Empty, times:24777
            |Meat, times:21065
            |Ham, times:10581
            |Bear Paw, times:1043, amount:1, total:1043
            |Honeycomb, times:250, amount:1, total:249
            }}

            {{Loot
            |version=8.54
            |kills=526
            |name=Bear
            |Empty, 252
            |[[Meat]], 233
            |[[Ham]], 95
            |[[Bear Paw]], 7
            |[[Honeycomb]], 3
            }}
            <br/>Average gold: 0

            {{Loot
            |version=8.5
            |kills = 60
            |name = Bear
            |[[Bear Paw]], 2
            |[[Ham]], 45
            |[[Meat]], 112
            |[[Worm]], 2
            |[[Honeycomb]], 1
            |Empty, 6
            }}
            """;
    private static final String SOME_TEXT_NO_INFOBOX = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. {{}}}}}{";
    private static final String SOME_TEXT_EMPTY = "";

    private ArticleFactory target;


    @BeforeEach
    void setup() {
        target = new ArticleFactory();
    }

    @Test
    void testExtractInfoboxPartOfArticle_EmptyText() {
        String result = target.extractInfoboxPartOfArticle(SOME_TEXT_EMPTY);

        assertThat(result, is(""));
    }

    @Test
    void testExtractInfoboxPartOfArticle_NoInfobox() {
        String result = target.extractInfoboxPartOfArticle(SOME_TEXT_NO_INFOBOX);

        assertThat(result, is(""));
    }

    @Test
    void testExtractInfoboxPartOfArticle_OnlyInfoboxInArticleText() {
        String result = target.extractInfoboxPartOfArticle(SOME_TEXT_ONLY_INFOBOX);

        assertThat(result, is(SOME_TEXT_ONLY_INFOBOX));
    }

    @Test
    void testExtractLootPartOfArticle_EmptyText() {
        String result = target.extractLootPartOfArticle("Unknown", SOME_TEXT_EMPTY);

        assertThat(result, is(""));
    }

    @Test
    void testExtractLootPartOfArticle_NoLoot2Template() {
        String result = target.extractLootPartOfArticle("Unknown", SOME_TEXT_NO_INFOBOX);

        assertThat(result, is(""));
    }

    @Test
    void testExtractLootPartOfArticle_OnlyLoot2TemplateInArticleText() {
        String result = target.extractLootPartOfArticle("Unknown", SOME_TEXT_ONLY_LOOT2_TEMPLATE);

        assertThat(result, is(SOME_TEXT_ONLY_LOOT2_TEMPLATE));
    }

    @Test
    void testExtractLootPartOfArticle_ALotOfStuffInArticleText() {
        String result = target.extractLootPartOfArticle("Unknown", SOME_TEXT_WITH_LOOT2_TEMPLATE);

        assertThat(result, is(SOME_TEXT_ONLY_LOOT2_TEMPLATE));
    }

    @Test
    void testExtractAllLootPartsOfArticle_EmptyText() {
        var result = target.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_EMPTY);

        assertThat("Test: empty text results in no matches", result.isEmpty());
    }

    @Test
    void testExtractAllLootPartsOfArticle_NoLoot2OrLoot2RCTemplate() {
        assertThat("Test: no Loot2 or Loot2_RC template results in no matches",
                target.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_NO_INFOBOX).isEmpty());
    }

    @Test
    void testExtractAllLootPartsOfArticle_OnlyLoot2TemplateInArticleText() {
        var result = target.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_ONLY_LOOT2_TEMPLATE);

        assertThat(result.get("loot2"), is(SOME_TEXT_ONLY_LOOT2_TEMPLATE));
        assertThat(result.get("loot2_rc"), nullValue());
    }

    @Test
    void testExtractAllLootPartsOfArticle_OnlyLoot2RCTemplateInArticleText() {
        var result = target.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_ONLY_LOOT2_RC_TEMPLATE);

        assertThat(result.get("loot2_rc"), is(SOME_TEXT_ONLY_LOOT2_RC_TEMPLATE));
        assertThat(result.get("loot2"), nullValue());
    }

    @Test
    void testExtractAllLootPartsOfArticle_BothLoot2AndLoot2RCTemplateInArticleText() {
        var result = target.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_BOTH_LOOT2_AND_LOOT2_RC_TEMPLATE);

        assertThat(result.get("loot2_rc"), notNullValue());
        assertThat(result.get("loot2"), notNullValue());
    }

    @Test
    void testInsertInfoboxPartOfArticle_Empty() {
        var result = target.insertInfoboxPartOfArticle(SOME_TEXT_EMPTY, "foobar");
        assertThat("Empty result when empty input", result.isEmpty());
    }

    @Test
    void testInsertInfoboxPartOfArticle_NoInfobox() {
        var result = target.insertInfoboxPartOfArticle(SOME_TEXT_NO_INFOBOX, "foobar");
        assertThat("Empty result when no infobox in input", result.isEmpty());
    }

    @Test
    void testInsertInfoboxPartOfArticle_OnlyInfoboxInArticleText() {
        var result = target.insertInfoboxPartOfArticle(SOME_TEXT_ONLY_INFOBOX, SOME_TEXT_ONLY_INFOBOX2);
        assertThat(result.orElseThrow(), is(SOME_TEXT_ONLY_INFOBOX2));
    }

    @Test
    void testInsertInfoboxPartOfArticle_WithTextBeforeAndAfter() {
        var result = target.insertInfoboxPartOfArticle(SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER, SOME_TEXT_ONLY_INFOBOX2);
        assertThat(result.orElseThrow(), is(SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER2));
    }
}