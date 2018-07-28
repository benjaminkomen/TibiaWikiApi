package com.tibiawiki.domain.repositories;

import fastily.jwiki.core.NS;
import fastily.jwiki.core.Wiki;
import okhttp3.HttpUrl;

import java.util.List;

/**
 * This repository is responsible for obtaining data from the external wiki source. Given a pageName or categoryName
 * it can retrieve one Article or list of Articles from the wiki.
 */
public class ArticleRepository {

    private static final String DEFAULT_WIKI_URI = "https://tibia.wikia.com/api.php";
    private Wiki wiki;

    public ArticleRepository() {
        wiki = new Wiki(null, null, HttpUrl.parse(DEFAULT_WIKI_URI), null, null);
    }

    public List<String> getMembersFromCategory(String categoryName) {
        return wiki.getCategoryMembers(categoryName, NS.MAIN);
    }

    public String getArticle(String pageName) {
        return wiki.getPageText(pageName);
    }

    /**
     * Given a list of pageNames, return a list of Articles in one go, which is supposedly faster and more efficient
     * than {@link #getArticle}. This is limited to 500? articles?
     *
     * @todo I don't think this works like I want to
     */
    public List<String> getArticles(List<String> pageNames) {
        return wiki.allPages("", false, false, -1, NS.MAIN);
    }
}