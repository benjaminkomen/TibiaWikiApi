package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tibiawiki.domain.interfaces.Description;

public enum Spawntype implements Description {

    REGULAR("Regular"),
    RAID("Raid"),
    EVENT("Event"),
    UNIQUE("Unique"),
    TRIGGERED("Triggered"),
    UNBLOCKABLE("Unblockable"),
    EMPTY("");

    private String description;

    Spawntype(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}