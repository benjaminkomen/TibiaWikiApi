package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tibiawiki.domain.interfaces.Description;

public enum BestiaryClass implements Description {
    AMPHIBIC("Amphibic"),
    AQUATIC("Aquatic"),
    BIRD("Bird"),
    CONSTRUCT("Construct"),
    DEMON("Demon"),
    DRAGON("Dragon"),
    ELEMENTAL("Elemental"),
    EXTRA_DIMENSIONAL("Extra Dimensional"),
    FEY("Fey"),
    GIANT("Giant"),
    HUMAN("Human"),
    HUMANOID("Humanoid"),
    LYCANTHROPE("Lycanthrope"),
    LYCANTHROPE2("Lycantrophe"), // supposedly wrongly spelled
    MAGICAL("Magical"),
    MAMMAL("Mammal"),
    PLANT("Plant"),
    REPTILE("Reptile"),
    SLIME("Slime"),
    UNDEAD("Undead"),
    VERMIN("Vermin");

    private String description;

    BestiaryClass(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
