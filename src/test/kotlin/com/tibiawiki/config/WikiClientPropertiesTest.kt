package com.tibiawiki.config

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import java.time.Duration

class WikiClientPropertiesTest {

    @Test
    fun defaultsMatchDocumentedLiveClient() {
        val properties = WikiClientProperties()
        assertThat(properties.apiUrl, `is`(WikiClientProperties.DEFAULT_API_URL))
        assertThat(properties.userAgent, `is`(WikiClientProperties.DEFAULT_USER_AGENT))
        assertThat(properties.callTimeout, `is`(Duration.ofSeconds(20)))
        assertThat(properties.warmOnStartup, `is`(false))
        assertThat(properties.retry.maxAttempts, `is`(3))
        assertThat(properties.cache.ttl, `is`(Duration.ofSeconds(60)))
        assertThat(properties.expand.maxPages, `is`(5000))
    }
}
