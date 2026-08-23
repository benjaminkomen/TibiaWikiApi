package com.tibiawiki.config

/**
 * OpenAPI text for PUT. Must match [WikiWriteFilter]: 403 when writes are off,
 * 401 only when a write token is configured and the request does not send it.
 */
object WikiWriteApiDocs {
    const val SECURITY_SCHEME = "WikiWriteToken"
    const val UNAUTHORIZED = "missing or invalid write token when WIKI_WRITE_TOKEN is set"
    const val FORBIDDEN = "wiki writes are disabled"
}
