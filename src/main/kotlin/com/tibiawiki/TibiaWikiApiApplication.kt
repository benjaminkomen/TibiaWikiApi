package com.tibiawiki

import com.tibiawiki.adapters.mediawiki.JwikiArticleRepository
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.fastily.jwiki.core.Wiki
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class TibiaWikiApiApplication {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<TibiaWikiApiApplication>(*args)
        }
    }

    @Bean
    fun wiki(): Wiki {
        return Wiki.Builder()
            .withApiEndpoint(JwikiArticleRepository.DEFAULT_WIKI_URI.toHttpUrlOrNull())
            .build()
    }
}
