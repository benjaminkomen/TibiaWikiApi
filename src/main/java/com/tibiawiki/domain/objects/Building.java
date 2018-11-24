package com.tibiawiki.domain.objects;

import com.tibiawiki.domain.enums.BuildingType;
import com.tibiawiki.domain.enums.City;
import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class Building extends WikiObject {

    private final BuildingType type;
    private final String location;
    private final String posx;
    private final String posy;
    private final String posz;
    private final String street;
    private final String street2;
    private final String street3;
    private final String street4;
    private final String street5;
    private final Integer houseid;
    private final Integer size;
    private final Integer beds;
    private final Integer rent;
    private final YesNo ownable;
    private final City city;
    private final Integer openwindows;
    private final Integer floors;
    private final Integer rooms;
    private final String furnishings;
    private final String image;

    private Building() {
        this.type = null;
        this.location = null;
        this.posx = null;
        this.posy = null;
        this.posz = null;
        this.street = null;
        this.street2 = null;
        this.street3 = null;
        this.street4 = null;
        this.street5 = null;
        this.houseid = null;
        this.size = null;
        this.beds = null;
        this.rent = null;
        this.ownable = null;
        this.city = null;
        this.openwindows = null;
        this.floors = null;
        this.rooms = null;
        this.furnishings = null;
        this.image = null;
    }

    @Builder
    private Building(String name, String implemented, String notes, String history, Status status, BuildingType type, String location,
                     String posx, String posy, String posz, String street, String street2, String street3, String street4,
                     String street5, Integer houseid, Integer size, Integer beds, Integer rent, YesNo ownable, City city,
                     Integer openwindows, Integer floors, Integer rooms, String furnishings, String image) {
        super(name, null, null, null, implemented, notes, history, status);
        this.type = type;
        this.location = location;
        this.posx = posx;
        this.posy = posy;
        this.posz = posz;
        this.street = street;
        this.street2 = street2;
        this.street3 = street3;
        this.street4 = street4;
        this.street5 = street5;
        this.houseid = houseid;
        this.size = size;
        this.beds = beds;
        this.rent = rent;
        this.ownable = ownable;
        this.city = city;
        this.openwindows = openwindows;
        this.floors = floors;
        this.rooms = rooms;
        this.furnishings = furnishings;
        this.image = image;
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "implemented", "type", "location", "posx", "posy", "posz", "street", "street2",
                "street3", "street4", "street5", "houseid", "size", "beds", "rent", "ownable", "city", "openwindows",
                "floors", "rooms", "furnishings", "notes", "history", "image", "status");
    }
}