package com.tibiawiki.process;

import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RetrieveEffects extends RetrieveAny {

    @Autowired
    public RetrieveEffects(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getEffectsList() {
        final List<String> effectsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.EFFECT.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return effectsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getEffectsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getEffectsList());
    }

    public Optional<JSONObject> getEffectJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
