package com.tibiawiki.process;

import com.tibiawiki.domain.ArticleRepository;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RetrieveObjects extends RetrieveAny {

    @Autowired
    public RetrieveObjects(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getObjectsList() {
        final List<String> objectsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.OBJECT.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return objectsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getObjectsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getObjectsList());
    }

    public Optional<JSONObject> getObjectJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
