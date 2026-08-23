package com.tibiawiki.domain.wiki

/**
 * Fandom / MediaWiki is down, timed out, or could not be initialized.
 * Mapped to HTTP 503. Does not fail process start when thrown from lazy Wiki init.
 */
class WikiUnavailableException(
    message: String,
    cause: Throwable? = null,
    val retryable: Boolean = true
) : RuntimeException(message, cause)
