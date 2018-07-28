package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import one.util.streamex.StreamEx;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class RetrieveAny {

    protected static final String CATEGORY_LISTS = "Lists";
    protected static final Boolean ONE_BY_ONE = true;

    protected ArticleRepository articleRepository;
    protected ArticleFactory articleFactory;
    protected JsonFactory jsonFactory;

    public RetrieveAny() {
        articleRepository = new ArticleRepository();
        articleFactory = new ArticleFactory();
        jsonFactory = new JsonFactory();
    }

    public RetrieveAny(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        this.articleRepository = articleRepository;
        this.articleFactory = articleFactory;
        this.jsonFactory = jsonFactory;
    }

    public Optional<JSONObject> getArticleJSON(String pageName) {
        return Optional.ofNullable(articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }

    protected Stream<JSONObject> obtainArticlesInBulk(List<String> pageNames) {
        return StreamEx.ofSubLists(pageNames, 50)
                .flatMap(names -> articleRepository.getArticles(names).stream())
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }

    protected Stream<JSONObject> obtainArticlesOneByOne(List<String> pageNames) {
        return pageNames.stream()
                .map(pageName -> articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }
}
