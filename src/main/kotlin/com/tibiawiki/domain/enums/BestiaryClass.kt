package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.tibiawiki.domain.interfaces.Description

enum class BestiaryClass(
    @get:JsonValue override val description: String
) : Description {
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
    MAGICAL("Magical"),
    MAMMAL("Mammal"),
    PLANT("Plant"),
    REPTILE("Reptile"),
    SLIME("Slime"),
    UNDEAD("Undead"),
    VERMIN("Vermin")
}
