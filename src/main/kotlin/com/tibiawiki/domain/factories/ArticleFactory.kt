package com.tibiawiki.domain.factories

import com.tibiawiki.domain.utils.TemplateUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.Optional

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

        val loot2 = extractExactLootTemplate(articleContent, LOOT2_HEADER)
        if (loot2.isEmpty) {
            if (log.isWarnEnabled) {
                log.warn(
                    "Cannot extract loot statistics template from article '{}'," +
                        " since it contains no Loot2 template.",
                    pageName
                )
            }
            return ""
        }

        return loot2.orElseThrow()
    }

    /**
     * Given a certain Article, extract the parts of all different supported loot statistics templates (Loot2 or Loot2_RC).
     */
    fun extractAllLootPartsOfArticle(pageNameAndArticleContent: Map.Entry<String, String>): Map<String, String> {
        val pageName = pageNameAndArticleContent.key
        val articleContent = pageNameAndArticleContent.value

        val loot2 = extractExactLootTemplate(articleContent, LOOT2_HEADER)
        val loot2Rc = extractExactLootTemplate(articleContent, LOOT2_RC_HEADER)
        if (loot2.isEmpty && loot2Rc.isEmpty) {
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

    /**
     * Locate `header` as a full template name. `{{Loot2` must not match `{{Loot2_RC`.
     */
    private fun extractExactLootTemplate(articleContent: String, header: String): Optional<String> {
        val startIndex = indexOfExactTemplate(articleContent, header)
        if (startIndex < 0) {
            return Optional.empty()
        }
        return TemplateUtils.getBetweenOuterBalancedBrackets(articleContent.substring(startIndex), header)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ArticleFactory::class.java)
        private const val INFOBOX_HEADER = "{{Infobox"
        private const val LOOT2_HEADER = "{{Loot2"
        private const val LOOT2_RC_HEADER = "{{Loot2_RC"

        private fun indexOfExactTemplate(text: String, header: String): Int {
            var fromIndex = 0
            while (fromIndex < text.length) {
                val index = text.indexOf(header, fromIndex)
                if (index < 0) {
                    return -1
                }
                val afterHeader = index + header.length
                if (afterHeader >= text.length || !isTemplateNameContinuation(text[afterHeader])) {
                    return index
                }
                fromIndex = afterHeader
            }
            return -1
        }

        private fun isTemplateNameContinuation(character: Char): Boolean {
            return character.isLetterOrDigit() || character == '_'
        }
    }
}
