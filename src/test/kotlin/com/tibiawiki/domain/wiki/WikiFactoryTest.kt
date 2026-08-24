package com.tibiawiki.domain.wiki

import com.tibiawiki.config.WikiClientProperties
import io.github.fastily.jwiki.core.Wiki
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

class WikiFactoryTest {

    @Test
    fun constructorDoesNotBuildWiki() {
        val builds = AtomicInteger()
        WikiFactory(WikiClientProperties()) {
            builds.incrementAndGet()
            mock(Wiki::class.java)
        }
        assertThat(builds.get(), `is`(0))
    }

    @Test
    fun getCachesSuccessfulWiki() {
        val builds = AtomicInteger()
        val wiki = mock(Wiki::class.java)
        val factory = WikiFactory(WikiClientProperties()) {
            builds.incrementAndGet()
            wiki
        }
        assertThat(factory.get(), sameInstance(wiki))
        assertThat(factory.get(), sameInstance(wiki))
        assertThat(builds.get(), `is`(1))
    }

    @Test
    fun initFailureIsRetryableAfterCooldown() {
        val builds = AtomicInteger()
        val properties = WikiClientProperties().apply {
            initFailureCooldown = Duration.ZERO
        }
        val factory = WikiFactory(properties) {
            if (builds.incrementAndGet() == 1) {
                throw IllegalStateException("Fandom down")
            }
            mock(Wiki::class.java)
        }
        assertThrows<WikiUnavailableException> { factory.get() }
        assertThat(factory.get(), notNullValue())
        assertThat(builds.get(), `is`(2))
    }

    @Test
    fun initFailureCooldownSuppressesRebuild() {
        val builds = AtomicInteger()
        val properties = WikiClientProperties().apply {
            initFailureCooldown = Duration.ofMinutes(5)
        }
        val factory = WikiFactory(properties) {
            builds.incrementAndGet()
            throw IllegalStateException("Fandom down")
        }
        assertThrows<WikiUnavailableException> { factory.get() }
        assertThrows<WikiUnavailableException> { factory.get() }
        assertThat(builds.get(), `is`(1))
    }

    @Test
    fun invalidApiUrlIsNotRetryable() {
        val properties = WikiClientProperties().apply {
            apiUrl = "not a url"
        }
        val thrown = assertThrows<WikiUnavailableException> {
            WikiFactory.defaultBuild(properties)
        }
        assertThat(thrown.retryable, `is`(false))
    }
}
