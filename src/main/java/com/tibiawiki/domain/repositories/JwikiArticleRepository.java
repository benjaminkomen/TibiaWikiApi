package com.tibiawiki.domain.repositories;

import com.tibiawiki.domain.utils.PropertiesUtil;
import okhttp3.HttpUrl;
import io.github.fastily.jwiki.core.MQuery;
import io.github.fastily.jwiki.core.NS;
import io.github.fastily.jwiki.core.Wiki;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * This repository is responsible for obtaining data from the external wiki source. Given a pageName or categoryName
 * it can retrieve one Article or list of Articles from the wiki.
 */
@Repository
public class JwikiArticleRepository implements ArticleRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JwikiArticleRepository.class);
    private static final String DEFAULT_WIKI_URI = "https://tibia.fandom.com/api.php";
    private boolean isDebugEnabled = false;
    private final Wiki wiki;

    public JwikiArticleRepository() {
        wiki = new Wiki.Builder().withApiEndpoint(HttpUrl.parse(DEFAULT_WIKI_URI)).build();

        this.login(wiki);
    }

    public JwikiArticleRepository(Wiki wiki) {
        this.wiki = wiki;
    }

    @Override
    public List<String> getPageNamesFromCategory(String categoryName) {
        return wiki.getCategoryMembers(categoryName, NS.MAIN);
    }

    @Override
    public List<String> getPageNamesFromCategory(String categoryName, NS namespace) {
        return wiki.getCategoryMembers(categoryName, namespace);
    }

    /**
     * @return a map of key-value pairs of: title - pagecontent
     */
    @Override
    public Map<String, String> getArticlesFromCategory(List<String> pageNames) {
        return MQuery.getPageText(wiki, pageNames);
    }

    @Override
    public Map<String, String> getArticlesFromCategory(String categoryName) {
        return MQuery.getPageText(wiki, wiki.getCategoryMembers(categoryName));
    }

    @Override
    public List<String> getPageNamesUsingTemplate(String templateName) {
        return wiki.whatTranscludesHere(templateName, NS.MAIN);
    }

    @Override
    public String getArticle(String pageName) {
        final String pageText = wiki.getPageText(pageName);
        return "".equals(pageText)
                ? null
                : pageText;
    }

    @Override
    public boolean modifyArticle(String pageName, String pageContent, String editSummary) {
        LOG.info("Attempting to publish page {} with new content {}.", pageName, pageContent);

        return isDebugEnabled
                ? true
                : wiki.edit(pageName, pageContent, editSummary);
    }

    protected void enableDebug() {
        this.isDebugEnabled = true;
    }

    protected void disableDebug() {
        this.isDebugEnabled = false;
    }

    protected boolean login(Wiki wiki) {
        String username = PropertiesUtil.getUsername();
        String password = PropertiesUtil.getPassword();

        if (username != null && password != null) {
            return wiki.login(username, password);
        } else {
            return false;
        }
    }
}