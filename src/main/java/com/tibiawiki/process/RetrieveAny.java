package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class RetrieveAny {

    protected static final String CATEGORY_LISTS = "Lists";

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

    public Optional<JSONObject> getArticleAsJSON(String pageName) {
        return Optional.ofNullable(articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }

    public Stream<JSONObject> getArticlesFromInfoboxTemplateAsJSON(String categoryName) {
        return Stream.of(categoryName)
                .flatMap(cat -> articleRepository.getArticlesFromCategory(cat).stream())
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }
}
