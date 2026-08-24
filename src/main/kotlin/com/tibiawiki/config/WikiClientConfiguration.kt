package com.tibiawiki.config

import com.tibiawiki.domain.wiki.WikiCallSupport
import com.tibiawiki.domain.wiki.WikiFactory
import com.tibiawiki.domain.wiki.WikiResponseCache
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * Live Fandom client wiring. Not loaded under the `fixtures` profile so CI
 * and offline boot never construct jwiki or call Fandom.
 */
@Configuration
@Profile("!fixtures")
@EnableConfigurationProperties(WikiClientProperties::class)
class WikiClientConfiguration {

    @Bean
    fun wikiFactory(properties: WikiClientProperties): WikiFactory {
        return WikiFactory(properties)
    }

    @Bean
    fun wikiCallSupport(properties: WikiClientProperties): WikiCallSupport {
        return WikiCallSupport(properties)
    }

    @Bean
    fun wikiResponseCache(properties: WikiClientProperties): WikiResponseCache {
        return WikiResponseCache(properties)
    }

    @Bean
    fun wikiWarmup(
        properties: WikiClientProperties,
        wikiFactory: WikiFactory,
        wikiCallSupport: WikiCallSupport
    ): ApplicationRunner {
        return ApplicationRunner {
            if (!properties.warmOnStartup) {
                return@ApplicationRunner
            }
            try {
                wikiCallSupport.call("wiki-warmup") { wikiFactory.get() }
                LOG.info("Warmed Fandom Wiki client for {}", properties.apiUrl)
            } catch (e: Exception) {
                LOG.warn("Wiki warmup failed; requests will retry lazily", e)
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(WikiClientConfiguration::class.java)
    }
}
