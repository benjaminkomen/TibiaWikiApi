package com.tibiawiki.domain.objects;

import com.tibiawiki.domain.enums.City;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class Street extends WikiObject {

    private final City city;
    private final City city2;
    private final String map;
    private final String floor;

    private Street() {
        this.city = null;
        this.city2 = null;
        this.map = null;
        this.floor = null;
    }

    @Builder
    private Street(String name, String implemented, String notes, City city, City city2, String map, String floor) {
        super(name, null, null, null, implemented, notes, null, null);
        this.city = city;
        this.city2 = city2;
        this.map = map;
        this.floor = floor;
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "implemented", "city", "city2", "map", "floor", "notes");
    }
}