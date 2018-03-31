package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Grade {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4);

    private int number;

    Grade(int number) {
        this.number = number;
    }

    @JsonValue
    public int getNumber() {
        return number;
    }
}