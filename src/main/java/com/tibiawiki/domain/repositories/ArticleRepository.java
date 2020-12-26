package com.tibiawiki.domain.repositories;

import org.fastily.jwiki.core.NS;

import java.util.List;
import java.util.Map;

public interface ArticleRepository {

    List<String> getPageNamesFromCategory(String categoryName);

    List<String> getPageNamesFromCategory(String categoryName, NS namespace);

    Map<String, String> getArticlesFromCategory(List<String> pageNames);

    Map<String, String> getArticlesFromCategory(String categoryName);

    List<String> getPageNamesUsingTemplate(String templateName);

    String getArticle(String pageName);

    boolean modifyArticle(String pageName, String pageContent, String editSummary);
}
