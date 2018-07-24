package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tibiawiki.domain.interfaces.Description;

public enum QuestType implements Description {

    WORLD_CHANGE("change"),
    MINI_WORLD_CHANGE("mwc"),
    WORLD_EVENT("event"),
    WORLD_TASK("task");

    private String description;

    QuestType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}