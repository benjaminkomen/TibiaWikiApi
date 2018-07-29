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

public class RetrieveObjects extends RetrieveAny {

    private static final String CATEGORY = "Objects";

    public RetrieveObjects() {
        super();
    }

    public RetrieveObjects(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getObjectsList() {
        final List<String> objectsCategory = articleRepository.getPageNamesFromCategory(CATEGORY);
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return objectsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getObjectsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(InfoboxTemplate.OBJECT.getCategoryName());
    }

    public Optional<JSONObject> getObjectJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
