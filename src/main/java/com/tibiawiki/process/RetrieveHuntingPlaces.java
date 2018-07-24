package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrieveHuntingPlaces extends RetrieveAny {

    private static final String CATEGORY = "Hunting Places";

    public RetrieveHuntingPlaces() {
        super();
    }

    public RetrieveHuntingPlaces(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getHuntingPlacesList() {
        final List<String> huntingPlacesCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY)) {
            huntingPlacesCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        return huntingPlacesCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getHuntingPlacesJSON() {
        return getHuntingPlacesJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getHuntingPlacesJSON(boolean oneByOne) {
        final List<String> huntingPlacesList = getHuntingPlacesList();

        return oneByOne
                ? obtainArticlesOneByOne(huntingPlacesList)
                : obtainArticlesInBulk(huntingPlacesList);
    }

    public Optional<JSONObject> getHuntingPlaceJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
