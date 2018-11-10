package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tibiawiki.domain.enums.City;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"templateType"})
@Getter
@NoArgsConstructor
@Component
public class Street extends WikiObject {

    private City city;
    private City city2;
    private String map;
    private String floor;

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "implemented", "city", "city2", "map", "floor", "notes");
    }
}