package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Percentage {

    private String originalValue;
    private Integer value;

    private Percentage(String value) {
        this.originalValue = value;
        this.value = sanitize(value);
    }

    private Percentage(Integer value) {
        this.originalValue = value + "%";
        this.value = value;
    }

    public static Percentage of(String value) {
        return new Percentage(value);
    }

    public static Percentage of(Integer value) {
        return new Percentage(value);
    }

    @JsonValue
    public String getOriginalValue() {
        return originalValue;
    }

    private Integer sanitize(String value) {

        String sanitizedValue = value.replaceAll("\\D+", "");

        if (sanitizedValue.length() < 1) {
            return null;
        }

        return Integer.valueOf(sanitizedValue);
    }
}
