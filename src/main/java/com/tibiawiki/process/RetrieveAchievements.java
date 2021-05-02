package com.tibiawiki.process;

import com.tibiawiki.domain.mediawiki.ArticleRepository;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.factories.JsonFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RetrieveAchievements extends RetrieveAny {

    @Autowired
    public RetrieveAchievements(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getAchievementsList() {
        final List<String> achievementsCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.ACHIEVEMENT.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return achievementsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getAchievementsJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getAchievementsList());
    }

    public Optional<JSONObject> getAchievementJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
