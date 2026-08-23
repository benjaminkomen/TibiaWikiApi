package com.tibiawiki.domain.enums

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class InfoboxTemplateTest {

    @Test
    fun worldCategoryIsGameWorlds() {
        assertThat(InfoboxTemplate.WORLD.templateName, `is`("World"))
        assertThat(InfoboxTemplate.WORLD.categoryName, `is`("Game Worlds"))
    }

    @Test
    fun newCollectionTemplatesUseWikiCategoryNames() {
        assertThat(InfoboxTemplate.IMBUEMENT.templateName, `is`("Imbuement"))
        assertThat(InfoboxTemplate.IMBUEMENT.categoryName, `is`("Imbuements"))
        assertThat(InfoboxTemplate.UPDATE.categoryName, `is`("Updates"))
        assertThat(InfoboxTemplate.FAMILIAR.templateName, `is`("Familiar"))
        assertThat(InfoboxTemplate.FAMILIAR.categoryName, `is`("Familiars"))
    }
}
