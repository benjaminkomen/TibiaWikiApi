package com.tibiawiki.domain.objects;

import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class Location extends WikiObject {

    private final String ruler;
    private final String population;
    private final String near;
    private final String organization;
    private final String map;
    private final String map2;
    private final String map3;
    private final String map4;
    private final String map5;
    private final String map6;
    private final YesNo links;

    private Location() {
        this.ruler = null;
        this.population = null;
        this.near = null;
        this.organization = null;
        this.map = null;
        this.map2 = null;
        this.map3 = null;
        this.map4 = null;
        this.map5 = null;
        this.map6 = null;
        this.links = null;
    }

    @Builder
    private Location(String name, String implemented, Status status, String ruler, String population, String near,
                     String organization, String map, String map2, String map3, String map4, String map5, String map6,
                     YesNo links) {
        super(name, null, null, null, implemented, null, null, status);
        this.ruler = ruler;
        this.population = population;
        this.near = near;
        this.organization = organization;
        this.map = map;
        this.map2 = map2;
        this.map3 = map3;
        this.map4 = map4;
        this.map5 = map5;
        this.map6 = map6;
        this.links = links;
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "implemented", "ruler", "population", "near", "organization", "map", "map2",
                "map3", "map4", "map5", "map6", "links", "status");
    }
}
