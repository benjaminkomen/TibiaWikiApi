package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tibiawiki.domain.interfaces.Description;

public enum City implements Description {

    AB_DENDRIEL("Ab'Dendriel"),
    ANKRAHMUN("Ankrahmun"),
    CARLIN("Carlin"),
    CORMAYA("Cormaya"),
    DARASHIA("Darashia"),
    DAWNPORT("Dawnport"),
    EDRON("Edron"),
    FARMINE("Farmine"),
    GNOMEGATE("Gnomegate"),
    GRAY_BEACH("Gray Beach"),
    ISLAND_OF_DESTINY("Island of Destiny"),
    KAZORDOON("Kazordoon"),
    LIBERTY_BAY("Liberty Bay"),
    MELUNA("Meluna"),
    PORT_HOPE("Port Hope"),
    RATHLETON("Rathleton"),
    ROOKGAARD("Rookgaard"),
    ROSHAMUUL("Roshamuul"),
    SVARGROND("Svargrond"),
    THAIS("Thais"),
    TRAVORA("Travora"),
    VARIES("Varies"),
    VENORE("Venore"),
    YALAHAR("Yalahar"),
    EMPTY("");

    private String description;

    City(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
