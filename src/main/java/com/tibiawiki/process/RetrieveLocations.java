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

public class RetrieveLocations {

    private static final String CATEGORY_LISTS = "Lists";
    private static final String CATEGORY_LOCATIONS = "Locations";

    private ArticleRepository articleRepository;
    private ArticleFactory articleFactory;
    private JsonFactory jsonFactory;

    public RetrieveLocations() {
        articleRepository = new ArticleRepository(new TibiaWikiBot());
        articleFactory = new ArticleFactory();
        jsonFactory = new JsonFactory();
    }

    public Stream<JSONObject> getLocationsJSON() {
        return getLocationsJSON(true);
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
                ? obtainLocationsOneByOne(pagesInLocationsCategoryButNotLists)
                : obtainLocationsInBulk(pagesInLocationsCategoryButNotLists);
    }

    public Optional<JSONObject> getLocationJSON(String pageName) {
        return Optional.ofNullable(articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }

    private Stream<JSONObject> obtainLocationsInBulk(List<String> pageNames) {
        return StreamEx.ofSubLists(pageNames, 50)
                .flatMap(names -> articleRepository.getArticles(names).stream())
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }

    private Stream<JSONObject> obtainLocationsOneByOne(List<String> pageNames) {
        return pageNames.stream()
                .map(pageName -> articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }
}
