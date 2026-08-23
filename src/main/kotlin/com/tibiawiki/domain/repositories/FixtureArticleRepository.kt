package com.tibiawiki.domain.repositories

import io.github.fastily.jwiki.core.NS
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.json.JsonMapper
import java.nio.file.Files
import java.nio.file.Path

/**
 * Offline [ArticleRepository] used by the `fixtures` Spring profile.
 * Loads recorded category lists and article wikitext from disk so bootRun / CI
 * never call Fandom or tibiawiki.dev.
 */
@Repository
@Profile("fixtures")
class FixtureArticleRepository(
    @Value("\${wiki.fixtures.path:regression/fixtures}") fixturesPath: String
) : ArticleRepository {

    private val categories: Map<String, List<String>>
    private val articles: Map<String, String>

    init {
        val dir = resolveFixturesDir(fixturesPath)
        categories = readCategories(dir.resolve("categories.json"))
        articles = readArticles(dir.resolve("articles"))
        LOG.info(
            "Loaded {} categories and {} articles from {} (no outbound wiki HTTP)",
            categories.size,
            articles.size,
            dir.toAbsolutePath()
        )
    }

    override fun getPageNamesFromCategory(categoryName: String): List<String> {
        return categories[categoryName].orEmpty().toList()
    }

    override fun getPageNamesFromCategory(categoryName: String, namespace: NS): List<String> {
        return getPageNamesFromCategory(categoryName)
    }

    override fun getArticlesFromCategory(pageNames: List<String>): Map<String, String> {
        val result = LinkedHashMap<String, String>()
        for (pageName in pageNames) {
            val text = lookupArticle(pageName)
            if (text != null) {
                result[pageName] = text
            }
        }
        return result
    }

    override fun getArticlesFromCategory(categoryName: String): Map<String, String> {
        return getArticlesFromCategory(getPageNamesFromCategory(categoryName))
    }

    override fun getPageNamesUsingTemplate(templateName: String): List<String> {
        return emptyList()
    }

    override fun getArticle(pageName: String): String? {
        return lookupArticle(pageName)
    }

    override fun modifyArticle(pageName: String, pageContent: String, editSummary: String?): Boolean {
        return false
    }

    private fun lookupArticle(pageName: String?): String? {
        if (pageName == null) {
            return null
        }
        val spaced = pageName.replace('_', ' ')
        val underscored = pageName.replace(' ', '_')
        return when {
            articles.containsKey(pageName) -> articles[pageName]
            articles.containsKey(spaced) -> articles[spaced]
            else -> articles[underscored]
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(FixtureArticleRepository::class.java)
        private val CATEGORIES_TYPE = object : TypeReference<Map<String, List<String>>>() {}

        private fun readCategories(file: Path): Map<String, List<String>> {
            check(Files.isRegularFile(file)) { "Missing fixture file: ${file.toAbsolutePath()}" }
            return try {
                JsonMapper.builder().build().readValue(file.toFile(), CATEGORIES_TYPE)
            } catch (e: RuntimeException) {
                throw IllegalStateException("Unable to read ${file.toAbsolutePath()}", e)
            }
        }

        private fun readArticles(articlesDir: Path): Map<String, String> {
            check(Files.isDirectory(articlesDir)) { "Missing fixture articles dir: ${articlesDir.toAbsolutePath()}" }
            val loaded = LinkedHashMap<String, String>()
            Files.list(articlesDir).use { paths ->
                paths.filter { it.fileName.toString().endsWith(".wiki") }
                    .sorted()
                    .forEach { path ->
                        val filename = path.fileName.toString()
                        val title = filename.substring(0, filename.length - ".wiki".length)
                        loaded[title] = Files.readString(path)
                    }
            }
            return loaded.toMap()
        }

        fun resolveFixturesDir(configured: String): Path {
            val given = Path.of(configured)
            if (isFixturesDir(given)) {
                return given.toAbsolutePath().normalize()
            }
            val fromUserDir = Path.of(System.getProperty("user.dir"), configured)
            if (isFixturesDir(fromUserDir)) {
                return fromUserDir.toAbsolutePath().normalize()
            }
            var cursor: Path? = Path.of(System.getProperty("user.dir")).toAbsolutePath()
            var i = 0
            while (i < 6 && cursor != null) {
                val candidate = cursor.resolve("regression/fixtures")
                if (isFixturesDir(candidate)) {
                    return candidate.toAbsolutePath().normalize()
                }
                cursor = cursor.parent
                i++
            }
            throw IllegalStateException(
                "Could not find wiki fixtures at '$configured'. " +
                    "Set wiki.fixtures.path or WIKI_FIXTURES_PATH to the directory that contains categories.json."
            )
        }

        private fun isFixturesDir(dir: Path): Boolean {
            return Files.isDirectory(dir) && Files.isRegularFile(dir.resolve("categories.json"))
        }
    }
}
