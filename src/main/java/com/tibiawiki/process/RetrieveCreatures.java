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
public class RetrieveCreatures extends RetrieveAnyService {

    @Autowired
    public RetrieveCreatures(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getCreaturesList() {
        final List<String> creaturesCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.CREATURE.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return creaturesCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getCreaturesJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getCreaturesList());
    }

    public JSONObject getCreatureJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
