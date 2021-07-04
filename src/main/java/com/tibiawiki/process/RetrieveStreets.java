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
public class RetrieveStreets extends RetrieveAnyService {

    @Autowired
    public RetrieveStreets(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getStreetsList() {
        final List<String> streetsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.STREET.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return streetsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getStreetsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getStreetsList());
    }

    public JSONObject getStreetJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
