package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tibiawiki.domain.interfaces.WikiTemplate;

public enum InfoboxTemplate implements WikiTemplate {

    ACHIEVEMENT("Achievement", "Achievements"),
    BOOK("Book", "Book Texts"),
    BUILDING("Building", "Buildings"),
    CIPSOFT_MEMBER("Cipsoft_Member", "CipSoft_Members"),
    CORPSE("Corpse", "Corpses"),
    CREATURE("Creature", "Creatures"),
    EFFECT("Effect", "Effects"),
    FANSITE("Fansite", "Tibia_Fansites"),
    GEOGRAPHY("Geography", "Locations"),
    HUNT("Hunt", "Hunting Places"),
    ITEM("Item", "Items"),
    KEY("Key", "Keys"),
    MOUNT("Mount", "Mounts"),
    NPC("NPC", "NPCs"),
    OBJECT("Object", "Objects"),
    OUTFIT("Outfit", "Outfits"),
    QUEST("Quest", "Quest Overview Pages"),
    SPELL("Spell", "Spells"),
    STREET("Street", "Streets"),
    UPDATE("Update", "Updates"),
    WORLD("World", "Gameworlds");

    private String templateName;
    private String categoryName;

    InfoboxTemplate(String templateName, String categoryName) {
        this.templateName = templateName;
        this.categoryName = categoryName;
    }

    @JsonValue
    public String getTemplateName() {
        return templateName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public String getTemplateDescription() {
        return "Template:Infobox_" + getTemplateName();
    }
}
