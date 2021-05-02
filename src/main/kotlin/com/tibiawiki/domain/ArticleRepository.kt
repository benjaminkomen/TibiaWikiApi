package com.tibiawiki.domain

import org.fastily.jwiki.core.NS

interface ArticleRepository {

    fun getPageNamesFromCategory(categoryName: String): List<String>

    fun getPageNamesFromCategory(categoryName: String, namespace: NS): List<String>

    fun getArticlesFromCategory(pageNames: List<String>): Map<String, String>

    fun getArticlesFromCategory(categoryName: String): Map<String, String>

    fun getPageNamesUsingTemplate(templateName: String): List<String>

    fun getArticle(pageName: String): String?

    fun modifyArticle(pageName: String, pageContent: String, editSummary: String): Boolean
}
