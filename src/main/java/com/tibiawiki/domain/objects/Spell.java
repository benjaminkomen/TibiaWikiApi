package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.tibiawiki.domain.enums.SpellSubclass;
import com.tibiawiki.domain.enums.SpellType;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"templateType"})
@Getter
@NoArgsConstructor
public class Spell extends WikiObject {

    private SpellType type;
    private SpellSubclass subclass;
    private SpellSubclass runegroup;
    private String damagetype;
    private String words;
    private Integer mana;
    private Integer cooldown;
    private Integer cooldowngroup;
    private Integer cooldowngroup2;
    private Integer levelrequired;
    private YesNo premium;
    private YesNo promotion;
    private Integer soul;
    private YesNo zoltanonly;
    private YesNo partyspell;
    private YesNo specialspell;
    private YesNo conjurespell;
    private String voc;
    private String druidAbDendriel;
    private String druidAnkrahmun;
    private String druidCarlin;
    private String druidDarashia;
    private String druidEdron;
    private String druidKazordoon;
    private String druidLibertyBay;
    private String druidPortHope;
    private String druidRathleton;
    private String druidSvargrond;
    private String druidThais;
    private String druidVenore;
    private String druidYalahar;
    private String knightAbDendriel;
    private String knightAnkrahmun;
    private String knightCarlin;
    private String knightDarashia;
    private String knightEdron;
    private String knightKazordoon;
    private String knightLibertyBay;
    private String knightPortHope;
    private String knightRathleton;
    private String knightSvargrond;
    private String knightThais;
    private String knightVenore;
    private String knightYalahar;
    private String paladinAbDendriel;
    private String paladinAnkrahmun;
    private String paladinCarlin;
    private String paladinDarashia;
    private String paladinEdron;
    private String paladinKazordoon;
    private String paladinLibertyBay;
    private String paladinPortHope;
    private String paladinRathleton;
    private String paladinSvargrond;
    private String paladinThais;
    private String paladinVenore;
    private String paladinYalahar;
    private String sorcererAbDendriel;
    private String sorcererAnkrahmun;
    private String sorcererCarlin;
    private String sorcererDarashia;
    private String sorcererEdron;
    private String sorcererKazordoon;
    private String sorcererLibertyBay;
    private String sorcererPortHope;
    private String sorcererRathleton;
    private String sorcererSvargrond;
    private String sorcererThais;
    private String sorcererVenore;
    private String sorcererYalahar;
    private Integer spellcost;
    private String effect;
    private String animation;

    @JsonGetter("d-abd")
    private String getDruidAbDendriel() {
        return druidAbDendriel;
    }

    @JsonSetter("d-abd")
    private void setDruidAbDendriel(String druidAbDendriel) {
        this.druidAbDendriel = druidAbDendriel;
    }

    //@todo add jsongetters and setters for the other 51 parameters

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "type", "subclass", "runegroup", "damagetype", "words", "mana", "cooldown",
                "cooldowngroup", "cooldowngroup2", "levelrequired", "premium", "promotion", "soul", "zoltanonly",
                "partyspell", "specialspell", "conjurespell", "voc", "d-abd", "d-ank", "d-car", "d-dar", "d-edr",
                "d-kaz", "d-lib", "d-por", "d-rat", "d-sva", "d-tha", "d-ven", "d-yal", "k-abd", "k-ank", "k-car",
                "k-dar", "k-edr", "k-kaz", "k-lib", "k-por", "k-rat", "k-sva", "k-tha", "k-ven", "k-yal", "p-abd",
                "p-ank", "p-car", "p-dar", "p-edr", "p-kaz", "p-lib", "p-por", "p-rat", "p-sva", "p-tha", "p-ven",
                "p-yal", "s-abd", "s-ank", "s-car", "s-dar", "s-edr", "s-kaz", "s-lib", "s-por", "s-rat", "s-sva",
                "s-tha", "s-ven", "s-yal", "spellcost", "implemented", "effect", "notes", "animation", "history",
                "status");
    }
}