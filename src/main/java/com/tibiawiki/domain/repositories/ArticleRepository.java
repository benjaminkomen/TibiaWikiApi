package com.tibiawiki.domain.repositories;

import benjaminkomen.jwiki.core.MQuery;
import benjaminkomen.jwiki.core.NS;
import benjaminkomen.jwiki.core.Wiki;
import com.tibiawiki.domain.utils.PropertiesUtil;
import okhttp3.HttpUrl;
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
public class ArticleRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleRepository.class);
    private static final String DEFAULT_WIKI_URI = "https://tibia.fandom.com/api.php";
    private boolean isDebugEnabled = true;
    private Wiki wiki;

    public ArticleRepository() {
        wiki = new Wiki.Builder().withApiEndpoint(HttpUrl.parse(DEFAULT_WIKI_URI)).build();

        this.login(wiki);
    }

    public ArticleRepository(Wiki wiki) {
        this.wiki = wiki;
    }

    public List<String> getPageNamesFromCategory(String categoryName) {
        return wiki.getCategoryMembers(categoryName, NS.MAIN);
    }

    public List<String> getPageNamesFromCategory(String categoryName, NS namespace) {
        return wiki.getCategoryMembers(categoryName, namespace);
    }

    /**
     * @return a map of key-value pairs of: title - pagecontent
     */
    public Map<String, String> getArticlesFromCategory(List<String> pageNames) {
        return MQuery.getPageText(wiki, pageNames);
    }


    public Map<String, String> getArticlesFromCategory(String categoryName) {
        return MQuery.getPageText(wiki, wiki.getCategoryMembers(categoryName));
    }

    public List<String> getPageNamesUsingTemplate(String templateName) {
        return wiki.whatTranscludesHere(templateName, NS.MAIN);
    }


    public String getArticle(String pageName) {
        final String pageText = wiki.getPageText(pageName);
        return "".equals(pageText)
                ? null
                : pageText;
    }

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