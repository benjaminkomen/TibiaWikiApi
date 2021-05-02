package com.tibiawiki.domain.infobox

import org.slf4j.LoggerFactory
import java.util.HashMap
import java.util.regex.Pattern

/**
 * Mapping from Article to infoboxPartOfArticle.
 */
object ArticleMapper {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private const val INFOBOX_HEADER = "{{Infobox"
    private const val LOOT2_HEADER = "{{Loot2"
    private val LOOT2_HEADER_PATTERN = Pattern.compile("""\{\{Loot2\n""")
    private const val LOOT2_RC_HEADER = "{{Loot2_RC"
    private val LOOT2_RC_HEADER_REGEX = Pattern.compile("""\{\{Loot2_RC\n""")

    fun extractInfoboxPartOfArticle(articleContent: String): String {
        return extractInfoboxPartOfArticle(Pair("Unknown", articleContent))
    }

    /**
     * Given a certain Article, extract the part from it which is the infobox, or an empty String if it does not contain
     * an infobox template (which is perfectly valid in some cases).
     */
    fun extractInfoboxPartOfArticle(pageNameAndArticleContent: Pair<String, String>): String {
        val (pageName, articleContent) = pageNameAndArticleContent
        if (articleContent.contains(INFOBOX_HEADER).not()) {
            logger.warn(
                "Cannot extract infobox template from article '$pageName'," +
                        " since it contains no Infobox template."
            )
            return ""
        }
        return TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, INFOBOX_HEADER) ?: ""
    }

    fun extractLootPartOfArticle(pageName: String, articleContent: String): String {
        return extractLootPartOfArticle(Pair(pageName, articleContent))
    }

    fun extractAllLootPartsOfArticle(pageName: String, articleContent: String): Map<String, String> {
        return extractAllLootPartsOfArticle(Pair(pageName, articleContent))
    }

    /**
     * Given a certain Article, extract the part from it which is the first loot statistics template, or an empty String if it does not contain
     * an Loot2 template (which is perfectly valid in some cases).
     */
    fun extractLootPartOfArticle(pageNameAndArticleContent: Pair<String, String>): String {
        val (pageName, articleContent) = pageNameAndArticleContent
        if (!LOOT2_HEADER_PATTERN.matcher(articleContent).find()) {
            logger.warn(
                "Cannot extract loot statistics template from article '$pageName'," +
                        " since it contains no Loot2 template."
            )
            return ""
        }
        return TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, LOOT2_HEADER) ?: ""
    }

    /**
     * Given a certain Article, extract the parts of all different supported loot statistics templates (Loot2 or Loot2_RC).
     */
    fun extractAllLootPartsOfArticle(pageNameAndArticleContent: Pair<String, String>): Map<String, String> {
        val (pageName, articleContent) = pageNameAndArticleContent
        if (LOOT2_HEADER_PATTERN.matcher(articleContent).find().not() &&
            LOOT2_RC_HEADER_REGEX.matcher(articleContent).find().not()
        ) {
            logger.warn(
                "Cannot extract loot statistics template from article '$pageName'," +
                        " since it contains no Loot2 or Loot2_RC template."
            )
            return emptyMap()
        }
        val result = HashMap<String, String>(2)
        val loot2: String? = if (LOOT2_HEADER_PATTERN.matcher(articleContent).find()) {
            TemplateUtils.getBetweenOuterBalancedBrackets(
                articleContent,
                LOOT2_HEADER
            )
        } else {
            null
        }
        val loot2Rc = if (LOOT2_RC_HEADER_REGEX.matcher(articleContent).find()) {
            TemplateUtils.getBetweenOuterBalancedBrackets(
                articleContent,
                LOOT2_RC_HEADER
            )
        } else {
            null
        }
        loot2.orEmpty().let { result["loot2"] = it }
        loot2Rc.orEmpty().let { result["loot2_rc"] = it }
        return result
    }

    /**
     * @param originalArticleContent the original article content with the old infobox content
     * @param newContent             the new infobox content
     * @return the full article content with the old infobox content replaced by the new infobox content
     */
    fun insertInfoboxPartOfArticle(originalArticleContent: String, newContent: String): String? {
        return TemplateUtils.getBeforeAndAfterOuterBalancedBrackets(originalArticleContent, INFOBOX_HEADER)
            ?.let { (before, after) -> before + newContent + after }
    }
}
