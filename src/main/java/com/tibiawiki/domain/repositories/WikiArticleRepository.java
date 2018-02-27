package com.tibiawiki.domain.repositories;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.objects.WikiObject;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersSimple;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import java.util.List;
import java.util.stream.Collectors;

public class WikiArticleRepository {

    private MediaWikiBot mediaWikiBot;

    public WikiArticleRepository(MediaWikiBot mediaWikiBot) {
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

    public WikiObject getWikiObject(String pageName) {
        Article article = getArticle(pageName);
        ArticleFactory articleFactory = new ArticleFactory();
        return articleFactory.createWikiObject(article);
    }

    public List<WikiObject> getWikiObjects(List<String> pageNames) {
        return getWikiObjects(pageNames.toArray(new String[0]));
    }

    public List<WikiObject> getWikiObjects(String[] pageNames) {
        return getArticles(pageNames).stream()
                .map(a -> new ArticleFactory().createWikiObject(a))
                .collect(Collectors.toList());
    }
}