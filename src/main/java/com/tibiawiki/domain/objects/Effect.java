package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"objectType"})
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Effect extends WikiObject {

    private String primarytype;
    private String secondarytype;
    private Integer lightradius;
    private Integer lightcolor;
    private String causes;
    private String effect;

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "implemented", "primarytype", "secondarytype", "lightradius", "lightcolor",
                "causes", "effect", "notes", "history", "status");
    }
}