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
        assertThat(properties.io.threads, `is`(WikiClientProperties.DEFAULT_IO_THREADS))
        assertThat(properties.io.queueCapacity, `is`(WikiClientProperties.DEFAULT_IO_QUEUE_CAPACITY))
        assertThat(properties.expand.maxPages, `is`(5000))
        assertThat(properties.expand.maxConcurrent, `is`(WikiClientProperties.DEFAULT_EXPAND_MAX_CONCURRENT))
        assertThat(properties.expand.acquireTimeout, `is`(Duration.ofSeconds(20)))
    }
}
