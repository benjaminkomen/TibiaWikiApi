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
public class RetrieveStreets extends RetrieveAny {

    @Autowired
    public RetrieveStreets(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getStreetsList() {
        final List<String> streetsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.STREET.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return streetsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getStreetsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getStreetsList());
    }

    public Optional<JSONObject> getStreetJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
