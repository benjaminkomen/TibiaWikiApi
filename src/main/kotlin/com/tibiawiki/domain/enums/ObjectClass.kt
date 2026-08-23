package com.tibiawiki.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.tibiawiki.domain.interfaces.Description

enum class ObjectClass(
    @get:JsonValue override val description: String
) : Description {
    BODY_EQUIPMENT("Body Equipment"),
    WEAPONS("Weapons"),
    RUNES("Runes"),
    HOUSEHOLD_ITEMS("Household Items"),
    PLANTS_ANIMAL_PRODUCTS_FOOD_AND_DRINK("Plants, Animal Products, Food and Drink"),
    TOOLS_AND_OTHER_EQUIPMENT("Tools and other Equipment"),
    OTHER_ITEMS("Other Items")
}
