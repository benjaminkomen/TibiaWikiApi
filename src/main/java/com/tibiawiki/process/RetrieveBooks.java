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

public class RetrieveBooks extends RetrieveAny {

    private static final String CATEGORY_BOOKS = "Book Texts";

    public RetrieveBooks() {
        super();
    }

    public RetrieveBooks(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getBooksList() {
        final List<String> booksCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_BOOKS)) {
            booksCategory.add(pageName);
        }

        final List<String> listsCategory = new ArrayList<>();
        for (String pageName : articleRepository.getMembersFromCategory(CATEGORY_LISTS)) {
            listsCategory.add(pageName);
        }

        return booksCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getBooksJSON() {
        return getBooksJSON(ONE_BY_ONE);
    }

    public Stream<JSONObject> getBooksJSON(boolean oneByOne) {
        final List<String> booksList = getBooksList();

        return oneByOne
                ? obtainArticlesOneByOne(booksList)
                : obtainArticlesInBulk(booksList);
    }

    public Optional<JSONObject> getBookJSON(String pageName) {
        return super.getArticleJSON(pageName);
    }
}
