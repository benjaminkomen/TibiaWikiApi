package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.tibiawiki.domain.enums.Spawntype;
import com.tibiawiki.domain.enums.YesNo;
import lombok.*;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties({"objectType"})
@Getter
public class Creature extends WikiObject {

    private String hitPoints; // FIXME should be Integer
    private String experiencePoints; // FIXME should be Integer
    private String armor; // FIXME should be Integer
    private String summon; // FIXME should be Integer
    private String convince; // FIXME should be Integer
    private YesNo illusionable;
    private String creatureclass;
    private String primarytype;
    private String secondarytype;
    private List<Spawntype> spawntype;
    private YesNo isboss;
    private YesNo isarenaboss;
    private YesNo isevent;
    private String abilities;
    private String usedelements; // FIXME should be List<DamageElement>
    private String maxdmg; // FIXME should be Integer
    private YesNo pushable;
    private YesNo pushobjects;
    private String walksaround; // FIXME should be List<Field>
    private String walksthrough; // FIXME should be List<Field>
    private YesNo paraimmune;
    private YesNo senseinvis;
    private Percentage physicalDmgMod;
    private Percentage holyDmgMod;
    private Percentage deathDmgMod;
    private Percentage fireDmgMod;
    private Percentage energyDmgMod;
    private Percentage iceDmgMod;
    private Percentage earthDmgMod;
    private Percentage drownDmgMod;
    private Percentage hpDrainDmgMod;
    private String bestiaryname;
    private String bestiarytext;
    private List<String> sounds;
    private String behaviour;
    private String runsat; // FIXME should be Integer
    private String speed; // FIXME should be Integer
    private String strategy;
    private String location;
    private List<LootItem> loot;

    public Creature() {
        super();
    }

    @JsonGetter("hp")
    public String getHitPoints() {
        return hitPoints;
    }

    @JsonSetter("hp")
    public void setHitPoints(String hitPoints) {
        this.hitPoints = hitPoints;
    }

    @JsonGetter("exp")
    public String getExperiencePoints() {
        return experiencePoints;
    }

    @JsonSetter("exp")
    public void setExperiencePoints(String experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    @Override
    public List<String> fieldOrder() {
        return Collections.emptyList();
    }
}
