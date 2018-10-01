package com.tibiawiki.domain.repositories;

import com.tibiawiki.domain.utils.PropertiesUtil;
import fastily.jwiki.core.MQuery;
import fastily.jwiki.core.NS;
import fastily.jwiki.core.Wiki;
import okhttp3.HttpUrl;

import java.util.List;
import java.util.Map;

/**
 * This repository is responsible for obtaining data from the external wiki source. Given a pageName or categoryName
 * it can retrieve one Article or list of Articles from the wiki.
 */
public class ArticleRepository {

    private static final String DEFAULT_WIKI_URI = "https://tibia.wikia.com/api.php";
    private Wiki wiki;

    public ArticleRepository() {
        wiki = new Wiki(null, null, HttpUrl.parse(DEFAULT_WIKI_URI), null, null);

        // @todo get logging in to work
//        this.login(wiki);
    }

    public ArticleRepository(Wiki wiki) {
        this.wiki = wiki;
    }

    public List<String> getPageNamesFromCategory(String categoryName) {
        return wiki.getCategoryMembers(categoryName, NS.MAIN);
    }

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
        return wiki.getPageText(pageName);
    }

    private boolean login(Wiki wiki) {
        String username = PropertiesUtil.getUsername();
        String password = PropertiesUtil.getPassword();

        if (username != null && password != null) {
            return wiki.login(username, password);
        } else {
            return false;
        }
    }
}