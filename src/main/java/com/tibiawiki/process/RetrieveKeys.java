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

public class RetrieveKeys extends RetrieveAny {

    private static final String CATEGORY = "Keys";

    public RetrieveKeys() {
        super();
    }

    public RetrieveKeys(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getKeysList() {
        final List<String> keysCategory = articleRepository.getPageNamesFromCategory(CATEGORY);
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return keysCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getKeysJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(InfoboxTemplate.KEY.getCategoryName());
    }

    public Optional<JSONObject> getKeyJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
