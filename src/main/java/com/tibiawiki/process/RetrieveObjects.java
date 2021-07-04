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
public class RetrieveObjects extends RetrieveAnyService {

    @Autowired
    public RetrieveObjects(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getObjectsList() {
        final List<String> objectsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.OBJECT.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return objectsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getObjectsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getObjectsList());
    }

    public JSONObject getObjectJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
