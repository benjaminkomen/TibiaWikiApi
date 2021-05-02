package com.tibiawiki.adapters.mediawiki

import com.tibiawiki.domain.mediawiki.ArticleRepository
import com.tibiawiki.domain.utils.PropertiesUtil
import org.fastily.jwiki.core.MQuery
import org.fastily.jwiki.core.NS
import org.fastily.jwiki.core.Wiki
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 * This repository is responsible for obtaining data from the external wiki source. Given a pageName or categoryName
 * it can retrieve one Article or list of Articles from the wiki.
 */
@Repository
class JwikiArticleRepository(
    private val wiki: Wiki
) : ArticleRepository {

    init {
        login(wiki)
    }

    override fun getPageNamesFromCategory(categoryName: String): List<String> {
        return wiki.getCategoryMembers(categoryName, NS.MAIN)
    }

    override fun getPageNamesFromCategory(categoryName: String, namespace: NS): List<String> {
        return wiki.getCategoryMembers(categoryName, namespace)
    }

    /**
     * @return a map of key-value pairs of: title - pagecontent
     */
    override fun getArticlesFromCategory(pageNames: List<String>): Map<String, String> {
        return MQuery.getPageText(wiki, pageNames)
    }

    override fun getArticlesFromCategory(categoryName: String): Map<String, String> {
        return MQuery.getPageText(wiki, wiki.getCategoryMembers(categoryName))
    }

    override fun getPageNamesUsingTemplate(templateName: String): List<String> {
        return wiki.whatTranscludesHere(templateName, NS.MAIN)
    }

    override fun getArticle(pageName: String): String? {
        val pageText = wiki.getPageText(pageName)
        return if ("" == pageText) null else pageText
    }

    override fun modifyArticle(pageName: String, pageContent: String, editSummary: String): Boolean {
        logger.info("Attempting to publish page {} with new content {}.", pageName, pageContent)
        return if (isDebugEnabled) {
            true
        } else {
            wiki.edit(pageName, pageContent, editSummary)
        }
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

    companion object {
        private val logger = LoggerFactory.getLogger(JwikiArticleRepository::class.java)
        const val DEFAULT_WIKI_URI = "https://tibia.fandom.com/api.php"
        private var isDebugEnabled = true
    }
}
