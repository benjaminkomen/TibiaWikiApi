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
public class RetrieveNPCs extends RetrieveAnyService {

    @Autowired
    public RetrieveNPCs(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getNPCsList() {
        final List<String> npcsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.NPC.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return npcsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getNPCsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getNPCsList());
    }

    public JSONObject getNPCJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
