package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.objects.TibiaObject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

class WikiCategoryTest {

    @Test
    fun fromPathResolvesKnownCollectionsAndIgnoresSpecializations() {
        assertThat(WikiCategory.fromPath("achievements"), `is`(WikiCategory.ACHIEVEMENTS))
        assertThat(WikiCategory.fromPath("items")?.template, `is`(InfoboxTemplate.ITEM))
        assertThat(WikiCategory.fromPath("objects")?.template, `is`(InfoboxTemplate.OBJECT))
        assertThat(WikiCategory.fromPath("locations")?.template, `is`(InfoboxTemplate.GEOGRAPHY))
        assertThat(WikiCategory.fromPath("huntingplaces"), nullValue())
        assertThat(WikiCategory.fromPath("loot"), nullValue())
        assertThat(WikiCategory.fromPath("pages"), nullValue())
        // Issue #408/#439 collections stay on dedicated RetrieveByTemplate controllers
        assertThat(WikiCategory.fromPath("imbuements"), nullValue())
        assertThat(WikiCategory.fromPath("updates"), nullValue())
        assertThat(WikiCategory.fromPath("worlds"), nullValue())
        assertThat(WikiCategory.fromPath("familiars"), nullValue())
        assertThat(WikiCategory.fromPath("fansites"), nullValue())
        assertThat(WikiCategory.fromPath("cipsoftmembers"), nullValue())
    }

    @Test
    fun itemsAndObjectsShareTibiaObjectForPutCompatibility() {
        assertThat(WikiCategory.ITEMS.wikiObjectType, `is`(TibiaObject::class))
        assertThat(WikiCategory.OBJECTS.wikiObjectType, `is`(TibiaObject::class))
    }

    @Test
    fun pathsAndTemplatesAreUnique() {
        assertThat(WikiCategory.entries.map { it.path }.toSet().size, `is`(WikiCategory.entries.size))
        assertThat(WikiCategory.entries.map { it.template }.toSet().size, `is`(WikiCategory.entries.size))
        assertThat(WikiCategory.PATHS, `is`(WikiCategory.entries.map { it.path }))
    }
}
