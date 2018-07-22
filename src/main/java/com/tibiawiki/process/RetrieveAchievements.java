package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.repositories.ArticleRepository;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrieveAchievements extends RetrieveAny {

    static final String CATEGORY_ACHIEVEMENTS = "Achievements";

    public RetrieveAchievements() {
        super();
    }

    public RetrieveAchievements(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getAchievementsList() {
        final List<String> achievementsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_ACHIEVEMENTS)) {
            achievementsCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        return achievementsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getAchievementsJSON() {
        return getAchievementsJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getAchievementsJSON(boolean oneByOne) {
        final List<String> achievementsList = getAchievementsList();

        return oneByOne
                ? obtainArticlesOneByOne(achievementsList)
                : obtainArticlesInBulk(achievementsList);
    }

    public Optional<JSONObject> getAchievementJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
