package com.tibiawiki.domain.factories;

import com.tibiawiki.domain.utils.TemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Conversion from Article to infoboxPartOfArticle.
 */
public class ArticleFactory {

    private static final Logger log = LoggerFactory.getLogger(ArticleFactory.class);
    private static final String INFOBOX_HEADER = "{{Infobox";

    /**
     * Given a certain Article, extract the part from it which is the infobox, or an empty String if it does not contain
     * an infobox template (which is perfectly valid in some cases).
     */
    public String extractInfoboxPartOfArticle(String articleContent) {

        if (!articleContent.contains(INFOBOX_HEADER)) {
            if (log.isWarnEnabled()) {
                int endLength = articleContent.length() >= 200 ? 200 : articleContent.length();
                log.warn("Cannot extract infobox template from article which starts with '{}'," +
                        " since it contains no Infobox template.", articleContent.substring(0, endLength)
                        .replaceAll("\\n", ""));
            }
            return "";
        }

        return TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, INFOBOX_HEADER);
    }

    public Map.Entry<String, String> extractInfoboxPartOfArticle(String pageName, String articleContent) {
        return Map.entry(pageName, extractInfoboxPartOfArticle(articleContent));
    }
}