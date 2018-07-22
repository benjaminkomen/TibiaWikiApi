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

public class RetrieveBooks {

    private static final String CATEGORY_LISTS = "Lists";
    private static final String CATEGORY_BOOKS = "Book Texts";

    private ArticleRepository articleRepository;
    private ArticleFactory articleFactory;
    private JsonFactory jsonFactory;

    public RetrieveBooks() {
        articleRepository = new ArticleRepository(new TibiaWikiBot());
        articleFactory = new ArticleFactory();
        jsonFactory = new JsonFactory();
    }

    public Stream<JSONObject> getBooksJSON() {
        return getBooksJSON(true);
    }

    public Stream<JSONObject> getBooksJSON(boolean oneByOne) {
        final List<String> booksCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_BOOKS)) {
            booksCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        final List<String> pagesInBooksCategoryButNotLists = booksCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());

        return oneByOne
                ? obtainBooksOneByOne(pagesInBooksCategoryButNotLists)
                : obtainBooksInBulk(pagesInBooksCategoryButNotLists);
    }

    public Optional<JSONObject> getBookJSON(String pageName) {
        return Optional.ofNullable(articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }

    private Stream<JSONObject> obtainBooksInBulk(List<String> pageNames) {
        return StreamEx.ofSubLists(pageNames, 50)
                .flatMap(names -> articleRepository.getArticles(names).stream())
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }

    private Stream<JSONObject> obtainBooksOneByOne(List<String> pageNames) {
        return pageNames.stream()
                .map(pageName -> articleRepository.getArticle(pageName))
                .map(articleFactory::extractInfoboxPartOfArticle)
                .map(jsonFactory::convertInfoboxPartOfArticleToJson);
    }
}
