package com.tibiawiki.process;

import com.tibiawiki.domain.RetrieveAnyService;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.mediawiki.ArticleRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RetrieveHuntingPlaces extends RetrieveAnyService {

    @Autowired
    public RetrieveHuntingPlaces(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getHuntingPlacesList() {
        final List<String> huntingPlacesCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.HUNT.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return huntingPlacesCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getHuntingPlacesJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getHuntingPlacesList());
    }

    public JSONObject getHuntingPlaceJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
