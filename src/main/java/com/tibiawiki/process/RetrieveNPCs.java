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

public class RetrieveNPCs extends RetrieveAny {

    private static final String CATEGORY = "NPCs";

    public RetrieveNPCs() {
        super();
    }

    public RetrieveNPCs(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getNPCsList() {
        final List<String> npcsCategory = articleRepository.getPageNamesFromCategory(CATEGORY);
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return npcsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getNPCsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(InfoboxTemplate.NPC.getCategoryName());
    }

    public Optional<JSONObject> getNPCJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
