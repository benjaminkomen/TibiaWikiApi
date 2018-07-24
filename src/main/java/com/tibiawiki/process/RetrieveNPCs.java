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

public class RetrieveNPCs extends RetrieveAny {

    private static final String CATEGORY = "NPCs";

    public RetrieveNPCs() {
        super();
    }

    public RetrieveNPCs(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getNPCsList() {
        final List<String> npcsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY)) {
            npcsCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        return npcsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getNPCsJSON() {
        return getNPCsJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getNPCsJSON(boolean oneByOne) {
        final List<String> npcsList = getNPCsList();

        return oneByOne
                ? obtainArticlesOneByOne(npcsList)
                : obtainArticlesInBulk(npcsList);
    }

    public Optional<JSONObject> getNPCJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
