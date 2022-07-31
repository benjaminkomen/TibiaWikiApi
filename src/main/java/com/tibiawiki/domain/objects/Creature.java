package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tibiawiki.domain.enums.Article;
import com.tibiawiki.domain.enums.AttackType;
import com.tibiawiki.domain.enums.BestiaryClass;
import com.tibiawiki.domain.enums.BestiaryLevel;
import com.tibiawiki.domain.enums.BestiaryOccurrence;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.enums.Spawntype;
import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class Creature extends WikiObject {

    private final String race_id;
    private final String hitPoints; // FIXME should be Integer
    private final String experiencePoints; // FIXME should be Integer
    private final String armor; // FIXME should be Integer
    private final String summon; // FIXME should be Integer
    private final String convince; // FIXME should be Integer
    private final YesNo illusionable;
    private final String creatureclass;
    private final String primarytype;
    private final String secondarytype;
    private final BestiaryClass bestiaryclass;
    private final BestiaryLevel bestiarylevel;
    private final BestiaryOccurrence occurrence;
    private final AttackType attacktype;
    private final YesNo usespells;
    private final List<Spawntype> spawntype;
    private final YesNo isboss;
    private final String bosstiaryclass;
    private final YesNo isarenaboss;
    private final YesNo isevent;
    private final String abilities;
    private final String usedelements; // FIXME should be List<DamageElement>
    private final String maxdmg; // FIXME should be Integer
    private final Integer lightradius;
    private final Integer lightcolor;
    private final YesNo pushable;
    private final YesNo pushobjects;
    private final String walksaround; // FIXME should be List<Field>
    private final String walksthrough; // FIXME should be List<Field>
    private final YesNo paraimmune;
    private final YesNo senseinvis;
    private final Percentage physicalDmgMod;
    private final Percentage earthDmgMod;
    private final Percentage fireDmgMod;
    private final Percentage deathDmgMod;
    private final Percentage energyDmgMod;
    private final Percentage holyDmgMod;
    private final Percentage iceDmgMod;
    private final Percentage healMod;
    private final Percentage hpDrainDmgMod;
    private final Percentage drownDmgMod;
    private final String bestiaryname;
    private final String bestiarytext;
    private final List<String> sounds;
    private final String behaviour;
    private final String runsat; // FIXME should be Integer
    private final String speed; // FIXME should be Integer
    private final String strategy;
    private final String location;
    private final List<LootItem> loot;

    private Creature() {
        this.race_id = null;
        this.hitPoints = null;
        this.experiencePoints = null;
        this.armor = null;
        this.summon = null;
        this.convince = null;
        this.illusionable = null;
        this.creatureclass = null;
        this.primarytype = null;
        this.secondarytype = null;
        this.bestiaryclass = null;
        this.bestiarylevel = null;
        this.occurrence = null;
        this.attacktype = null;
        this.usespells = null;
        this.spawntype = null;
        this.isboss = null;
        this.bosstiaryclass = null;
        this.isarenaboss = null;
        this.isevent = null;
        this.abilities = null;
        this.usedelements = null;
        this.maxdmg = null;
        this.lightradius = null;
        this.lightcolor = null;
        this.pushable = null;
        this.pushobjects = null;
        this.walksaround = null;
        this.walksthrough = null;
        this.paraimmune = null;
        this.senseinvis = null;
        this.physicalDmgMod = null;
        this.earthDmgMod = null;
        this.fireDmgMod = null;
        this.deathDmgMod = null;
        this.energyDmgMod = null;
        this.holyDmgMod = null;
        this.iceDmgMod = null;
        this.healMod = null;
        this.hpDrainDmgMod = null;
        this.drownDmgMod = null;
        this.bestiaryname = null;
        this.bestiarytext = null;
        this.sounds = null;
        this.behaviour = null;
        this.runsat = null;
        this.speed = null;
        this.strategy = null;
        this.location = null;
        this.loot = null;
    }

    @SuppressWarnings("squid:S00107")
    @Builder
    public Creature(String name, Article article, String actualname, String plural, String implemented, String notes,
                    String history, Status status, String race_id, String hitPoints, String experiencePoints, String armor,
                    String summon, String convince, YesNo illusionable, String creatureclass, String primarytype,
                    String secondarytype, BestiaryClass bestiaryclass, BestiaryLevel bestiarylevel,
                    BestiaryOccurrence occurrence, AttackType attacktype, YesNo usespells, List<Spawntype> spawntype,
                    YesNo isboss, String bosstiaryclass, YesNo isarenaboss,
                    YesNo isevent, String abilities, String usedelements, String maxdmg, Integer lightradius, Integer lightcolor, YesNo pushable,
                    YesNo pushobjects, String walksaround, String walksthrough, YesNo paraimmune, YesNo senseinvis,
                    Percentage physicalDmgMod, Percentage holyDmgMod, Percentage deathDmgMod, Percentage fireDmgMod,
                    Percentage energyDmgMod, Percentage iceDmgMod, Percentage earthDmgMod, Percentage drownDmgMod,
                    Percentage hpDrainDmgMod, Percentage healMod, String bestiaryname, String bestiarytext,
                    List<String> sounds, String behaviour, String runsat, String speed, String strategy, String location,
                    List<LootItem> loot) {
        super(name, article, actualname, plural, implemented, notes, history, status);
        this.race_id = race_id;
        this.hitPoints = hitPoints;
        this.experiencePoints = experiencePoints;
        this.armor = armor;
        this.summon = summon;
        this.convince = convince;
        this.illusionable = illusionable;
        this.creatureclass = creatureclass;
        this.primarytype = primarytype;
        this.secondarytype = secondarytype;
        this.bestiaryclass = bestiaryclass;
        this.bestiarylevel = bestiarylevel;
        this.occurrence = occurrence;
        this.attacktype = attacktype;
        this.usespells = usespells;
        this.spawntype = spawntype;
        this.isboss = isboss;
        this.bosstiaryclass = bosstiaryclass;
        this.isarenaboss = isarenaboss;
        this.isevent = isevent;
        this.abilities = abilities;
        this.usedelements = usedelements;
        this.maxdmg = maxdmg;
        this.lightradius = lightradius;
        this.lightcolor = lightcolor;
        this.pushable = pushable;
        this.pushobjects = pushobjects;
        this.walksaround = walksaround;
        this.walksthrough = walksthrough;
        this.paraimmune = paraimmune;
        this.senseinvis = senseinvis;
        this.physicalDmgMod = physicalDmgMod;
        this.earthDmgMod = earthDmgMod;
        this.fireDmgMod = fireDmgMod;
        this.deathDmgMod = deathDmgMod;
        this.energyDmgMod = energyDmgMod;
        this.holyDmgMod = holyDmgMod;
        this.iceDmgMod = iceDmgMod;
        this.healMod = healMod;
        this.hpDrainDmgMod = hpDrainDmgMod;
        this.drownDmgMod = drownDmgMod;
        this.bestiaryname = bestiaryname;
        this.bestiarytext = bestiarytext;
        this.sounds = sounds;
        this.behaviour = behaviour;
        this.runsat = runsat;
        this.speed = speed;
        this.strategy = strategy;
        this.location = location;
        this.loot = loot;
    }

    @JsonGetter("hp")
    public String getHitPoints() {
        return hitPoints;
    }

    @JsonGetter("exp")
    public String getExperiencePoints() {
        return experiencePoints;
    }

    @JsonGetter("healMod")
    public Percentage gethealMod() {
        return healMod;
    }

    @Override
    public String getTemplateType() {
        return InfoboxTemplate.CREATURE.getTemplateName();
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "article", "actualname", "plural", "hp", "exp", "armor", "summon", "convince",
                "illusionable", "creatureclass", "primarytype", "secondarytype", "bestiaryclass", "bestiarylevel",
                "occurrence", "attacktype", "usespells", "spawntype", "isboss", "bosstiaryclass", "isarenaboss", "isevent", "abilities",
                "usedelements", "maxdmg", "lightradius", "lightcolor", "pushable",
                "pushobjects", "walksaround", "walksthrough", "paraimmune", "senseinvis", "physicalDmgMod", "earthDmgMod",
                "fireDmgMod", "deathDmgMod", "energyDmgMod", "holyDmgMod", "iceDmgMod", "hpDrainDmgMod", "drownDmgMod",
                "healMod", "bestiaryname", "bestiarytext", "sounds", "implemented", "race_id", "notes", "behaviour", "runsat",
                "speed", "strategy", "location", "loot", "history", "status");
    }
}
