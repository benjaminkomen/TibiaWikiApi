package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tibiawiki.domain.interfaces.Description;

public enum Status implements Description {

    ACTIVE("Active"),
    DEPRECATED("deprecated"),
    UNOBTAINABLE("unobtainable"),
    UNAVAILABLE("unavailable"),
    TS_ONLY_LOWERCASE("ts-only"),
    TS_ONLY_UPPERCASE("TS-only"),
    EVENT("event"),
    EMPTY("");

    private String description;

    Status(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}