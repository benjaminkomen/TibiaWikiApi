package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tibiawiki.domain.enums.City;
import com.tibiawiki.domain.enums.Star;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties({"templateType"})
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HuntingPlace extends WikiObject {

    private String image;
    private City city;
    private String location;
    private String vocation;
    private String lvlknights; // Integer?
    private String lvlpaladins; // Integer?
    private String lvlmages; // Integer?
    private String skknights; // Integer?
    private String skpaladins; // Integer?
    private String skmages; // Integer?
    private String defknights; // Integer?
    private String defpaladins; // Integer?
    private String defmages; // Integer?
    @JsonManagedReference
    private List<HuntingPlaceSkills> lowerlevels;
    private String loot;
    private Star lootstar;
    private String exp;
    private Star expstar;
    private String bestloot;
    private String bestloot2;
    private String bestloot3;
    private String bestloot4;
    private String bestloot5;
    private String map;
    private String map2;
    private String map3;
    private String map4;

    @Override
    public List<String> fieldOrder() {
        return Collections.emptyList();
    }
}
