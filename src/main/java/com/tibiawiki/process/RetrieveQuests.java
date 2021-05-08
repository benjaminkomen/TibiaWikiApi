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
public class RetrieveQuests extends RetrieveAnyService {

    @Autowired
    public RetrieveQuests(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getQuestsList() {
        final List<String> questsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.QUEST.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return questsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getQuestsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getQuestsList());
    }

    public JSONObject getQuestJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
