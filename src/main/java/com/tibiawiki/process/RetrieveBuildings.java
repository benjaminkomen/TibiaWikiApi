package com.tibiawiki.process;

import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrieveBuildings extends RetrieveAny {

    private static final String CATEGORY = "Buildings";

    public RetrieveBuildings() {
        super();
    }

    public RetrieveBuildings(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getBuildingsList() {
        final List<String> buildingsCategory = articleRepository.getPageNamesFromCategory(CATEGORY);
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return buildingsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getBuildingsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getBuildingsList());
    }

    public Optional<JSONObject> getBuildingJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
