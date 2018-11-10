package com.tibiawiki.process;

import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RetrieveItems extends RetrieveAny {

    private RetrieveItems() {
        // nothing to do, all dependencies are injected
    }

    public RetrieveItems(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getItemsList() {
        final List<String> itemsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.ITEM.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return itemsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getItemsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getItemsList());
    }

    public Optional<JSONObject> getItemJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
