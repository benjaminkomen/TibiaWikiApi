package com.tibiawiki.domain.factories;

import com.tibiawiki.domain.utils.TemplateUtils;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Conversion from Article to infoboxPartOfArticle.
 */
@Component
public class ArticleFactory {

    private static final Logger log = LoggerFactory.getLogger(ArticleFactory.class);
    private static final String INFOBOX_HEADER = "{{Infobox";

    public String extractInfoboxPartOfArticle(String articleContent) {
        return extractInfoboxPartOfArticle(Map.entry("Unknown", articleContent));
    }

    /**
     * Given a certain Article, extract the part from it which is the infobox, or an empty String if it does not contain
     * an infobox template (which is perfectly valid in some cases).
     */
    public String extractInfoboxPartOfArticle(Map.Entry<String, String> pageNameAndArticleContent) {
        final String pageName = pageNameAndArticleContent.getKey();
        final String articleContent = pageNameAndArticleContent.getValue();

        if (!articleContent.contains(INFOBOX_HEADER)) {
            if (log.isWarnEnabled()) {
                log.warn("Cannot extract infobox template from article '{}'," +
                        " since it contains no Infobox template.", pageName);
            }
            return "";
        }

        return TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, INFOBOX_HEADER);
    }

    /**
     * @param originalArticleContent the original article content with the old infobox content
     * @param newContent the new infobox content
     * @return the full article content with the old infobox content replaced by the new infobox content
     */
    public String insertInfoboxPartOfArticle(String originalArticleContent, String newContent) {
        final Tuple2<String, String> beforeAndAfterOuterBalancedBrackets =
                TemplateUtils.getBeforeAndAfterOuterBalancedBrackets(originalArticleContent, INFOBOX_HEADER);

        return beforeAndAfterOuterBalancedBrackets._1() + newContent + beforeAndAfterOuterBalancedBrackets._2();
    }
}