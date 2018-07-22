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

public class RetrieveCreatures extends RetrieveAny {

    private static final String CATEGORY_CREATURES = "Creatures";

    public RetrieveCreatures() {
        super();
    }

    public RetrieveCreatures(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public Stream<JSONObject> getCreaturesJSON() {
        return getCreaturesJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getCreaturesJSON(boolean oneByOne) {
        final List<String> creaturesCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_CREATURES)) {
            creaturesCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        final List<String> pagesInCreaturesCategoryButNotLists = creaturesCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return oneByOne
                ? obtainArticlesOneByOne(pagesInCreaturesCategoryButNotLists)
                : obtainArticlesInBulk(pagesInCreaturesCategoryButNotLists);
    }

    public Optional<JSONObject> getCreatureJson(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
