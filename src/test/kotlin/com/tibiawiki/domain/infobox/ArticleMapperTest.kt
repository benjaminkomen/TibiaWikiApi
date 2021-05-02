package com.tibiawiki.domain.infobox

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ArticleMapperTest {

    @Test
    fun testExtractInfoboxPartOfArticle_EmptyText() {
        assertEquals("", ArticleMapper.extractInfoboxPartOfArticle(SOME_TEXT_EMPTY))
    }

    @Test
    fun testExtractInfoboxPartOfArticle_NoInfobox() {
        assertEquals("", ArticleMapper.extractInfoboxPartOfArticle(SOME_TEXT_NO_INFOBOX))
    }

    @Test
    fun testExtractInfoboxPartOfArticle_OnlyInfoboxInArticleText() {
        assertEquals(SOME_TEXT_ONLY_INFOBOX, ArticleMapper.extractInfoboxPartOfArticle(SOME_TEXT_ONLY_INFOBOX))
    }

    @Test
    fun testExtractLootPartOfArticle_EmptyText() {
        assertEquals("", ArticleMapper.extractLootPartOfArticle("Unknown", SOME_TEXT_EMPTY))
    }

    @Test
    fun testExtractLootPartOfArticle_NoLoot2Template() {
        assertEquals("", ArticleMapper.extractLootPartOfArticle("Unknown", SOME_TEXT_NO_INFOBOX))
    }

    @Test
    fun testExtractLootPartOfArticle_OnlyLoot2TemplateInArticleText() {
        assertEquals(
            "",
            ArticleMapper.extractLootPartOfArticle("Unknown", SOME_TEXT_ONLY_LOOT2_TEMPLATE)
        )
    }

    @Test
    fun testExtractLootPartOfArticle_ALotOfStuffInArticleText() {
        assertEquals(
            SOME_TEXT_ONLY_LOOT2_TEMPLATE,
            ArticleMapper.extractLootPartOfArticle("Unknown", SOME_TEXT_WITH_LOOT2_TEMPLATE)
        )
    }

    @Test
    fun testExtractAllLootPartsOfArticle_EmptyText() {
        assertTrue(ArticleMapper.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_EMPTY).isEmpty())
    }

    @Test
    fun testExtractAllLootPartsOfArticle_NoLoot2OrLoot2RCTemplate() {
        assertTrue(ArticleMapper.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_NO_INFOBOX).isEmpty())
    }

    @Test
    fun testExtractAllLootPartsOfArticle_OnlyLoot2TemplateInArticleText() {
        val result = ArticleMapper.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_ONLY_LOOT2_TEMPLATE)
        assertEquals(SOME_TEXT_ONLY_LOOT2_TEMPLATE, result["loot2"])
        assertNull(result["loot2_rc"])
    }

    @Test
    fun testExtractAllLootPartsOfArticle_OnlyLoot2RCTemplateInArticleText() {
        val result = ArticleMapper.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_ONLY_LOOT2_RC_TEMPLATE)
        assertEquals(SOME_TEXT_ONLY_LOOT2_RC_TEMPLATE, result["loot2_rc"])
        assertNull(result["loot2"])
    }

    @Test
    fun testExtractAllLootPartsOfArticle_BothLoot2AndLoot2RCTemplateInArticleText() {
        val result = ArticleMapper.extractAllLootPartsOfArticle("Unknown", SOME_TEXT_BOTH_LOOT2_AND_LOOT2_RC_TEMPLATE)
        assertNotNull(result["loot2_rc"])
        assertNotNull(result["loot2"])
    }

    @Test
    fun testInsertInfoboxPartOfArticle_Empty() {
        val result = ArticleMapper.insertInfoboxPartOfArticle(SOME_TEXT_EMPTY, "foobar")
        assertTrue(result?.isEmpty() ?: false)
    }

    @Test
    fun testInsertInfoboxPartOfArticle_NoInfobox() {
        val result = ArticleMapper.insertInfoboxPartOfArticle(SOME_TEXT_NO_INFOBOX, "foobar")
        assertTrue(result?.isEmpty() ?: false)
    }

    @Test
    fun testInsertInfoboxPartOfArticle_OnlyInfoboxInArticleText() {
        val result = ArticleMapper.insertInfoboxPartOfArticle(SOME_TEXT_ONLY_INFOBOX, SOME_TEXT_ONLY_INFOBOX2)
        assertEquals(SOME_TEXT_ONLY_INFOBOX2, result)
    }

    @Test
    fun testInsertInfoboxPartOfArticle_WithTextBeforeAndAfter() {
        val result =
            ArticleMapper.insertInfoboxPartOfArticle(SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER, SOME_TEXT_ONLY_INFOBOX2)
        assertEquals(SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER2, result)
    }

    companion object {
        private val SOME_TEXT_ONLY_INFOBOX = """
        {{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
        | name         = Goo Goo Dancer
        }}
        """.trimIndent()
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
        }}
        """.trimIndent()
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
        }}
        """.trimIndent()
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
        }}
        """.trimIndent()
        private val SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER = """
        <noinclude>{{merge|blabla}}</noinclude><!--
        -->{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
        | name         = Goo Goo Dancer
        }}
        [[Category:Achievements Made By Aliens]]
        """.trimIndent()
        private val SOME_TEXT_ONLY_INFOBOX2 = """
        {{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
        | name         = Goo Goo Dancer
        | points       = 5
        }}
        """.trimIndent()
        private val SOME_TEXT_INFOBOX_WITH_BEFORE_AND_AFTER2 = """
        <noinclude>{{merge|blabla}}</noinclude><!--
        -->{{Infobox Achievement|List={{{1|}}}|GetValue={{{GetValue|}}}
        | name         = Goo Goo Dancer
        | points       = 5
        }}
        [[Category:Achievements Made By Aliens]]
        """.trimIndent()
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
        
        """.trimIndent()
        private const val SOME_TEXT_NO_INFOBOX = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. {{}}}}}{"
        private const val SOME_TEXT_EMPTY = ""
    }
}