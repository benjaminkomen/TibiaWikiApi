package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tibiawiki.domain.interfaces.Description;

public enum Article implements Description {

    A("a"),
    AN("an"),
    EMPTY("");

    private String description;

    Article(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}