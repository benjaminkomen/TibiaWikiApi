package com.tibiawiki.process;

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

    public Stream<JSONObject> getKeysJSON() {
        return getKeysJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getKeysJSON(boolean oneByOne) {
        final List<String> keysCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_KEYS)) {
            keysCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        final List<String> pagesInKeysCategoryButNotLists = keysCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return oneByOne
                ? obtainArticlesOneByOne(pagesInKeysCategoryButNotLists)
                : obtainArticlesInBulk(pagesInKeysCategoryButNotLists);
    }

    public Optional<JSONObject> getKeyJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
