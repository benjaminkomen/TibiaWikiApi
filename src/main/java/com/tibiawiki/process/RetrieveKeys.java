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

public class RetrieveKeys extends RetrieveAny {

    private static final String CATEGORY_KEYS = "Keys";

    public RetrieveKeys() {
        super();
    }

    public RetrieveKeys(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getKeysList() {
        final List<String> keysCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_KEYS)) {
            keysCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        return keysCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getKeysJSON() {
        return getKeysJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getKeysJSON(boolean oneByOne) {
        final List<String> keysList = getKeysList();

        return oneByOne
                ? obtainArticlesOneByOne(keysList)
                : obtainArticlesInBulk(keysList);
    }

    public Optional<JSONObject> getKeyJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
