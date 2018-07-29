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

public class RetrieveHuntingPlaces extends RetrieveAny {

    private static final String CATEGORY = "Hunting Places";

    public RetrieveHuntingPlaces() {
        super();
    }

    public RetrieveHuntingPlaces(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getHuntingPlacesList() {
        final List<String> huntingPlacesCategory = articleRepository.getPageNamesFromCategory(CATEGORY);
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return huntingPlacesCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getHuntingPlacesJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(InfoboxTemplate.HUNT.getCategoryName());
    }

    public Optional<JSONObject> getHuntingPlaceJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
