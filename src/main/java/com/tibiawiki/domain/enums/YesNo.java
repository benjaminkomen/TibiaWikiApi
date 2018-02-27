package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tibiawiki.domain.interfaces.Description;

public enum YesNo implements Description {

    YES_LOWERCASE("yes"),
    YES_UPPERCASE("Yes"),
    YES_DOT("Yes."),
    YES_UNKNOWN("yes?"),
    NO_LOWERCASE("no"),
    NO_UPPERCASE("No"),
    NO_DOT("No."),
    NO_UNKNOWN("no?"),
    UNKNOWN("?"),
    EMPTY("");

    private String description;

    YesNo(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
