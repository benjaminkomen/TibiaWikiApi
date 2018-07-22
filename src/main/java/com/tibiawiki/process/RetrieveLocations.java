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

public class RetrieveLocations extends RetrieveAny {

    private static final String CATEGORY_LOCATIONS = "Locations";

    public RetrieveLocations() {
        super();
    }

    public RetrieveLocations(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public Stream<JSONObject> getLocationsJSON() {
        return getLocationsJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getLocationsJSON(boolean oneByOne) {
        final List<String> locationsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LOCATIONS)) {
            locationsCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        final List<String> pagesInLocationsCategoryButNotLists = locationsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return oneByOne
                ? obtainArticlesOneByOne(pagesInLocationsCategoryButNotLists)
                : obtainArticlesInBulk(pagesInLocationsCategoryButNotLists);
    }

    public Optional<JSONObject> getLocationJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
