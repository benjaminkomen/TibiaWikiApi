package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.objects.Familiar
import com.tibiawiki.domain.objects.Imbuement
import com.tibiawiki.domain.objects.TibiaObject
import com.tibiawiki.domain.objects.Update
import com.tibiawiki.domain.objects.World
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
        assertThat(WikiCategory.fromPath("familiars")?.template, `is`(InfoboxTemplate.FAMILIAR))
        assertThat(WikiCategory.fromPath("imbuements")?.template, `is`(InfoboxTemplate.IMBUEMENT))
        assertThat(WikiCategory.fromPath("updates")?.template, `is`(InfoboxTemplate.UPDATE))
        assertThat(WikiCategory.fromPath("worlds")?.template, `is`(InfoboxTemplate.WORLD))
        assertThat(WikiCategory.fromPath("huntingplaces"), nullValue())
        assertThat(WikiCategory.fromPath("loot"), nullValue())
        assertThat(WikiCategory.fromPath("pages"), nullValue())
        // PUT collections stay on dedicated controllers until a follow-up fold
        assertThat(WikiCategory.fromPath("fansites"), nullValue())
        assertThat(WikiCategory.fromPath("cipsoftmembers"), nullValue())
    }

    @Test
    fun wikiObjectTypesMatchSharedAndFoldedCollections() {
        assertThat(WikiCategory.ITEMS.wikiObjectType, `is`(TibiaObject::class))
        assertThat(WikiCategory.OBJECTS.wikiObjectType, `is`(TibiaObject::class))
        assertThat(WikiCategory.WORLDS.wikiObjectType, `is`(World::class))
        assertThat(WikiCategory.UPDATES.wikiObjectType, `is`(Update::class))
        assertThat(WikiCategory.FAMILIARS.wikiObjectType, `is`(Familiar::class))
        assertThat(WikiCategory.IMBUEMENTS.wikiObjectType, `is`(Imbuement::class))
    }

    @Test
    fun pathsAndTemplatesAreUnique() {
        assertThat(WikiCategory.entries.map { it.path }.toSet().size, `is`(WikiCategory.entries.size))
        assertThat(WikiCategory.entries.map { it.template }.toSet().size, `is`(WikiCategory.entries.size))
        assertThat(WikiCategory.PATHS, `is`(WikiCategory.entries.map { it.path }))
        assertThat(WikiCategory.PATH_PATTERN, `is`(WikiCategory.PATHS.joinToString("|")))
    }

    @Test
    fun pathsStayInSyncWithDocsSmokeList() {
        // Keep regression/src/smoke-docs.ts WIKI_CATEGORIES in sync with this list.
        assertThat(
            WikiCategory.PATHS,
            `is`(
                listOf(
                    "achievements", "books", "buildings", "charms", "corpses", "creatures",
                    "effects", "familiars", "imbuements", "items", "keys", "locations", "missiles",
                    "mounts", "npcs", "objects", "outfits", "quests", "spells", "streets", "updates",
                    "worlds"
                )
            )
        )
    }
}
