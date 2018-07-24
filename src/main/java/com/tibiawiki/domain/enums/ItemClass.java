package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tibiawiki.domain.interfaces.Description;

public enum ItemClass implements Description {
    BODY_EQUIPMENT("Body Equipment"),
    WEAPONS("Weapons"),
    RUNES("Runes"),
    HOUSEHOLD_ITEMS("Household Items"),
    PLANTS_ANIMAL_PRODUCTS_FOOD_AND_DRINK("Plants, Animal Products, Food and Drink"),
    TOOLS_AND_OTHER_EQUIPMENT("Tools and other Equipment"),
    OTHER_ITEMS("Other Items");

    private String description;

    ItemClass(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}