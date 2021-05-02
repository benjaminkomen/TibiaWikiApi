package com.tibiawiki.process;

import com.tibiawiki.domain.mediawiki.ArticleRepository;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.JsonFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RetrieveLocations extends RetrieveAny {

    @Autowired
    public RetrieveLocations(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getLocationsList() {
        final List<String> locationsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.GEOGRAPHY.getCategoryName());
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
