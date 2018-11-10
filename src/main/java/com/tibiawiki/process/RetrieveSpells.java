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
public class RetrieveSpells extends RetrieveAny {

    private RetrieveSpells() {
        // nothing to do, all dependencies are injected
    }

    public RetrieveSpells(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getSpellsList() {
        final List<String> spellsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.SPELL.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return spellsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getSpellsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getSpellsList());
    }

    public Optional<JSONObject> getSpellJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
