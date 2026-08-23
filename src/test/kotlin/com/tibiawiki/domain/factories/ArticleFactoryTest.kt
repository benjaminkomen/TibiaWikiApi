package com.tibiawiki.domain.factories

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ArticleFactoryTest {

    private lateinit var target: ArticleFactory

    @BeforeEach
    fun setup() {
        target = ArticleFactory()
    }

    @Test
    fun testExtractInfoboxPartOfArticle_EmptyText() {
        val result = target.extractInfoboxPartOfArticle(SOME_TEXT_EMPTY)

        assertThat(result, `is`(""))
    }

    @Test
    fun testExtractInfoboxPartOfArticle_NoInfobox() {
        val result = target.extractInfoboxPartOfArticle(SOME_TEXT_NO_INFOBOX)

        assertThat(result, `is`(""))
    }

    @Test
    fun testExtractInfoboxPartOfArticle_OnlyInfoboxInArticleText() {
        val result = target.extractInfoboxPartOfArticle(SOME_TEXT_ONLY_INFOBOX)

        assertThat(result, `is`(SOME_TEXT_ONLY_INFOBOX))
    }

    @Test
    fun testExtractLootPartOfArticle_EmptyText() {
        val result = target.extractLootPartOfArticle("Unknown", SOME_TEXT_EMPTY)

        assertThat(result, `is`(""))
    }

    @Test
    fun testExtractLootPartOfArticle_NoLoot2Template() {
        val result = target.extractLootPartOfArticle("Unknown", SOME_TEXT_NO_INFOBOX)

        assertThat(result, `is`(""))
    }

    @Test
    fun testExtractLootPartOfArticle_OnlyLoot2TemplateInArticleText() {
        val result = target.extractLootPartOfArticle("Unknown", SOME_TEXT_ONLY_LOOT2_TEMPLATE)

        assertThat(result, `is`(SOME_TEXT_ONLY_LOOT2_TEMPLATE))
    }

    @Test
    fun testExtractLootPartOfArticle_ALotOfStuffInArticleText() {
        val result = target.extractLootPartOfArticle("Unknown", SOME_TEXT_WITH_LOOT2_TEMPLATE)

        assertThat(result, `is`(SOME_TEXT_ONLY_LOOT2_TEMPLATE))
    }

    @Test
    fun testExtractAllLootPartsOfArticle_EmptyText() {
        var result = target.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_EMPTY)

        assertThat("Test: empty text results in no matches", result.isEmpty())
    }

    @Test
    fun testExtractAllLootPartsOfArticle_NoLoot2OrLoot2RCTemplate() {
        assertThat(
            "Test: no Loot2 or Loot2_RC template results in no matches",
            target.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_NO_INFOBOX).isEmpty()
        )
    }

    @Test
    fun testExtractAllLootPartsOfArticle_OnlyLoot2TemplateInArticleText() {
        var result = target.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_ONLY_LOOT2_TEMPLATE)

        assertThat(result.get("loot2"), `is`(SOME_TEXT_ONLY_LOOT2_TEMPLATE))
        assertThat(result.get("loot2_rc"), nullValue())
    }

    @Test
    fun testExtractAllLootPartsOfArticle_OnlyLoot2RCTemplateInArticleText() {
        var result = target.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_ONLY_LOOT2_RC_TEMPLATE)

        assertThat(result.get("loot2_rc"), `is`(SOME_TEXT_ONLY_LOOT2_RC_TEMPLATE))
        assertThat(result.get("loot2"), nullValue())
    }

    @Test
    fun testExtractAllLootPartsOfArticle_BothLoot2AndLoot2RCTemplateInArticleText() {
        var result = target.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_BOTH_LOOT2_AND_LOOT2_RC_TEMPLATE)

        assertThat(result.get("loot2_rc"), notNullValue())
        assertThat(result.get("loot2"), notNullValue())
    }

    @Test
    fun testInsertInfoboxPartOfArticle_Empty() {
        var result = target.insertInfoboxPartOfArticle(SOME_TEXT_EMPTY, "foobar")
        assertThat("Empty result when empty input", result.isEmpty())
    }

    @Test
    fun testInsertInfoboxPartOfArticle_NoInfobox() {
        var result = target.insertInfoboxPartOfArticle(SOME_TEXT_NO_INFOBOX, "foobar")
        assertThat("Empty result when no infobox in input", result.isEmpty())
    }

    @Test
    fun testInsertInfoboxPartOfArticle_OnlyInfoboxInArticleText() {
        var result = target.insertInfoboxPartOfArticle(SOME_TEXT_ONLY_INFOBOX, SOME_TEXT_ONLY_INFOBOX2)
        assertThat(result.orElseThrow(), `is`(SOME_TEXT_ONLY_INFOBOX2))
    }

    @Test
    fun testInsertInfoboxPartOfArticle_WithTextBeforeAndAfter() {
        var result = target.insertInfoboxPartOfArticle(SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER, SOME_TEXT_ONLY_INFOBOX2)
        assertThat(result.orElseThrow(), `is`(SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER2))
    }

    companion object {
        private val SOME_TEXT_ONLY_INFOBOX = """
            {{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Goo Goo Dancer
            }}"""
        private val SOME_TEXT_ONLY_LOOT2_TEMPLATE = """
            {{Loot2
            |version=8.6
            |kills=52807
            |name=Bear
            |Empty, times:24777
            |Meat, times:21065
            |Ham, times:10581
            |Bear Paw, times:1043, amount:1, total:1043
            |Honeycomb, times:250, amount:1, total:249
            }}"""
        private val SOME_TEXT_ONLY_LOOT2_RC_TEMPLATE = """
            {{Loot2_RC
            |version=8.6
            |kills=52807
            |name=Bear
            |Empty, times:24777
            |Meat, times:21065
            |Ham, times:10581
            |Bear Paw, times:1043, amount:1, total:1043
            |Honeycomb, times:250, amount:1, total:249
            }}"""
        private val SOME_TEXT_BOTH_LOOT2_AND_LOOT2_RC_TEMPLATE = """
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
            }}"""
        private val SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER = """
            <noinclude>{{merge|blabla}}</noinclude><!--
            -->{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Goo Goo Dancer
            }}
            [[Category:Achievements Made By Aliens]]"""
        private val SOME_TEXT_ONLY_INFOBOX2 = """
            {{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Goo Goo Dancer
            | points       = 5
            }}"""
        private val SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER2 = """
            <noinclude>{{merge|blabla}}</noinclude><!--
            -->{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
            | name         = Goo Goo Dancer
            | points       = 5
            }}
            [[Category:Achievements Made By Aliens]]"""
        private val SOME_TEXT_WITH_LOOT2_TEMPLATE = """
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
            """
        private val SOME_TEXT_NO_INFOBOX = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. {{}}}}}{"
        private val SOME_TEXT_EMPTY = ""
    }
}
