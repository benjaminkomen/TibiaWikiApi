package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tibiawiki.domain.interfaces.Description;

public enum Gender implements Description {

    FEMALE("Female"),
    MALE("Male"),
    UNKNOWN("Unknown"),
    EMPTY("");

    private String description;

    Gender(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}