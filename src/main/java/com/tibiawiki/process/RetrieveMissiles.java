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
public class RetrieveMissiles extends RetrieveAnyService {

    @Autowired
    public RetrieveMissiles(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getMissilesList() {
        final List<String> effectsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.MISSILE.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return effectsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getMissilesJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getMissilesList());
    }

    public JSONObject getMissileJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
