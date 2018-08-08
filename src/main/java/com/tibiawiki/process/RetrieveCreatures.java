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

public class RetrieveCreatures extends RetrieveAny {

    public RetrieveCreatures() {
        super();
    }

    public RetrieveCreatures(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getCreaturesList() {
        final List<String> creaturesCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.CREATURE.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return creaturesCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getCreaturesJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getCreaturesList());
    }

    public Optional<JSONObject> getCreatureJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
