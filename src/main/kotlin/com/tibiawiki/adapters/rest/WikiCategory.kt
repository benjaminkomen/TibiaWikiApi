package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.objects.Achievement
import com.tibiawiki.domain.objects.Book
import com.tibiawiki.domain.objects.Building
import com.tibiawiki.domain.objects.Charm
import com.tibiawiki.domain.objects.Corpse
import com.tibiawiki.domain.objects.Creature
import com.tibiawiki.domain.objects.Effect
import com.tibiawiki.domain.objects.Familiar
import com.tibiawiki.domain.objects.Imbuement
import com.tibiawiki.domain.objects.Key
import com.tibiawiki.domain.objects.Location
import com.tibiawiki.domain.objects.Missile
import com.tibiawiki.domain.objects.Mount
import com.tibiawiki.domain.objects.NPC
import com.tibiawiki.domain.objects.Outfit
import com.tibiawiki.domain.objects.Quest
import com.tibiawiki.domain.objects.Spell
import com.tibiawiki.domain.objects.Street
import com.tibiawiki.domain.objects.TibiaObject
import com.tibiawiki.domain.objects.Update
import com.tibiawiki.domain.objects.WikiObject
import com.tibiawiki.domain.objects.World
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper
import kotlin.reflect.KClass

/**
 * REST catalog for standard wiki collections. A new category is a new entry here
 * (plus [InfoboxTemplate] and a [WikiObject] type if those do not already exist).
 *
 * Hunting places (slash-containing names) and loot stay on dedicated controllers.
 * Fansites and CipSoft members stay dedicated until their PUT types are folded.
 */
enum class WikiCategory(
    val path: String,
    val template: InfoboxTemplate,
    val tag: String,
    val wikiObjectType: KClass<out WikiObject>
) {
    ACHIEVEMENTS("achievements", InfoboxTemplate.ACHIEVEMENT, "Achievements", Achievement::class),
    BOOKS("books", InfoboxTemplate.BOOK, "Books", Book::class),
    BUILDINGS("buildings", InfoboxTemplate.BUILDING, "Buildings", Building::class),
    CHARMS("charms", InfoboxTemplate.CHARM, "Charms", Charm::class),
    CORPSES("corpses", InfoboxTemplate.CORPSE, "Corpses", Corpse::class),
    CREATURES("creatures", InfoboxTemplate.CREATURE, "Creatures", Creature::class),
    EFFECTS("effects", InfoboxTemplate.EFFECT, "Effects", Effect::class),
    FAMILIARS("familiars", InfoboxTemplate.FAMILIAR, "Familiars", Familiar::class),
    IMBUEMENTS("imbuements", InfoboxTemplate.IMBUEMENT, "Imbuements", Imbuement::class),
    ITEMS("items", InfoboxTemplate.ITEM, "Items", TibiaObject::class),
    KEYS("keys", InfoboxTemplate.KEY, "Keys", Key::class),
    LOCATIONS("locations", InfoboxTemplate.GEOGRAPHY, "Locations", Location::class),
    MISSILES("missiles", InfoboxTemplate.MISSILE, "Missiles", Missile::class),
    MOUNTS("mounts", InfoboxTemplate.MOUNT, "Mounts", Mount::class),
    NPCS("npcs", InfoboxTemplate.NPC, "NPCs", NPC::class),
    OBJECTS("objects", InfoboxTemplate.OBJECT, "Objects", TibiaObject::class),
    OUTFITS("outfits", InfoboxTemplate.OUTFIT, "Outfits", Outfit::class),
    QUESTS("quests", InfoboxTemplate.QUEST, "Quests", Quest::class),
    SPELLS("spells", InfoboxTemplate.SPELL, "Spells", Spell::class),
    STREETS("streets", InfoboxTemplate.STREET, "Streets", Street::class),
    UPDATES("updates", InfoboxTemplate.UPDATE, "Updates", Update::class),
    WORLDS("worlds", InfoboxTemplate.WORLD, "Worlds", World::class);

    fun readWikiObject(mapper: ObjectMapper, node: JsonNode): WikiObject {
        @Suppress("UNCHECKED_CAST")
        val type = wikiObjectType.java as Class<WikiObject>
        return mapper.treeToValue(node, type)
    }

    companion object {
        private val BY_PATH = entries.associateBy { it.path }

        const val PATH_PATTERN =
            "achievements|books|buildings|charms|corpses|creatures|effects|" +
                "familiars|imbuements|items|keys|locations|missiles|mounts|npcs|" +
                "objects|outfits|quests|spells|streets|updates|worlds"

        val PATHS: List<String> = entries.map { it.path }

        fun fromPath(path: String): WikiCategory? = BY_PATH[path]
    }
}
