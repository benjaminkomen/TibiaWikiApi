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
public class RetrieveItems extends RetrieveAnyService {

    @Autowired
    public RetrieveItems(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getItemsList() {
        final List<String> itemsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.ITEM.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return itemsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getItemsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getItemsList());
    }

    public JSONObject getItemJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
