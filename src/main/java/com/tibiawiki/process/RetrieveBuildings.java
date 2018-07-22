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

public class RetrieveBuildings extends RetrieveAny {

    private static final String CATEGORY_BUILDINGS = "Buildings";

    public RetrieveBuildings() {
        super();
    }

    public RetrieveBuildings(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public Stream<JSONObject> getBuildingsJSON() {
        return getBuildingsJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getBuildingsJSON(boolean oneByOne) {
        final List<String> buildingsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_BUILDINGS)) {
            buildingsCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        final List<String> pagesInBuildingsCategoryButNotLists = buildingsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return oneByOne
                ? obtainArticlesOneByOne(pagesInBuildingsCategoryButNotLists)
                : obtainArticlesInBulk(pagesInBuildingsCategoryButNotLists);
    }

    public Optional<JSONObject> getBuildingJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
