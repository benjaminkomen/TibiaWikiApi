package com.tibiawiki.domain.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Percentage {

    private String originalValue;
    private Integer value;

    public Percentage(String value) {
        this.originalValue = value;
        this.value = sanitize(value);
    }

    private Integer sanitize(String value) {

        String sanitizedValue = value.replaceAll("\\D+","");

        if (sanitizedValue.length() < 1) {
            return null;
        }

        return  Integer.valueOf(sanitizedValue);
    }

}
