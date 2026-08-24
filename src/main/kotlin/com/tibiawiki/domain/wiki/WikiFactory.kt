package com.tibiawiki.domain.wiki

import com.tibiawiki.config.WikiClientProperties
import com.tibiawiki.domain.utils.PropertiesUtil
import io.github.fastily.jwiki.core.Wiki
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.slf4j.LoggerFactory

/**
 * Lazily builds the jwiki [Wiki] client. [Wiki.Builder.build] calls Fandom
 * (`refreshNS`); doing that in a Spring constructor would fail process start
 * when Fandom is down.
 */
class WikiFactory(
    private val properties: WikiClientProperties,
    private val build: (WikiClientProperties) -> Wiki = Companion::defaultBuild
) {
    private val lock = Any()

    @Volatile
    private var wiki: Wiki? = null

    @Volatile
    private var lastFailureEpochMs: Long = 0

    fun get(): Wiki {
        wiki?.let { return it }
        synchronized(lock) {
            wiki?.let { return it }
            val cooldownMs = properties.initFailureCooldown.toMillis().coerceAtLeast(0)
            val now = System.currentTimeMillis()
            if (lastFailureEpochMs != 0L && now - lastFailureEpochMs < cooldownMs) {
                throw WikiUnavailableException(
                    "Wiki init recently failed; waiting ${properties.initFailureCooldown} before retry"
                )
            }
            return try {
                val created = build(properties)
                wiki = created
                created
            } catch (e: WikiUnavailableException) {
                lastFailureEpochMs = now
                throw e
            } catch (e: Exception) {
                lastFailureEpochMs = now
                throw WikiUnavailableException("Failed to initialize Fandom client", e)
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(WikiFactory::class.java)

        fun fixed(wiki: Wiki): WikiFactory {
            return WikiFactory(WikiClientProperties()) { wiki }
        }

        fun defaultBuild(properties: WikiClientProperties): Wiki {
            val url = properties.apiUrl.toHttpUrlOrNull()
                ?: throw WikiUnavailableException(
                    "Invalid wiki.api-url '${properties.apiUrl}'",
                    retryable = false
                )
            val created = Wiki.Builder()
                .withApiEndpoint(url)
                .withUserAgent(properties.userAgent)
                .build()
            loginQuietly(created)
            return created
        }

        private fun loginQuietly(wiki: Wiki) {
            val username = PropertiesUtil.getUsername()
            val password = PropertiesUtil.getPassword()
            if (username != null && password != null) {
                try {
                    if (!wiki.login(username, password)) {
                        LOG.warn("Wiki login failed for {}; continuing anonymously", username)
                    }
                } catch (e: Exception) {
                    LOG.warn("Wiki login threw; continuing anonymously", e)
                }
            }
        }
    }
}
