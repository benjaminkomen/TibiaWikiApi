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
public class RetrieveCharms extends RetrieveAny {

    @Autowired
    public RetrieveCharms(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getCharmsList() {
        final List<String> charmsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.CHARM.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return charmsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getCharmsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getCharmsList());
    }

    public Optional<JSONObject> getCharmJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
