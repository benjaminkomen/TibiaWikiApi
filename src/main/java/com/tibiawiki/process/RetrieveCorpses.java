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

public class RetrieveCorpses extends RetrieveAny {

    private static final String CATEGORY_CORPSES = "Corpses";

    public RetrieveCorpses() {
        super();
    }

    public RetrieveCorpses(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public Stream<JSONObject> getCorpsesJSON() {
        return getCorpsesJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getCorpsesJSON(boolean oneByOne) {
        final List<String> corpsesCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_CORPSES)) {
            corpsesCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        final List<String> pagesInCorpsesCategoryButNotLists = corpsesCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return oneByOne
                ? obtainArticlesOneByOne(pagesInCorpsesCategoryButNotLists)
                : obtainArticlesInBulk(pagesInCorpsesCategoryButNotLists);
    }

    public Optional<JSONObject> getCorpseJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
