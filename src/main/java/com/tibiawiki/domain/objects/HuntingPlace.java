package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tibiawiki.domain.enums.City;
import com.tibiawiki.domain.enums.Star;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class HuntingPlace extends WikiObject {

    private final String image;
    private final City city;
    private final String location;
    private final String vocation;
    private final String lvlknights; // Integer?
    private final String lvlpaladins; // Integer?
    private final String lvlmages; // Integer?
    private final String skknights; // Integer?
    private final String skpaladins; // Integer?
    private final String skmages; // Integer?
    private final String defknights; // Integer?
    private final String defpaladins; // Integer?
    private final String defmages; // Integer?
    @JsonManagedReference
    private final List<HuntingPlaceSkills> lowerlevels;
    private final String loot;
    private final Star lootstar;
    private final String exp;
    private final Star expstar;
    private final String bestloot;
    private final String bestloot2;
    private final String bestloot3;
    private final String bestloot4;
    private final String bestloot5;
    private final String map;
    private final String map2;
    private final String map3;
    private final String map4;

    private HuntingPlace() {
        this.image = null;
        this.city = null;
        this.location = null;
        this.vocation = null;
        this.lvlknights = null;
        this.lvlpaladins = null;
        this.lvlmages = null;
        this.skknights = null;
        this.skpaladins = null;
        this.skmages = null;
        this.defknights = null;
        this.defpaladins = null;
        this.defmages = null;
        this.lowerlevels = null;
        this.loot = null;
        this.lootstar = null;
        this.exp = null;
        this.expstar = null;
        this.bestloot = null;
        this.bestloot2 = null;
        this.bestloot3 = null;
        this.bestloot4 = null;
        this.bestloot5 = null;
        this.map = null;
        this.map2 = null;
        this.map3 = null;
        this.map4 = null;
    }

    @SuppressWarnings("squid:S00107")
    @Builder
    private HuntingPlace(String name, String implemented, String image, City city, String location, String vocation,
                         String lvlknights, String lvlpaladins, String lvlmages, String skknights, String skpaladins,
                         String skmages, String defknights, String defpaladins, String defmages,
                         List<HuntingPlaceSkills> lowerlevels, String loot, Star lootstar, String exp, Star expstar,
                         String bestloot, String bestloot2, String bestloot3, String bestloot4, String bestloot5,
                         String map, String map2, String map3, String map4) {
        super(name, null, null, null, implemented, null, null, null);
        this.image = image;
        this.city = city;
        this.location = location;
        this.vocation = vocation;
        this.lvlknights = lvlknights;
        this.lvlpaladins = lvlpaladins;
        this.lvlmages = lvlmages;
        this.skknights = skknights;
        this.skpaladins = skpaladins;
        this.skmages = skmages;
        this.defknights = defknights;
        this.defpaladins = defpaladins;
        this.defmages = defmages;
        this.lowerlevels = lowerlevels;
        this.loot = loot;
        this.lootstar = lootstar;
        this.exp = exp;
        this.expstar = expstar;
        this.bestloot = bestloot;
        this.bestloot2 = bestloot2;
        this.bestloot3 = bestloot3;
        this.bestloot4 = bestloot4;
        this.bestloot5 = bestloot5;
        this.map = map;
        this.map2 = map2;
        this.map3 = map3;
        this.map4 = map4;
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "image", "implemented", "city", "location", "vocation", "lvlknights", "lvlpaladins",
                "lvlmages", "skknights", "skpaladins", "skmages", "defknights", "defpaladins", "defmages", "lowerlevels",
                "loot", "lootstar", "exp", "expstar", "bestloot", "bestloot2", "bestloot3", "bestloot4", "bestloot5",
                "map", "map2", "map3", "map4");
    }
}
