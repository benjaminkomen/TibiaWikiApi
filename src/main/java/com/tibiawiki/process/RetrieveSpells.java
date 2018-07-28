package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrieveSpells extends RetrieveAny {

    private static final String CATEGORY = "Spells";

    public RetrieveSpells() {
        super();
    }

    public RetrieveSpells(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getSpellsList() {
        final List<String> npcsCategory = articleRepository.getMembersFromCategory(CATEGORY);
        final List<String> listsCategory = articleRepository.getMembersFromCategory(CATEGORY_LISTS);

        return npcsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getSpellsJSON() {
        return getSpellsJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getSpellsJSON(boolean oneByOne) {
        final List<String> npcsList = getSpellsList();

        return oneByOne
                ? obtainArticlesOneByOne(npcsList)
                : obtainArticlesInBulk(npcsList);
    }

    public Optional<JSONObject> getSpellJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
