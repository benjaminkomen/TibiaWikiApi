package com.tibiawiki.process;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrieveMounts extends RetrieveAny {

    private static final String CATEGORY_MOUNTS = "Mounts";

    public RetrieveMounts() {
        super();
    }

    public Stream<JSONObject> getMountsJSON() {
        return getMountsJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getMountsJSON(boolean oneByOne) {
        final List<String> mountsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_MOUNTS)) {
            mountsCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        final List<String> pagesInMountsCategoryButNotLists = mountsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return oneByOne
                ? obtainArticlesOneByOne(pagesInMountsCategoryButNotLists)
                : obtainArticlesInBulk(pagesInMountsCategoryButNotLists);
    }

    public Optional<JSONObject> getMountJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
