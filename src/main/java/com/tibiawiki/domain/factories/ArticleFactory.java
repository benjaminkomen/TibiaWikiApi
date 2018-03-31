package com.tibiawiki.domain.factories;

import com.tibiawiki.domain.utils.TemplateUtils;
import net.sourceforge.jwbf.core.contentRep.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Conversion from Article to infoboxPartOfArticle.
 */
public class ArticleFactory {

    private static final Logger log = LoggerFactory.getLogger(ArticleFactory.class);
    private static final String INFOBOX_HEADER = "{{Infobox";

    /**
     * Given a certain Article, extract the part from it which is the infobox.
     */
    public String extractInfoboxPartOfArticle(Article article) {
        final String articleContent = article.getText();

        if (!articleContent.contains(INFOBOX_HEADER)) {
            log.error("Cannot extract infobox template from article with title '{}'," +
                    " since it contains no Infobox template.", article.getTitle());
            return null;
        }

        return TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, INFOBOX_HEADER);
    }
}