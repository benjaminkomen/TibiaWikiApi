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

public class RetrieveQuests extends RetrieveAny {

    public RetrieveQuests() {
        super();
    }

    public RetrieveQuests(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getQuestsList() {
        final List<String> questsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.QUEST.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return questsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getQuestsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getQuestsList());
    }

    public Optional<JSONObject> getQuestJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
