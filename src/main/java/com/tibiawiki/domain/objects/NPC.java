package com.tibiawiki.domain.objects;

import com.tibiawiki.domain.enums.City;
import com.tibiawiki.domain.enums.Gender;
import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class NPC extends WikiObject {

    private final String job;
    private final String job2;
    private final String job3;
    private final String job4;
    private final String job5;
    private final String job6;
    private final String location;
    private final City city;
    private final City city2;
    private final String street;
    private final Double posx;
    private final Double posy;
    private final Integer posz;
    private final Double posx2;
    private final Double posy2;
    private final Integer posz2;
    private final Double posx3;
    private final Double posy3;
    private final Integer posz3;
    private final Double posx4;
    private final Double posy4;
    private final Integer posz4;
    private final Double posx5;
    private final Double posy5;
    private final Integer posz5;
    private final Gender gender;
    private final String race;
    private final YesNo buysell;
    private final String buys;
    private final String sells;
    private final List<String> sounds;

    private NPC() {
        this.job = null;
        this.job2 = null;
        this.job3 = null;
        this.job4 = null;
        this.job5 = null;
        this.job6 = null;
        this.location = null;
        this.city = null;
        this.city2 = null;
        this.street = null;
        this.posx = null;
        this.posy = null;
        this.posz = null;
        this.posx2 = null;
        this.posy2 = null;
        this.posz2 = null;
        this.posx3 = null;
        this.posy3 = null;
        this.posz3 = null;
        this.posx4 = null;
        this.posy4 = null;
        this.posz4 = null;
        this.posx5 = null;
        this.posy5 = null;
        this.posz5 = null;
        this.gender = null;
        this.race = null;
        this.buysell = null;
        this.buys = null;
        this.sells = null;
        this.sounds = null;
    }

    @Builder
    private NPC(String name, String actualname, String implemented, String notes, String history, Status status,
                String job, String job2, String job3, String job4, String job5, String job6, String location, City city,
                City city2, String street, Double posx, Double posy, Integer posz, Double posx2, Double posy2,
                Integer posz2, Double posx3, Double posy3, Integer posz3, Double posx4, Double posy4, Integer posz4,
                Double posx5, Double posy5, Integer posz5, Gender gender, String race, YesNo buysell, String buys,
                String sells, List<String> sounds) {
        super(name, null, actualname, null, implemented, notes, history, status);
        this.job = job;
        this.job2 = job2;
        this.job3 = job3;
        this.job4 = job4;
        this.job5 = job5;
        this.job6 = job6;
        this.location = location;
        this.city = city;
        this.city2 = city2;
        this.street = street;
        this.posx = posx;
        this.posy = posy;
        this.posz = posz;
        this.posx2 = posx2;
        this.posy2 = posy2;
        this.posz2 = posz2;
        this.posx3 = posx3;
        this.posy3 = posy3;
        this.posz3 = posz3;
        this.posx4 = posx4;
        this.posy4 = posy4;
        this.posz4 = posz4;
        this.posx5 = posx5;
        this.posy5 = posy5;
        this.posz5 = posz5;
        this.gender = gender;
        this.race = race;
        this.buysell = buysell;
        this.buys = buys;
        this.sells = sells;
        this.sounds = sounds;
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "actualname", "job", "location", "city", "street", "posx", "posy", "posz", "gender",
                "race", "buysell", "buys", "sells", "sounds", "implemented", "notes", "history", "status");
    }
}