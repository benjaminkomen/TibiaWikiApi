package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tibiawiki.domain.enums.SpellSubclass;
import com.tibiawiki.domain.enums.SpellType;
import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class Spell extends WikiObject {

    private final SpellType type;
    private final SpellSubclass subclass;
    private final SpellSubclass runegroup;
    private final String damagetype;
    private final String words;
    private final Integer mana;
    private final Integer cooldown;
    private final Integer cooldowngroup;
    private final Integer cooldowngroup2;
    private final Integer levelrequired;
    private final YesNo premium;
    private final YesNo promotion;
    private final Integer soul;
    private final YesNo zoltanonly;
    private final YesNo partyspell;
    private final YesNo specialspell;
    private final YesNo conjurespell;
    private final String voc;
    private final String druidAbDendriel;
    private final String druidAnkrahmun;
    private final String druidCarlin;
    private final String druidDarashia;
    private final String druidEdron;
    private final String druidKazordoon;
    private final String druidLibertyBay;
    private final String druidPortHope;
    private final String druidRathleton;
    private final String druidSvargrond;
    private final String druidThais;
    private final String druidVenore;
    private final String druidYalahar;
    private final String knightAbDendriel;
    private final String knightAnkrahmun;
    private final String knightCarlin;
    private final String knightDarashia;
    private final String knightEdron;
    private final String knightKazordoon;
    private final String knightLibertyBay;
    private final String knightPortHope;
    private final String knightRathleton;
    private final String knightSvargrond;
    private final String knightThais;
    private final String knightVenore;
    private final String knightYalahar;
    private final String paladinAbDendriel;
    private final String paladinAnkrahmun;
    private final String paladinCarlin;
    private final String paladinDarashia;
    private final String paladinEdron;
    private final String paladinKazordoon;
    private final String paladinLibertyBay;
    private final String paladinPortHope;
    private final String paladinRathleton;
    private final String paladinSvargrond;
    private final String paladinThais;
    private final String paladinVenore;
    private final String paladinYalahar;
    private final String sorcererAbDendriel;
    private final String sorcererAnkrahmun;
    private final String sorcererCarlin;
    private final String sorcererDarashia;
    private final String sorcererEdron;
    private final String sorcererKazordoon;
    private final String sorcererLibertyBay;
    private final String sorcererPortHope;
    private final String sorcererRathleton;
    private final String sorcererSvargrond;
    private final String sorcererThais;
    private final String sorcererVenore;
    private final String sorcererYalahar;
    private final Integer spellcost;
    private final String effect;
    private final String animation;

    private Spell() {
        this.type = null;
        this.subclass = null;
        this.runegroup = null;
        this.damagetype = null;
        this.words = null;
        this.mana = null;
        this.cooldown = null;
        this.cooldowngroup = null;
        this.cooldowngroup2 = null;
        this.levelrequired = null;
        this.premium = null;
        this.promotion = null;
        this.soul = null;
        this.zoltanonly = null;
        this.partyspell = null;
        this.specialspell = null;
        this.conjurespell = null;
        this.voc = null;
        this.druidAbDendriel = null;
        this.druidAnkrahmun = null;
        this.druidCarlin = null;
        this.druidDarashia = null;
        this.druidEdron = null;
        this.druidKazordoon = null;
        this.druidLibertyBay = null;
        this.druidPortHope = null;
        this.druidRathleton = null;
        this.druidSvargrond = null;
        this.druidThais = null;
        this.druidVenore = null;
        this.druidYalahar = null;
        this.knightAbDendriel = null;
        this.knightAnkrahmun = null;
        this.knightCarlin = null;
        this.knightDarashia = null;
        this.knightEdron = null;
        this.knightKazordoon = null;
        this.knightLibertyBay = null;
        this.knightPortHope = null;
        this.knightRathleton = null;
        this.knightSvargrond = null;
        this.knightThais = null;
        this.knightVenore = null;
        this.knightYalahar = null;
        this.paladinAbDendriel = null;
        this.paladinAnkrahmun = null;
        this.paladinCarlin = null;
        this.paladinDarashia = null;
        this.paladinEdron = null;
        this.paladinKazordoon = null;
        this.paladinLibertyBay = null;
        this.paladinPortHope = null;
        this.paladinRathleton = null;
        this.paladinSvargrond = null;
        this.paladinThais = null;
        this.paladinVenore = null;
        this.paladinYalahar = null;
        this.sorcererAbDendriel = null;
        this.sorcererAnkrahmun = null;
        this.sorcererCarlin = null;
        this.sorcererDarashia = null;
        this.sorcererEdron = null;
        this.sorcererKazordoon = null;
        this.sorcererLibertyBay = null;
        this.sorcererPortHope = null;
        this.sorcererRathleton = null;
        this.sorcererSvargrond = null;
        this.sorcererThais = null;
        this.sorcererVenore = null;
        this.sorcererYalahar = null;
        this.spellcost = null;
        this.effect = null;
        this.animation = null;
    }

    private Spell(String name, String implemented, String notes, String history, Status status, SpellType type,
                  SpellSubclass subclass, SpellSubclass runegroup, String damagetype, String words, Integer mana,
                  Integer cooldown, Integer cooldowngroup, Integer cooldowngroup2, Integer levelrequired, YesNo premium,
                  YesNo promotion, Integer soul, YesNo zoltanonly, YesNo partyspell, YesNo specialspell,
                  YesNo conjurespell, String voc, String druidAbDendriel, String druidAnkrahmun, String druidCarlin,
                  String druidDarashia, String druidEdron, String druidKazordoon, String druidLibertyBay,
                  String druidPortHope, String druidRathleton, String druidSvargrond, String druidThais,
                  String druidVenore, String druidYalahar, String knightAbDendriel, String knightAnkrahmun,
                  String knightCarlin, String knightDarashia, String knightEdron, String knightKazordoon,
                  String knightLibertyBay, String knightPortHope, String knightRathleton, String knightSvargrond,
                  String knightThais, String knightVenore, String knightYalahar, String paladinAbDendriel,
                  String paladinAnkrahmun, String paladinCarlin, String paladinDarashia, String paladinEdron,
                  String paladinKazordoon, String paladinLibertyBay, String paladinPortHope, String paladinRathleton,
                  String paladinSvargrond, String paladinThais, String paladinVenore, String paladinYalahar,
                  String sorcererAbDendriel, String sorcererAnkrahmun, String sorcererCarlin, String sorcererDarashia,
                  String sorcererEdron, String sorcererKazordoon, String sorcererLibertyBay, String sorcererPortHope,
                  String sorcererRathleton, String sorcererSvargrond, String sorcererThais, String sorcererVenore,
                  String sorcererYalahar, Integer spellcost, String effect, String animation) {
        super(name, null, null, null, implemented, notes, history, status);
        this.type = type;
        this.subclass = subclass;
        this.runegroup = runegroup;
        this.damagetype = damagetype;
        this.words = words;
        this.mana = mana;
        this.cooldown = cooldown;
        this.cooldowngroup = cooldowngroup;
        this.cooldowngroup2 = cooldowngroup2;
        this.levelrequired = levelrequired;
        this.premium = premium;
        this.promotion = promotion;
        this.soul = soul;
        this.zoltanonly = zoltanonly;
        this.partyspell = partyspell;
        this.specialspell = specialspell;
        this.conjurespell = conjurespell;
        this.voc = voc;
        this.druidAbDendriel = druidAbDendriel;
        this.druidAnkrahmun = druidAnkrahmun;
        this.druidCarlin = druidCarlin;
        this.druidDarashia = druidDarashia;
        this.druidEdron = druidEdron;
        this.druidKazordoon = druidKazordoon;
        this.druidLibertyBay = druidLibertyBay;
        this.druidPortHope = druidPortHope;
        this.druidRathleton = druidRathleton;
        this.druidSvargrond = druidSvargrond;
        this.druidThais = druidThais;
        this.druidVenore = druidVenore;
        this.druidYalahar = druidYalahar;
        this.knightAbDendriel = knightAbDendriel;
        this.knightAnkrahmun = knightAnkrahmun;
        this.knightCarlin = knightCarlin;
        this.knightDarashia = knightDarashia;
        this.knightEdron = knightEdron;
        this.knightKazordoon = knightKazordoon;
        this.knightLibertyBay = knightLibertyBay;
        this.knightPortHope = knightPortHope;
        this.knightRathleton = knightRathleton;
        this.knightSvargrond = knightSvargrond;
        this.knightThais = knightThais;
        this.knightVenore = knightVenore;
        this.knightYalahar = knightYalahar;
        this.paladinAbDendriel = paladinAbDendriel;
        this.paladinAnkrahmun = paladinAnkrahmun;
        this.paladinCarlin = paladinCarlin;
        this.paladinDarashia = paladinDarashia;
        this.paladinEdron = paladinEdron;
        this.paladinKazordoon = paladinKazordoon;
        this.paladinLibertyBay = paladinLibertyBay;
        this.paladinPortHope = paladinPortHope;
        this.paladinRathleton = paladinRathleton;
        this.paladinSvargrond = paladinSvargrond;
        this.paladinThais = paladinThais;
        this.paladinVenore = paladinVenore;
        this.paladinYalahar = paladinYalahar;
        this.sorcererAbDendriel = sorcererAbDendriel;
        this.sorcererAnkrahmun = sorcererAnkrahmun;
        this.sorcererCarlin = sorcererCarlin;
        this.sorcererDarashia = sorcererDarashia;
        this.sorcererEdron = sorcererEdron;
        this.sorcererKazordoon = sorcererKazordoon;
        this.sorcererLibertyBay = sorcererLibertyBay;
        this.sorcererPortHope = sorcererPortHope;
        this.sorcererRathleton = sorcererRathleton;
        this.sorcererSvargrond = sorcererSvargrond;
        this.sorcererThais = sorcererThais;
        this.sorcererVenore = sorcererVenore;
        this.sorcererYalahar = sorcererYalahar;
        this.spellcost = spellcost;
        this.effect = effect;
        this.animation = animation;
    }

    @JsonGetter("d-abd")
    private String getDruidAbDendriel() {
        return druidAbDendriel;
    }

    @JsonGetter("d-ank")
    public String getDruidAnkrahmun() {
        return druidAnkrahmun;
    }

    @JsonGetter("d-car")
    public String getDruidCarlin() {
        return druidCarlin;
    }

    @JsonGetter("d-dar")
    public String getDruidDarashia() {
        return druidDarashia;
    }

    @JsonGetter("d-edr")
    public String getDruidEdron() {
        return druidEdron;
    }

    @JsonGetter("d-kaz")
    public String getDruidKazordoon() {
        return druidKazordoon;
    }

    @JsonGetter("d-lib")
    public String getDruidLibertyBay() {
        return druidLibertyBay;
    }

    @JsonGetter("d-por")
    public String getDruidPortHope() {
        return druidPortHope;
    }

    @JsonGetter("d-rat")
    public String getDruidRathleton() {
        return druidRathleton;
    }

    @JsonGetter("d-sva")
    public String getDruidSvargrond() {
        return druidSvargrond;
    }

    @JsonGetter("d-tha")
    public String getDruidThais() {
        return druidThais;
    }

    @JsonGetter("d-ven")
    public String getDruidVenore() {
        return druidVenore;
    }

    @JsonGetter("d-yal")
    public String getDruidYalahar() {
        return druidYalahar;
    }

    @JsonGetter("k-abd")
    public String getKnightAbDendriel() {
        return knightAbDendriel;
    }

    @JsonGetter("k-ank")
    public String getKnightAnkrahmun() {
        return knightAnkrahmun;
    }

    @JsonGetter("k-car")
    public String getKnightCarlin() {
        return knightCarlin;
    }

    @JsonGetter("k-dar")
    public String getKnightDarashia() {
        return knightDarashia;
    }

    @JsonGetter("k-edr")
    public String getKnightEdron() {
        return knightEdron;
    }

    @JsonGetter("k-kaz")
    public String getKnightKazordoon() {
        return knightKazordoon;
    }

    @JsonGetter("k-lib")
    public String getKnightLibertyBay() {
        return knightLibertyBay;
    }

    @JsonGetter("k-por")
    public String getKnightPortHope() {
        return knightPortHope;
    }

    @JsonGetter("k-rat")
    public String getKnightRathleton() {
        return knightRathleton;
    }

    @JsonGetter("k-sva")
    public String getKnightSvargrond() {
        return knightSvargrond;
    }

    @JsonGetter("k-tha")
    public String getKnightThais() {
        return knightThais;
    }

    @JsonGetter("k-ven")
    public String getKnightVenore() {
        return knightVenore;
    }

    @JsonGetter("k-yal")
    public String getKnightYalahar() {
        return knightYalahar;
    }

    @JsonGetter("p-abd")
    public String getPaladinAbDendriel() {
        return paladinAbDendriel;
    }

    @JsonGetter("p-ank")
    public String getPaladinAnkrahmun() {
        return paladinAnkrahmun;
    }

    @JsonGetter("p-car")
    public String getPaladinCarlin() {
        return paladinCarlin;
    }

    @JsonGetter("p-dar")
    public String getPaladinDarashia() {
        return paladinDarashia;
    }

    @JsonGetter("p-edr")
    public String getPaladinEdron() {
        return paladinEdron;
    }

    @JsonGetter("p-kaz")
    public String getPaladinKazordoon() {
        return paladinKazordoon;
    }

    @JsonGetter("p-lib")
    public String getPaladinLibertyBay() {
        return paladinLibertyBay;
    }

    @JsonGetter("p-por")
    public String getPaladinPortHope() {
        return paladinPortHope;
    }

    @JsonGetter("p-rat")
    public String getPaladinRathleton() {
        return paladinRathleton;
    }

    @JsonGetter("p-sva")
    public String getPaladinSvargrond() {
        return paladinSvargrond;
    }

    @JsonGetter("p-tha")
    public String getPaladinThais() {
        return paladinThais;
    }

    @JsonGetter("p-ven")
    public String getPaladinVenore() {
        return paladinVenore;
    }

    @JsonGetter("p-yal")
    public String getPaladinYalahar() {
        return paladinYalahar;
    }

    @JsonGetter("s-abd")
    public String getSorcererAbDendriel() {
        return sorcererAbDendriel;
    }

    @JsonGetter("s-ank")
    public String getSorcererAnkrahmun() {
        return sorcererAnkrahmun;
    }

    @JsonGetter("s-car")
    public String getSorcererCarlin() {
        return sorcererCarlin;
    }

    @JsonGetter("s-dar")
    public String getSorcererDarashia() {
        return sorcererDarashia;
    }

    @JsonGetter("s-edr")
    public String getSorcererEdron() {
        return sorcererEdron;
    }

    @JsonGetter("s-kaz")
    public String getSorcererKazordoon() {
        return sorcererKazordoon;
    }

    @JsonGetter("s-lib")
    public String getSorcererLibertyBay() {
        return sorcererLibertyBay;
    }

    @JsonGetter("s-por")
    public String getSorcererPortHope() {
        return sorcererPortHope;
    }

    @JsonGetter("s-rat")
    public String getSorcererRathleton() {
        return sorcererRathleton;
    }

    @JsonGetter("s-sva")
    public String getSorcererSvargrond() {
        return sorcererSvargrond;
    }

    @JsonGetter("s-tha")
    public String getSorcererThais() {
        return sorcererThais;
    }

    @JsonGetter("s-ven")
    public String getSorcererVenore() {
        return sorcererVenore;
    }

    @JsonGetter("s-yal")
    public String getSorcererYalahar() {
        return sorcererYalahar;
    }

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