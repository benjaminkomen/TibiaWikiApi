package com.tibiawiki.process;

import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrieveCorpses extends RetrieveAny {

    public RetrieveCorpses() {
        super();
    }

    public RetrieveCorpses(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getCorpsesList() {
        final List<String> corpsesCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.CORPSE.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return corpsesCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getCorpsesJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getCorpsesList());
    }

    public Optional<JSONObject> getCorpseJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
