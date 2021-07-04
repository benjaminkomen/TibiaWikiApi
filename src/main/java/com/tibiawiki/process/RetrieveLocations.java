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
public class RetrieveLocations extends RetrieveAnyService {

    @Autowired
    public RetrieveLocations(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getLocationsList() {
        final List<String> locationsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.GEOGRAPHY.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return locationsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getLocationsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getLocationsList());
    }

    public JSONObject getLocationJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
