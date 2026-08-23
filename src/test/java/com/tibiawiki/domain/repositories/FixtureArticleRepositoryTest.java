package com.tibiawiki.domain.repositories;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class FixtureArticleRepositoryTest {

    @Test
    void loadsWikiTitlesFromDiskAndNormalizesUnderscores() {
        FixtureArticleRepository repo = new FixtureArticleRepository("regression/fixtures");

        assertThat(repo.getPageNamesFromCategory("Creatures").get(0), is("Dragon"));
        assertThat(repo.getArticle("Dragon"), notNullValue());
        assertThat(repo.getArticle("Carlin_Sword"), notNullValue());
        assertThat(repo.getArticle("ThisDoesNotExistXYZ123"), is(nullValue()));
        assertThat(repo.getArticle("Loot_Statistics:Ferumbras"), notNullValue());
    }
}
