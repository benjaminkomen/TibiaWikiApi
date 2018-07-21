package com.tibiawiki.process;

import com.tibiawiki.domain.factories.ArticleFactory;
import com.tibiawiki.domain.factories.JsonFactory;
import com.tibiawiki.domain.objects.TibiaWikiBot;
import com.tibiawiki.domain.repositories.ArticleRepository;
import one.util.streamex.StreamEx;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrieveAchievements {

    static final String CATEGORY_LISTS = "Lists";
    static final String CATEGORY_ACHIEVEMENTS = "Achievements";

    private ArticleRepository articleRepository;
    private ArticleFactory articleFactory;
    private JsonFactory jsonFactory;

    public RetrieveAchievements() {
        articleRepository = new ArticleRepository(new TibiaWikiBot());
        articleFactory = new ArticleFactory();
        jsonFactory = new JsonFactory();
    }

    public Stream<JSONObject> getAchievementsJSON() {
        return getAchievementsJSON(true);
    }

    public Stream<JSONObject> getAchievementsJSON(boolean oneByOne) {
        final List<String> achievementsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_ACHIEVEMENTS)) {
            achievementsCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        final List<String> pagesInAchievementsCategoryButNotLists = achievementsCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return oneByOne
                ? obtainAchievementsOneByOne(pagesInAchievementsCategoryButNotLists)
                : obtainAchievementsInBulk(pagesInAchievementsCategoryButNotLists);
    }

    public Optional<JSONObject> getAchievementJSON(String pageName) {
        return Stream.of(articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson)
                .findAny();
    }

    private Stream<JSONObject> obtainAchievementsInBulk(List<String> pageNames) {
        return StreamEx.ofSubLists(pageNames, 50)
                .flatMap(names -> articleRepository.getArticles(names).stream())
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }

    private Stream<JSONObject> obtainAchievementsOneByOne(List<String> pageNames) {
        return pageNames.stream()
                .map(pageName -> articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }
}
