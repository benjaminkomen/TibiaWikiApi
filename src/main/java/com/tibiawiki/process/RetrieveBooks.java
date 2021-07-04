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
public class RetrieveBooks extends RetrieveAnyService {

    @Autowired
    public RetrieveBooks(ArticleRepository articleRepository, JsonFactory jsonFactory) {
        super(articleRepository, jsonFactory);
    }

    public List<String> getBooksList() {
        final List<String> booksCategory = articleRepository.getPageNamesFromCategory(InfoboxTemplate.BOOK.getCategoryName());
        final List<String> listsCategory = articleRepository.getPageNamesFromCategory(CATEGORY_LISTS);

        return booksCategory.stream()
                .filter(page -> !listsCategory.contains(page))
                .collect(Collectors.toList());
    }

    public List<JSONObject> getBooksJSON() {
        return getArticlesFromInfoboxTemplateAsJSON(getBooksList());
    }

    public JSONObject getBookJSON(String pageName) {
        return super.getArticleAsJSON(pageName);
    }
}
