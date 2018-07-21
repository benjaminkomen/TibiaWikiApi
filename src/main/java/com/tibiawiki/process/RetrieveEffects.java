package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.objects.TibiaWikiBot;
import com.tibiawiki.domain.repositories.ArticleRepository;
import one.util.streamex.StreamEx;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrieveEffects {

    private static final String CATEGORY_LISTS = "Lists";
    private static final String CATEGORY_EFFECTS = "Effects";

    private ArticleRepository articleRepository;
    private ArticleFactory articleFactory;
    private JsonFactory jsonFactory;

    public RetrieveEffects() {
        articleRepository = new ArticleRepository(new TibiaWikiBot());
        articleFactory = new ArticleFactory();
        jsonFactory = new JsonFactory();
    }

    public Stream<JSONObject> getEffectsJSON() {
        return getEffectsJSON(true);
    }

    public Stream<JSONObject> getEffectsJSON(boolean oneByOne) {
        final List<String> effectsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_EFFECTS)) {
            effectsCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        final List<String> pagesInEffectsCategoryButNotLists = effectsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return oneByOne
                ? obtainEffectsOneByOne(pagesInEffectsCategoryButNotLists)
                : obtainEffectsInBulk(pagesInEffectsCategoryButNotLists);
    }

    public Optional<JSONObject> getEffectJSON(String pageName) {
        return Stream.of(articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson)
                .findAny();
    }

    private Stream<JSONObject> obtainEffectsInBulk(List<String> pageNames) {
        return StreamEx.ofSubLists(pageNames, 50)
                .flatMap(names -> articleRepository.getArticles(names).stream())
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }

    private Stream<JSONObject> obtainEffectsOneByOne(List<String> pageNames) {
        return pageNames.stream()
                .map(pageName -> articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }
}
