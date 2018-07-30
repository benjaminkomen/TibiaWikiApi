package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrieveAchievements extends RetrieveAny {

    static final String CATEGORY = "Achievements";

    public RetrieveAchievements() {
        super();
    }

    public RetrieveAchievements(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getAchievementsList() {
        final List<String> achievementsCategory = articleRepository.getPageNamesFromCategory(CATEGORY);
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
