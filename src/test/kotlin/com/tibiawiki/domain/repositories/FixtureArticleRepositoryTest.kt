package com.tibiawiki.domain.repositories

import com.tibiawiki.domain.objects.WikiNamespace
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

class FixtureArticleRepositoryTest {

    @Test
    fun loadsWikiTitlesFromDiskAndNormalizesUnderscores() {
        val repo = FixtureArticleRepository("src/test/resources/wiki-fixtures")

        assertThat(repo.getPageNamesFromCategory("Creatures")[0], `is`("Dragon"))
        assertThat(repo.getArticle("Dragon"), notNullValue())
        assertThat(repo.getArticle("Carlin_Sword"), notNullValue())
        assertThat(repo.getArticle("ThisDoesNotExistXYZ123"), `is`(nullValue()))
        assertThat(repo.getArticle("Loot_Statistics:Ferumbras"), notNullValue())
    }

    @Test
    fun namespacedCategoryLookupUsesTheSameFixtureList() {
        val repo = FixtureArticleRepository("src/test/resources/wiki-fixtures")

        assertThat(
            repo.getPageNamesFromCategory("Creatures", WikiNamespace.LOOT_STATISTICS),
            `is`(repo.getPageNamesFromCategory("Creatures"))
        )
    }
}
