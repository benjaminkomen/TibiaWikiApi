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
public class RetrieveCorpses extends RetrieveAnyService {

    @Autowired
    public RetrieveCorpses(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getCorpsesList() {
        final List<String> corpsesCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.CORPSE.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return corpsesCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getCorpsesJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getCorpsesList());
    }

    public JSONObject getCorpseJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
