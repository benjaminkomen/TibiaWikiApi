package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tibiawiki.domain.enums.YesNo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"objectType"})
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Location extends WikiObject {

    private String ruler;
    private Integer population;
    private String near;
    private String organization;
    private String map;
    private String map2;
    private String map3;
    private String map4;
    private String map5;
    private String map6;
    private YesNo links;

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "implemented", "ruler", "population", "near", "organization", "map", "map2",
                "map3", "map4", "map5", "map6", "links", "status");
    }
}
