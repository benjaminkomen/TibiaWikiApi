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

public class RetrieveMounts extends RetrieveAny {

    private static final String CATEGORY = "Mounts";

    public RetrieveMounts() {
        super();
    }

    public RetrieveMounts(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getMountsList() {
        final List<String> mountsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY)) {
            mountsCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        return mountsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getMountsJSON() {
        return getMountsJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getMountsJSON(boolean oneByOne) {
        final List<String> mountsList = getMountsList();

        return oneByOne
                ? obtainArticlesOneByOne(mountsList)
                : obtainArticlesInBulk(mountsList);
    }

    public Optional<JSONObject> getMountJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
