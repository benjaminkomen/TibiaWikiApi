package com.tibiawiki.process;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrieveNPCs extends RetrieveAny {

    private static final String CATEGORY_MOUNTS = "NPCs";

    public RetrieveNPCs() {
        super();
    }

    public Stream<JSONObject> getNPCsJSON() {
        return getNPCsJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getNPCsJSON(boolean oneByOne) {
        final List<String> npcsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_MOUNTS)) {
            npcsCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        final List<String> pagesInNPCsCategoryButNotLists = npcsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return oneByOne
                ? obtainArticlesOneByOne(pagesInNPCsCategoryButNotLists)
                : obtainArticlesInBulk(pagesInNPCsCategoryButNotLists);
    }

    public Optional<JSONObject> getNPCJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
