package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.tibiawiki.domain.interfaces.WikiTemplate

enum class InfoboxTemplate(
    @get:JsonValue val templateName: String,
    val categoryName: String
) : WikiTemplate {
    ACHIEVEMENT("Achievement", "Achievements"),
    BOOK("Book", "Book Texts"),
    BUILDING("Building", "Buildings"),
    CHARM("Charm", "Charms"),
    CIPSOFT_MEMBER("Cipsoft_Member", "CipSoft_Members"),
    CORPSE("Corpse", "Corpses"),
    CREATURE("Creature", "Creatures"),
    EFFECT("Effect", "Effects"),
    FANSITE("Fansite", "Tibia_Fansites"),
    GEOGRAPHY("Geography", "Locations"),
    HUNT("Hunt", "Hunting Places"),
    ITEM("Item", "Pickupable Objects"),
    KEY("Key", "Keys"),
    LOOT("Loot2", "Loot Statistics"),
    MISSILE("Missile", "Missiles"),
    MOUNT("Mount", "Mounts"),
    NPC("NPC", "NPCs"),
    OBJECT("Object", "Objects"),
    OUTFIT("Outfit", "Outfits"),
    QUEST("Quest", "Quest Overview Pages"),
    SPELL("Spell", "Spells"),
    STREET("Street", "Streets"),
    UPDATE("Update", "Updates"),
    WORLD("World", "Gameworlds");

    override val templateDescription: String
        get() = "Template:Infobox_$templateName"
}
