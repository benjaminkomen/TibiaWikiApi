package com.tibiawiki.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * Live Fandom / MediaWiki client settings. Used only by the default profile;
 * the `fixtures` profile never constructs [com.tibiawiki.domain.wiki.WikiFactory].
 */
@ConfigurationProperties(prefix = "wiki")
class WikiClientProperties {
    var apiUrl: String = DEFAULT_API_URL
    var userAgent: String = DEFAULT_USER_AGENT
    var callTimeout: Duration = Duration.ofSeconds(20)
    var initFailureCooldown: Duration = Duration.ofSeconds(5)
    var warmOnStartup: Boolean = false
    var retry: Retry = Retry()
    var cache: Cache = Cache()
    var expand: Expand = Expand()

    class Retry {
        var maxAttempts: Int = 3
        var baseDelay: Duration = Duration.ofMillis(200)
        var maxDelay: Duration = Duration.ofSeconds(2)
    }

    class Cache {
        var ttl: Duration = Duration.ofSeconds(60)
        var maxCategoryEntries: Long = 256
        var maxArticleEntries: Long = 2048
    }

    class Expand {
        var maxPages: Int = 5000
    }

    companion object {
        const val DEFAULT_API_URL = "https://tibia.fandom.com/api.php"
        const val DEFAULT_USER_AGENT =
            "TibiaWikiApi/2.0 (https://github.com/benjaminkomen/TibiaWikiApi; +https://tibiawiki.dev)"
    }
}
