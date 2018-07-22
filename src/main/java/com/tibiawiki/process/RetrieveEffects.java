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

public class RetrieveEffects extends RetrieveAny {

    private static final String CATEGORY_EFFECTS = "Effects";

    public RetrieveEffects() {
        super();
    }

    public RetrieveEffects(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public Stream<JSONObject> getEffectsJSON() {
        return getEffectsJSON(ONE_BY_ONE);
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
                ? obtainArticlesOneByOne(pagesInEffectsCategoryButNotLists)
                : obtainArticlesInBulk(pagesInEffectsCategoryButNotLists);
    }

    public Optional<JSONObject> getEffectJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
