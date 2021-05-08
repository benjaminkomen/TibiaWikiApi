package com.tibiawiki.process;

import com.tibiawiki.domain.RetrieveAnyService;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.mediawiki.ArticleRepository;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RetrieveAchievements extends RetrieveAnyService {

    @Autowired
    public RetrieveAchievements(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getAchievementsList() {
        final List<String> achievementsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.ACHIEVEMENT.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return achievementsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getAchievementsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getAchievementsList());
    }

    @Nullable
    public JSONObject getAchievementJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
