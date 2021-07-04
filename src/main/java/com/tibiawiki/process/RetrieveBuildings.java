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
public class RetrieveBuildings extends RetrieveAnyService {

    @Autowired
    public RetrieveBuildings(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getBuildingsList() {
        final List<String> buildingsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.BUILDING.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return buildingsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getBuildingsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getBuildingsList());
    }

    public JSONObject getBuildingJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
