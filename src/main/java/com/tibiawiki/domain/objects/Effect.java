package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"templateType"})
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Effect extends WikiObject {

    private String primarytype;
    private String secondarytype;
    private Integer lightradius;
    private Integer lightcolor;
    private String causes;
    @SuppressWarnings("squid:S1700") // class and field name are the same, but that's understandable
    private String effect;

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "implemented", "primarytype", "secondarytype", "lightradius", "lightcolor",
                "causes", "effect", "notes", "history", "status");
    }
}