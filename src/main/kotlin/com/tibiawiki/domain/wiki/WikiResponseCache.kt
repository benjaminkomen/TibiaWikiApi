package com.tibiawiki.domain.wiki

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.tibiawiki.config.WikiClientProperties
import java.time.Duration
import java.util.concurrent.ExecutionException

/**
 * Short-TTL in-process cache for category member lists and page wikitext.
 * Shared by list endpoints and `?expand=true` so repeat traffic does not
 * stampede Fandom.
 */
class WikiResponseCache(
    properties: WikiClientProperties,
    private val enabled: Boolean = true
) {
    private val categoryCache: Cache<String, List<String>>? = buildCache(
        enabled,
        properties.cache.maxCategoryEntries,
        properties.cache.ttl
    )
    private val articleCache: Cache<String, CachedWikitext>? = buildCache(
        enabled,
        properties.cache.maxArticleEntries,
        properties.cache.ttl
    )

    fun getOrLoadCategory(key: String, loader: () -> List<String>): List<String> {
        val cache = categoryCache
        if (cache == null) {
            return loader()
        }
        return unwrap { cache.get(key) { loader().toList() } }.toList()
    }

    fun getArticleIfPresent(pageName: String): CachedWikitext? {
        return articleCache?.getIfPresent(pageName)
    }

    fun getOrLoadArticle(pageName: String, loader: () -> String?): String? {
        val cache = articleCache
        if (cache == null) {
            return loader()
        }
        return unwrap { cache.get(pageName) { CachedWikitext(loader()) } }.text
    }

    fun putArticle(pageName: String, text: String?) {
        articleCache?.put(pageName, CachedWikitext(text))
    }

    fun invalidateArticle(pageName: String) {
        articleCache?.invalidate(pageName)
    }

    class CachedWikitext(val text: String?)

    companion object {
        fun disabled(): WikiResponseCache {
            return WikiResponseCache(WikiClientProperties(), enabled = false)
        }

        private fun <K : Any, V : Any> buildCache(
            enabled: Boolean,
            maxSize: Long,
            ttl: Duration
        ): Cache<K, V>? {
            if (!enabled || maxSize <= 0 || ttl.isZero || ttl.isNegative) {
                return null
            }
            return CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(ttl)
                .build()
        }

        private fun <T> unwrap(loader: () -> T): T {
            return try {
                loader()
            } catch (e: ExecutionException) {
                val cause = e.cause
                if (cause is RuntimeException) {
                    throw cause
                }
                throw WikiUnavailableException("cache load failed", e)
            }
        }
    }
}
