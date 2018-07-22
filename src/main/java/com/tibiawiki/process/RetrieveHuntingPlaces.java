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

public class RetrieveHuntingPlaces {

    private static final String CATEGORY_LISTS = "Lists";
    private static final String CATEGORY_HUNTING_PLACES = "Hunting Places";

    private ArticleRepository articleRepository;
    private ArticleFactory articleFactory;
    private JsonFactory jsonFactory;

    public RetrieveHuntingPlaces() {
        articleRepository = new ArticleRepository(new TibiaWikiBot());
        articleFactory = new ArticleFactory();
        jsonFactory = new JsonFactory();
    }

    public Stream<JSONObject> getHuntingPlacesJSON() {
        return getHuntingPlacesJSON(true);
    }

    public Stream<JSONObject> getHuntingPlacesJSON(boolean oneByOne) {
        final List<String> huntingPlacesCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_HUNTING_PLACES)) {
            huntingPlacesCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        final List<String> pagesInHuntingPlacesCategoryButNotLists = huntingPlacesCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return oneByOne
                ? obtainHuntingPlacesOneByOne(pagesInHuntingPlacesCategoryButNotLists)
                : obtainHuntingPlacesInBulk(pagesInHuntingPlacesCategoryButNotLists);
    }

    public Optional<JSONObject> getHuntingPlaceJSON(String pageName) {
        return Optional.ofNullable(articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }

    private Stream<JSONObject> obtainHuntingPlacesInBulk(List<String> pageNames) {
        return StreamEx.ofSubLists(pageNames, 50)
                .flatMap(names -> articleRepository.getArticles(names).stream())
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }

    private Stream<JSONObject> obtainHuntingPlacesOneByOne(List<String> pageNames) {
        return pageNames.stream()
                .map(pageName -> articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }
}
