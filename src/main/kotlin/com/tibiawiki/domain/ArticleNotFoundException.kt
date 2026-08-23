package com.tibiawiki.domain

class ArticleNotFoundException(
    val articleName: String? = null
) : RuntimeException(messageFor(articleName)) {

    companion object {
        private fun messageFor(articleName: String?): String {
            return if (articleName.isNullOrBlank()) {
                "Article not found"
            } else {
                "Article not found: $articleName"
            }
        }
    }
}
