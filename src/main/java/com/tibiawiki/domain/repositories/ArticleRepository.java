package com.tibiawiki.domain.repositories;

import benjaminkomen.jwiki.core.MQuery;
import benjaminkomen.jwiki.core.NS;
import benjaminkomen.jwiki.core.Wiki;
import com.tibiawiki.domain.utils.PropertiesUtil;
import okhttp3.HttpUrl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * This repository is responsible for obtaining data from the external wiki source. Given a pageName or categoryName
 * it can retrieve one Article or list of Articles from the wiki.
 */
@Repository
public class ArticleRepository {

    private static final String DEFAULT_WIKI_URI = "https://tibia.wikia.com/api.php";
    private Wiki wiki;

    public ArticleRepository() {
        wiki = new Wiki(null, null, HttpUrl.parse(DEFAULT_WIKI_URI), null, null, true);

        this.login(wiki);
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