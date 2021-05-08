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
public class RetrieveSpells extends RetrieveAnyService {

    @Autowired
    public RetrieveSpells(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getSpellsList() {
        final List<String> spellsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.SPELL.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return spellsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getSpellsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getSpellsList());
    }

    public JSONObject getSpellJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
