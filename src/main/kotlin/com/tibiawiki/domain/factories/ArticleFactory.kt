package com.tibiawiki.domain.factories

import com.tibiawiki.domain.utils.TemplateUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.regex.Pattern

/**
 * Conversion from Article to infoboxPartOfArticle.
 */
@Component
class ArticleFactory {

    fun extractInfoboxPartOfArticle(articleContent: String): String {
        return extractInfoboxPartOfArticle(java.util.Map.entry("Unknown", articleContent))
    }

    /**
     * Given a certain Article, extract the part from it which is the infobox, or an empty String if it does not contain
     * an infobox template (which is perfectly valid in some cases).
     */
    fun extractInfoboxPartOfArticle(pageNameAndArticleContent: Map.Entry<String, String>): String {
        val pageName = pageNameAndArticleContent.key
        val articleContent = pageNameAndArticleContent.value

        if (!articleContent.contains(INFOBOX_HEADER)) {
            if (log.isWarnEnabled) {
                log.warn(
                    "Cannot extract infobox template from article '{}'," +
                        " since it contains no Infobox template.",
                    pageName
                )
            }
            return ""
        }

        return TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, INFOBOX_HEADER)
            .orElse("")
    }

    fun extractLootPartOfArticle(pageName: String, articleContent: String): String {
        return extractLootPartOfArticle(java.util.Map.entry(pageName, articleContent))
    }

    fun extractAllLootPartsOfArticle(pageName: String, articleContent: String): Map<String, String> {
        return extractAllLootPartsOfArticle(java.util.Map.entry(pageName, articleContent))
    }

    /**
     * Given a certain Article, extract the part from it which is the first loot statistics template, or an empty String
     * if it does not contain a Loot2 template (which is perfectly valid in some cases).
     */
    fun extractLootPartOfArticle(pageNameAndArticleContent: Map.Entry<String, String>): String {
        val pageName = pageNameAndArticleContent.key
        val articleContent = pageNameAndArticleContent.value

        if (!LOOT2_HEADER_PATTERN.matcher(articleContent).find()) {
            if (log.isWarnEnabled) {
                log.warn(
                    "Cannot extract loot statistics template from article '{}'," +
                        " since it contains no Loot2 template.",
                    pageName
                )
            }
            return ""
        }

        return TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, LOOT2_HEADER)
            .orElse("")
    }

    /**
     * Given a certain Article, extract the parts of all different supported loot statistics templates (Loot2 or Loot2_RC).
     */
    fun extractAllLootPartsOfArticle(pageNameAndArticleContent: Map.Entry<String, String>): Map<String, String> {
        val pageName = pageNameAndArticleContent.key
        val articleContent = pageNameAndArticleContent.value

        if (!LOOT2_HEADER_PATTERN.matcher(articleContent).find() && !LOOT2_RC_HEADER_REGEX.matcher(articleContent).find()) {
            if (log.isWarnEnabled) {
                log.warn(
                    "Cannot extract loot statistics template from article '{}'," +
                        " since it contains no Loot2 or Loot2_RC template.",
                    pageName
                )
            }
            return emptyMap()
        }

        val result = HashMap<String, String>(2)
        val loot2 = if (LOOT2_HEADER_PATTERN.matcher(articleContent).find()) {
            TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, LOOT2_HEADER)
        } else {
            Optional.empty()
        }
        val loot2Rc = if (LOOT2_RC_HEADER_REGEX.matcher(articleContent).find()) {
            TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, LOOT2_RC_HEADER)
        } else {
            Optional.empty()
        }

        loot2.ifPresent { result["loot2"] = it }
        loot2Rc.ifPresent { result["loot2_rc"] = it }

        return result
    }

    /**
     * @param originalArticleContent the original article content with the old infobox content
     * @param newContent the new infobox content
     * @return the full article content with the old infobox content replaced by the new infobox content
     */
    fun insertInfoboxPartOfArticle(originalArticleContent: String, newContent: String): Optional<String> {
        return TemplateUtils.getBeforeAndAfterOuterBalancedBrackets(originalArticleContent, INFOBOX_HEADER)
            .map { it._1() + newContent + it._2() }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ArticleFactory::class.java)
        private const val INFOBOX_HEADER = "{{Infobox"
        private const val LOOT2_HEADER = "{{Loot2"
        private val LOOT2_HEADER_PATTERN: Pattern = Pattern.compile("\\{\\{Loot2\\n")
        private const val LOOT2_RC_HEADER = "{{Loot2_RC"
        private val LOOT2_RC_HEADER_REGEX: Pattern = Pattern.compile("\\{\\{Loot2_RC\\n")
    }
}
