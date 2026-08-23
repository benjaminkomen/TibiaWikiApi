package com.tibiawiki.domain.wiki

import com.tibiawiki.config.WikiClientProperties
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

class WikiResponseCacheTest {

    @Test
    fun categoryAndArticleLoadersRunOnceWhileCached() {
        val cache = WikiResponseCache(WikiClientProperties())
        val categoryLoads = AtomicInteger()
        val articleLoads = AtomicInteger()

        val names = cache.getOrLoadCategory("creatures") {
            categoryLoads.incrementAndGet()
            listOf("Dragon")
        }
        cache.getOrLoadCategory("creatures") {
            categoryLoads.incrementAndGet()
            listOf("should-not-run")
        }
        val text = cache.getOrLoadArticle("Dragon") {
            articleLoads.incrementAndGet()
            "wikitext"
        }
        cache.getOrLoadArticle("Dragon") {
            articleLoads.incrementAndGet()
            "nope"
        }

        assertThat(names, `is`(listOf("Dragon")))
        assertThat(text, `is`("wikitext"))
        assertThat(cache.getArticleIfPresent("Dragon")!!.text, `is`("wikitext"))
        assertThat(categoryLoads.get(), `is`(1))
        assertThat(articleLoads.get(), `is`(1))
    }

    @Test
    fun disabledCacheAlwaysReloads() {
        val cache = WikiResponseCache.disabled()
        val loads = AtomicInteger()
        cache.getOrLoadArticle("Dragon") {
            loads.incrementAndGet()
            "a"
        }
        cache.getOrLoadArticle("Dragon") {
            loads.incrementAndGet()
            "b"
        }
        assertThat(loads.get(), `is`(2))
        assertThat(cache.getArticleIfPresent("Dragon"), `is`(nullValue()))
    }

    @Test
    fun invalidateDropsArticle() {
        val cache = WikiResponseCache(WikiClientProperties())
        cache.putArticle("Dragon", "old")
        cache.invalidateArticle("Dragon")
        assertThat(cache.getArticleIfPresent("Dragon"), `is`(nullValue()))
    }
}
