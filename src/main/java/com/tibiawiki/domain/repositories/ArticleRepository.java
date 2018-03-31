package com.tibiawiki.domain.repositories;

import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersSimple;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This repository is responsible for obtaining data from the external wiki source. Given a pageName or categoryName
 * it can retrieve one Article or list of Articles from the wiki.
 */
public class ArticleRepository {

    private MediaWikiBot mediaWikiBot;

    public ArticleRepository(MediaWikiBot mediaWikiBot) {
        this.mediaWikiBot = mediaWikiBot;
    }

    public CategoryMembersSimple getMembersFromCategory(String categoryName) {
        return new CategoryMembersSimple(mediaWikiBot, categoryName);
    }

    public Article getArticle(String pageName) {
        return mediaWikiBot.getArticle(pageName);
    }

    public List<Article> getArticles(List<String> pageNames) {
        return getArticles(pageNames.toArray(new String[0]));
    }

    /**
     * Given a list of pageNames, return a list of Articles in one go, which is supposedly faster and more efficient
     * than {@link #getArticle}. This is limited to 500? articles?
     */
    public List<Article> getArticles(String[] pageNames) {
        return mediaWikiBot.readData(pageNames).stream()
                .map(sa -> Article.withoutReload(sa, mediaWikiBot))
                .collect(Collectors.toList());
    }
}