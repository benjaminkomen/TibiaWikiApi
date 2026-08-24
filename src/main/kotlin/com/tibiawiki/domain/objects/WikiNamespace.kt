package com.tibiawiki.domain.objects

/**
 * MediaWiki namespace identity used by [com.tibiawiki.domain.repositories.ArticleRepository].
 * Independent of jwiki's `NS` type so the port does not leak the wiki client.
 *
 * [id] is the numeric namespace (0 = main). [prefix] is the on-wiki title prefix
 * without a trailing colon (`Loot Statistics` for TibiaWiki namespace 112).
 */
data class WikiNamespace(
    val id: Int,
    val prefix: String
) {
    companion object {
        val MAIN = WikiNamespace(id = 0, prefix = "")
        val LOOT_STATISTICS = WikiNamespace(id = 112, prefix = "Loot Statistics")
    }
}
