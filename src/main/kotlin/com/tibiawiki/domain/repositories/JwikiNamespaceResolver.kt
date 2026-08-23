package com.tibiawiki.domain.repositories

import com.tibiawiki.domain.objects.WikiNamespace
import io.github.fastily.jwiki.core.NS
import io.github.fastily.jwiki.core.Wiki

/**
 * Maps a domain [WikiNamespace] to jwiki [NS] using the client's public API.
 *
 * Central jwiki 1.11.0 has no public constructor for custom numeric namespaces.
 * [Wiki.getNS] builds those from siteinfo, so loot namespace 112 does not need
 * reflection or the `benjaminkomen/jwiki` fork.
 */
internal object JwikiNamespaceResolver {

    fun resolve(wiki: Wiki, namespace: WikiNamespace): NS {
        if (namespace.id == WikiNamespace.MAIN.id) {
            return NS.MAIN
        }
        for (prefix in prefixesToTry(namespace.prefix)) {
            val resolved = wiki.getNS(prefix)
            if (resolved != null) {
                return resolved
            }
        }
        throw UnknownWikiNamespaceException(namespace)
    }

    private fun prefixesToTry(prefix: String): List<String> {
        if (prefix.isBlank()) {
            return emptyList()
        }
        val candidates = linkedSetOf(prefix)
        if (prefix.contains(' ')) {
            candidates.add(prefix.replace(' ', '_'))
        }
        if (prefix.contains('_')) {
            candidates.add(prefix.replace('_', ' '))
        }
        return candidates.toList()
    }

    class UnknownWikiNamespaceException(namespace: WikiNamespace) : IllegalStateException(
        "Wiki siteinfo has no namespace for prefix '${namespace.prefix}' (id ${namespace.id})"
    )
}
