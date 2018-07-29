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

public class RetrieveBooks extends RetrieveAny {

    public RetrieveBooks() {
        super();
    }

    public RetrieveBooks(ArticleRepository articleRepository, ArticleFactory articleFactory, JsonFactory jsonFactory) {
        super(articleRepository, articleFactory, jsonFactory);
    }

    public List<String> getBooksList() {
        final List<String> booksCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.BOOK.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return booksCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public Stream<JSONObject> getBooksJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(InfoboxTemplate.BOOK.getCategoryName());
    }

    public Optional<JSONObject> getBookJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
