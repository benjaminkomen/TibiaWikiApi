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
        assertThat(InfoboxTemplate.FANSITE.templateName, `is`("Fansite"))
        assertThat(InfoboxTemplate.FANSITE.categoryName, `is`("Tibia_Fansites"))
        assertThat(InfoboxTemplate.CIPSOFT_MEMBER.templateName, `is`("Cipsoft_Member"))
        assertThat(InfoboxTemplate.CIPSOFT_MEMBER.categoryName, `is`("CipSoft_Members"))
    }
}
