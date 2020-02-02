package com.tibiawiki.domain.factories;

import com.tibiawiki.domain.utils.TemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Conversion from Article to infoboxPartOfArticle.
 */
@Component
public class ArticleFactory {

    private static final Logger log = LoggerFactory.getLogger(ArticleFactory.class);
    private static final String INFOBOX_HEADER = "{{Infobox";
    private static final String LOOT2_HEADER = "{{Loot2";
    private static final Pattern LOOT2_HEADER_PATTERN = Pattern.compile("\\{\\{Loot2\\n");
    private static final String LOOT2_RC_HEADER = "{{Loot2_RC";
    private static final Pattern LOOT2_RC_HEADER_REGEX = Pattern.compile("\\{\\{Loot2_RC\\n");

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

        return TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, INFOBOX_HEADER)
                .orElse("");
    }

    public String extractLootPartOfArticle(String pageName, String articleContent) {
        return extractLootPartOfArticle(Map.entry(pageName, articleContent));
    }

    public Map<String, String> extractAllLootPartsOfArticle(String pageName, String articleContent) {
        return extractAllLootPartsOfArticle(Map.entry(pageName, articleContent));
    }

    /**
     * Given a certain Article, extract the part from it which is the first loot statistics template, or an empty String if it does not contain
     * an Loot2 template (which is perfectly valid in some cases).
     */
    public String extractLootPartOfArticle(Map.Entry<String, String> pageNameAndArticleContent) {
        final String pageName = pageNameAndArticleContent.getKey();
        final String articleContent = pageNameAndArticleContent.getValue();

        if (!LOOT2_HEADER_PATTERN.matcher(articleContent).find()) {
            if (log.isWarnEnabled()) {
                log.warn("Cannot extract loot statistics template from article '{}'," +
                        " since it contains no Loot2 template.", pageName);
            }
            return "";
        }

        return TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, LOOT2_HEADER)
                .orElse("");
    }

    /**
     * Given a certain Article, extract the parts of all different supported loot statistics templates (Loot2 or Loot2_RC).
     */
    public Map<String, String> extractAllLootPartsOfArticle(Map.Entry<String, String> pageNameAndArticleContent) {
        final String pageName = pageNameAndArticleContent.getKey();
        final String articleContent = pageNameAndArticleContent.getValue();

        if (!LOOT2_HEADER_PATTERN.matcher(articleContent).find() && !LOOT2_RC_HEADER_REGEX.matcher(articleContent).find()) {
            if (log.isWarnEnabled()) {
                log.warn("Cannot extract loot statistics template from article '{}'," +
                        " since it contains no Loot2 or Loot2_RC template.", pageName);
            }
            return Collections.emptyMap();
        }

        var result = new HashMap<String, String>(2);
        var loot2 = LOOT2_HEADER_PATTERN.matcher(articleContent).find()
                ? TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, LOOT2_HEADER)
                : Optional.empty();
        var loot2Rc = LOOT2_RC_HEADER_REGEX.matcher(articleContent).find()
                ? TemplateUtils.getBetweenOuterBalancedBrackets(articleContent, LOOT2_RC_HEADER)
                : Optional.empty();

        loot2.ifPresent(s -> result.put("loot2", (String) s));
        loot2Rc.ifPresent(s -> result.put("loot2_rc", (String) s));

        return result;
    }

    /**
     * @param originalArticleContent the original article content with the old infobox content
     * @param newContent             the new infobox content
     * @return the full article content with the old infobox content replaced by the new infobox content
     */
    public Optional<String> insertInfoboxPartOfArticle(String originalArticleContent, String newContent) {
        return TemplateUtils.getBeforeAndAfterOuterBalancedBrackets(originalArticleContent, INFOBOX_HEADER)
                .map(beforeAndAfterOuterBalancedBrackets -> beforeAndAfterOuterBalancedBrackets._1() + newContent + beforeAndAfterOuterBalancedBrackets._2());
    }
}