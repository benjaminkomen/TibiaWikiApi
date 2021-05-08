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
public class RetrieveEffects extends RetrieveAnyService {

    @Autowired
    public RetrieveEffects(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getEffectsList() {
        final List<String> effectsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.EFFECT.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return effectsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getEffectsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getEffectsList());
    }

    public JSONObject getEffectJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
