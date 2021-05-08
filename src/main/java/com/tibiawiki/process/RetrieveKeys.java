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
public class RetrieveKeys extends RetrieveAnyService {

    @Autowired
    public RetrieveKeys(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getKeysList() {
        final List<String> keysCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.KEY.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return keysCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getKeysJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getKeysList());
    }

    public JSONObject getKeyJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
