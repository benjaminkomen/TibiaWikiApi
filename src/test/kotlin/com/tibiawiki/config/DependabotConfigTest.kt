package com.tibiawiki.config

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

/**
 * Dependabot ignore `versions` for Docker use Bundler requirement syntax. A
 * comma-joined range in one entry (">= 26, < 29") is not a valid single
 * requirement and is dropped before it reaches Dependabot, so non-LTS Temurin
 * updates slipped through. Each entry must be one single-operator requirement.
 */
class DependabotConfigTest {

    private val config = Files.readString(repoFile(".github", "dependabot.yml"))

    @Test
    fun ignoreVersionsHaveNoCommaJoinedRanges() {
        val versionEntries = config.lines()
            .map { it.trim() }
            .filter { it.startsWith("- \"") && it.drop(3).firstOrNull() in setOf('>', '<', '=', '~', '!') }
        assertThat(versionEntries.isNotEmpty(), `is`(true))
        versionEntries.forEach { entry -> assertThat(entry, not(containsString(","))) }
    }

    @Test
    fun nonLtsTemurinMajorsAreIgnoredAndLtsMajorsAreNot() {
        listOf(26, 27, 28, 30, 31, 32).forEach { major ->
            assertThat(config, containsString("- \"~> $major.0\""))
        }
        listOf(25, 29, 33).forEach { major ->
            assertThat(config, not(containsString("~> $major.0")))
        }
    }

    private fun repoFile(vararg parts: String): Path {
        var cursor: Path? = Path.of(System.getProperty("user.dir")).toAbsolutePath()
        while (cursor != null) {
            val candidate = parts.fold(cursor) { acc, part -> acc.resolve(part) }
            if (Files.isRegularFile(candidate)) {
                return candidate
            }
            cursor = cursor.parent
        }
        error("Could not find ${parts.joinToString("/")} from ${System.getProperty("user.dir")}")
    }
}
