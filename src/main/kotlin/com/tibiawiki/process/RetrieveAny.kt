package com.tibiawiki.process

import com.tibiawiki.domain.WikiJson
import com.tibiawiki.domain.factories.ArticleFactory
import com.tibiawiki.domain.factories.JsonFactory
import com.tibiawiki.domain.repositories.ArticleRepository
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.stream.Stream

@Component
abstract class RetrieveAny(
    protected val articleRepository: ArticleRepository,
    protected val articleFactory: ArticleFactory,
    protected val jsonFactory: JsonFactory
) {

    fun getArticleAsJSON(pageName: String): Optional<WikiJson> {
        return Optional.ofNullable(articleRepository.getArticle(pageName))
            .map { articleFactory.extractInfoboxPartOfArticle(it) }
            .map { jsonFactory.convertInfoboxPartOfArticleToJson(it) }
    }

    fun getArticlesFromInfoboxTemplateAsJSON(pageNames: List<String>): Stream<WikiJson> {
        return Stream.of(pageNames)
            .flatMap { lst -> articleRepository.getArticlesFromCategory(lst).entries.stream() }
            .map { articleFactory.extractInfoboxPartOfArticle(it) }
            .map { jsonFactory.convertInfoboxPartOfArticleToJson(it) }
    }

    companion object {
        const val CATEGORY_LISTS = "Lists"
    }
}
