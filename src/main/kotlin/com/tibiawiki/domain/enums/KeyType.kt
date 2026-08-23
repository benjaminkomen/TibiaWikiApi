package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.tibiawiki.domain.interfaces.Description

enum class KeyType(
    @get:JsonValue override val description: String
) : Description {
    BONE("Bone"),
    COPPER("Copper"),
    CRYSTAL("Crystal"),
    GOLDEN("Golden"),
    GOBLIN_BONE("Goblin Bone"),
    MAGICAL("Magical"),
    OTHER("Other"),
    SILVER("Silver"),
    WOODEN("Wooden")
}
