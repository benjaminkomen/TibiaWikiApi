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

    private static final String CATEGORY = "Effects";

    public RetrieveEffects() {
        super();
    }

    public RetrieveEffects(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getEffectsList() {
        final List<String> effectsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY)) {
            effectsCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        return effectsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getEffectsJSON() {
        return getEffectsJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getEffectsJSON(boolean oneByOne) {
        final List<String> effectsList = getEffectsList();

        return oneByOne
                ? obtainArticlesOneByOne(effectsList)
                : obtainArticlesInBulk(effectsList);
    }

    public Optional<JSONObject> getEffectJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
