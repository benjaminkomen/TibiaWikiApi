package com.tibiawiki.domain.repositories

import com.tibiawiki.config.WikiClientProperties
import com.tibiawiki.domain.objects.WikiNamespace
import com.tibiawiki.domain.utils.PropertiesUtil
import com.tibiawiki.domain.wiki.ExpandConcurrencyLimiter
import com.tibiawiki.domain.wiki.ExpandTooLargeException
import com.tibiawiki.domain.wiki.WikiCallSupport
import com.tibiawiki.domain.wiki.WikiFactory
import com.tibiawiki.domain.wiki.WikiResponseCache
import io.github.fastily.jwiki.core.MQuery
import io.github.fastily.jwiki.core.NS
import io.github.fastily.jwiki.core.Wiki
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

/**
 * Live Fandom-backed [ArticleRepository]. Wiki construction, HTTP, and login
 * happen on first use (not in this constructor) so a Fandom outage degrades
 * reads instead of failing process start.
 *
 * The [Wiki] convenience constructor is for tests. It must not be the only
 * Spring candidate: with two constructors and no [@Autowired], Boot 4 falls
 * back to a missing no-arg constructor and the process exits.
 */
@Repository
@Profile("!fixtures")
class JwikiArticleRepository @Autowired constructor(
    private val properties: WikiClientProperties,
    private val wikiFactory: WikiFactory,
    private val cache: WikiResponseCache,
    private val calls: WikiCallSupport
) : ArticleRepository {

    private val expandLimiter = ExpandConcurrencyLimiter(
        properties.expand.maxConcurrent,
        properties.expand.acquireTimeout
    )

    private var isDebugEnabled = false

    constructor(wiki: Wiki) : this(
        WikiClientProperties(),
        WikiFactory.fixed(wiki),
        WikiResponseCache.disabled(),
        WikiCallSupport.direct()
    )

    override fun getPageNamesFromCategory(categoryName: String): List<String> {
        return getPageNamesFromCategory(categoryName, WikiNamespace.MAIN)
    }

    override fun getPageNamesFromCategory(categoryName: String, namespace: WikiNamespace): List<String> {
        val resolved = JwikiNamespaceResolver.resolve(wiki(), namespace)
        val key = categoryKey(categoryName, resolved.v)
        return cache.getOrLoadCategory(key) {
            calls.call("getCategoryMembers") {
                wiki().getCategoryMembers(categoryName, resolved)
            }
        }
    }

    /**
     * @return a map of key-value pairs of: title - pagecontent
     */
    override fun getArticlesFromCategory(pageNames: List<String>): Map<String, String> {
        if (pageNames.size > properties.expand.maxPages) {
            throw ExpandTooLargeException(pageNames.size, properties.expand.maxPages)
        }
        val result = LinkedHashMap<String, String>()
        val missing = ArrayList<String>()
        for (pageName in pageNames) {
            val cached = cache.getArticleIfPresent(pageName)
            if (cached != null) {
                cached.text?.let { result[pageName] = it }
            } else {
                missing.add(pageName)
            }
        }
        if (missing.isNotEmpty()) {
            expandLimiter.withPermit {
                val fetched = calls.call("getArticles") {
                    MQuery.getPageText(wiki(), missing)
                }
                for (pageName in missing) {
                    val text = fetched[pageName]?.takeIf { it.isNotEmpty() }
                    cache.putArticle(pageName, text)
                    if (text != null) {
                        result[pageName] = text
                    }
                }
            }
        }
        return result
    }

    override fun getArticlesFromCategory(categoryName: String): Map<String, String> {
        val key = categoryKey(categoryName, ALL_NAMESPACES)
        val names = cache.getOrLoadCategory(key) {
            calls.call("getCategoryMembers") {
                wiki().getCategoryMembers(categoryName)
            }
        }
        return getArticlesFromCategory(names)
    }

    override fun getPageNamesUsingTemplate(templateName: String): List<String> {
        val key = "template:$templateName"
        return cache.getOrLoadCategory(key) {
            calls.call("whatTranscludesHere") {
                wiki().whatTranscludesHere(templateName, NS.MAIN)
            }
        }
    }

    override fun getArticle(pageName: String): String? {
        return cache.getOrLoadArticle(pageName) {
            calls.call("getArticle") {
                wiki().getPageText(pageName).takeIf { it.isNotEmpty() }
            }
        }
    }

    override fun modifyArticle(pageName: String, pageContent: String, editSummary: String?): Boolean {
        LOG.info("Attempting to publish page {} ({} characters).", pageName, pageContent.length)
        val edited = if (isDebugEnabled) {
            true
        } else {
            calls.call("modifyArticle") {
                wiki().edit(pageName, pageContent, editSummary)
            }
        }
        if (edited) {
            cache.invalidateArticle(pageName)
        }
        return edited
    }

    fun enableDebug() {
        isDebugEnabled = true
    }

    fun disableDebug() {
        isDebugEnabled = false
    }

    fun login(wiki: Wiki): Boolean {
        val username = PropertiesUtil.getUsername()
        val password = PropertiesUtil.getPassword()
        return if (username != null && password != null) {
            wiki.login(username, password)
        } else {
            false
        }
    }

    private fun wiki(): Wiki = wikiFactory.get()

    companion object {
        private val LOG = LoggerFactory.getLogger(JwikiArticleRepository::class.java)
        private const val ALL_NAMESPACES = Int.MIN_VALUE

        private fun categoryKey(categoryName: String, namespace: Int): String {
            return "category:$categoryName#$namespace"
        }
    }
}
