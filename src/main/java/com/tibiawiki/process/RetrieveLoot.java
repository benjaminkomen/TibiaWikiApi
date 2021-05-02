package com.tibiawiki.process;

import com.tibiawiki.domain.mediawiki.ArticleRepository;
import com.tibiawiki.domain.factories.JsonFactory;
import org.fastily.jwiki.core.NS;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class RetrieveLoot extends RetrieveAny {

    private static final String LOOT_STATISTICS_CATEGORY_NAME = "Loot Statistics";

    @Autowired
    public RetrieveLoot(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getLootList() {
        final List<String> lootStatisticsCategory = articleRepository.getPageNamesFromCategory(LOOT_STATISTICS_CATEGORY_NAME, makeLootNamespace(112));

        return new ArrayList<>(lootStatisticsCategory);
    }

    public Stream<JSONObject> getLootJSONObject() {
        return getArticlesFromLoot2TemplateAsJSONObject(getLootList());
    }

    public Optional<JSONObject> getLootJSONObject(String pageName) {
        return getLootArticleAsJSON(pageName);
    }

    public Stream<JSONObject> getAllLootPartsJSON() {
        return getArticlesFromAllLootTemplatesAsJSON(getLootList());
    }

    public Optional<JSONObject> getAllLootPartsJSON(String pageName) {
        return getAllLootPartsAsJSON(pageName);
    }

    private Stream<JSONObject> getArticlesFromLoot2TemplateAsJSONObject(List<String> pageNames) {
        return Stream.of(pageNames)
                .flatMap(lst -> articleRepository.getArticlesFromCategory(lst).entrySet().stream())
                .map(e -> {
                    var lootPartOfArticle = articleFactory.extractLootPartOfArticle(e);
                    return jsonFactory.convertLootPartOfArticleToJson(e.getKey(), lootPartOfArticle);
                });
    }

    private Stream<JSONObject> getArticlesFromAllLootTemplatesAsJSON(List<String> pageNames) {
        return Stream.of(pageNames)
                .flatMap(lst -> articleRepository.getArticlesFromCategory(lst).entrySet().stream())
                .map(e -> {
                    var lootPartOfArticle = articleFactory.extractAllLootPartsOfArticle(e);
                    return jsonFactory.convertAllLootPartsOfArticleToJson(e.getKey(), lootPartOfArticle);
                });
    }

    private Optional<JSONObject> getLootArticleAsJSON(String pageName) {
        return Optional.ofNullable(articleRepository.getArticle(pageName))
                .map(articleContent -> articleFactory.extractLootPartOfArticle(pageName, articleContent))
                .map(lootPartOfArticle -> jsonFactory.convertLootPartOfArticleToJson(pageName, lootPartOfArticle));
    }

    private Optional<JSONObject> getAllLootPartsAsJSON(String pageName) {
        return Optional.ofNullable(articleRepository.getArticle(pageName))
                .map(articleContent -> articleFactory.extractAllLootPartsOfArticle(pageName, articleContent))
                .map(lootPartsOfArticle -> jsonFactory.convertAllLootPartsOfArticleToJson(pageName, lootPartsOfArticle));
    }

    // TODO replace this reflection hack with functionality in jwiki to construct a custom namespace
    public static NS makeLootNamespace(int namespaceInput) {
        try {
            final Constructor<?>[] constructors = NS.class.getDeclaredConstructors();
            constructors[0].setAccessible(true);
            Object namespace = constructors[0].newInstance(namespaceInput);
            return (NS) namespace;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new NamespaceReflectionException(e);
        }
    }

    public static class NamespaceReflectionException extends RuntimeException {
        public NamespaceReflectionException(Throwable e) {
            super(e);
        }
    }
}
