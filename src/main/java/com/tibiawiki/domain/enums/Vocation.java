package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tibiawiki.domain.interfaces.Description;

public enum Vocation implements Description {

    KNIGHT("knight"),
    PALADIN("paladin"),
    DRUID("druid"),
    SORCERER("sorcerer");

    private String description;

    Vocation(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
