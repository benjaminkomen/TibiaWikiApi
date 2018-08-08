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

public class RetrieveLocations extends RetrieveAny {

    private static final String CATEGORY = "Locations";

    public RetrieveLocations() {
        super();
    }

    public RetrieveLocations(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getLocationsList() {
        final List<String> locationsCategory = articleRepository.getPageNamesFromCategory(CATEGORY);
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return locationsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getLocationsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getLocationsList());
    }

    public Optional<JSONObject> getLocationJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
