package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tibiawiki.domain.enums.Article;
import com.tibiawiki.domain.enums.DamageElement;
import com.tibiawiki.domain.enums.Hands;
import com.tibiawiki.domain.enums.ItemClass;
import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.enums.WeaponType;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class Item extends WikiObject {

    private final List<Integer> itemid;
    private final YesNo marketable;
    private final YesNo usable;
    private final String sprites;
    private final String flavortext;
    private final Status ingamestatus;
    private final String words;
    private final ItemClass itemclass;
    private final String primarytype;
    private final String secondarytype;
    private final Integer lightcolor;
    private final Integer lightradius;
    private final Integer levelrequired;
    private final String vocrequired;
    private final Integer mlrequired;
    private final Hands hands;
    private final WeaponType type;
    private final String attack; // FIXME should be Integer
    private final String elementattack;
    private final Integer defense;
    private final String defensemod;
    private final Integer imbueslots;
    private final String imbuements;
    private final YesNo enchantable;
    private final YesNo enchanted;
    private final String range; // FIXME should be Integer
    private final String attackModification; // FIXME should be Integer
    private final String hitpointModification; // FIXME should be Integer
    private final Integer armor;
    private final String resist;
    private final Integer charges;
    private final Percentage criticalHitChance;
    private final Percentage criticalHitExtraDamage;
    private final Percentage manaleechChance;
    private final Percentage manaleechAmount;
    private final Percentage hitpointLeechChance;
    private final Percentage hitpointLeechAmount;
    private final String attrib;
    private final BigDecimal weight;
    private final YesNo stackable;
    private final YesNo pickupable;
    private final YesNo immobile;
    private final YesNo walkable;
    private final YesNo unshootable;
    private final YesNo blockspath;
    private final YesNo rotatable;
    private final Integer mapcolor;
    private final YesNo consumable;
    private final Integer regenseconds;
    private final List<String> sounds;
    private final YesNo writable;
    private final YesNo rewritable;
    private final Integer writechars;
    private final YesNo hangable;
    private final YesNo holdsliquid;
    private final Integer mana;
    private final DamageElement damagetype;
    private final String damage; // FIXME should be Integer
    private final Integer volume;
    private final String duration; // FIXME should be Integer
    private final YesNo destructible;
    private final List<String> droppedby;
    private final String value;
    private final String npcvalue;
    private final String npcprice;
    private final String npcvaluerook;
    private final String npcpricerook;
    private final String buyfrom;
    private final String sellto;

    private Item() {
        this.itemid = null;
        this.marketable = null;
        this.usable = null;
        this.sprites = null;
        this.flavortext = null;
        this.ingamestatus = null;
        this.words = null;
        this.itemclass = null;
        this.primarytype = null;
        this.secondarytype = null;
        this.lightcolor = null;
        this.lightradius = null;
        this.levelrequired = null;
        this.vocrequired = null;
        this.mlrequired = null;
        this.hands = null;
        this.type = null;
        this.attack = null;
        this.elementattack = null;
        this.defense = null;
        this.defensemod = null;
        this.imbueslots = null;
        this.imbuements = null;
        this.enchantable = null;
        this.enchanted = null;
        this.range = null;
        this.attackModification = null;
        this.hitpointModification = null;
        this.armor = null;
        this.resist = null;
        this.charges = null;
        this.criticalHitChance = null;
        this.criticalHitExtraDamage = null;
        this.manaleechChance = null;
        this.manaleechAmount = null;
        this.hitpointLeechChance = null;
        this.hitpointLeechAmount = null;
        this.attrib = null;
        this.weight = null;
        this.stackable = null;
        this.pickupable = null;
        this.immobile = null;
        this.walkable = null;
        this.unshootable = null;
        this.blockspath = null;
        this.rotatable = null;
        this.mapcolor = null;
        this.consumable = null;
        this.regenseconds = null;
        this.sounds = null;
        this.writable = null;
        this.rewritable = null;
        this.writechars = null;
        this.hangable = null;
        this.holdsliquid = null;
        this.mana = null;
        this.damagetype = null;
        this.damage = null;
        this.volume = null;
        this.duration = null;
        this.destructible = null;
        this.droppedby = null;
        this.value = null;
        this.npcvalue = null;
        this.npcprice = null;
        this.npcvaluerook = null;
        this.npcpricerook = null;
        this.buyfrom = null;
        this.sellto = null;
    }

    @Builder
    private Item(String name, Article article, String actualname, String plural, String implemented, String notes,
                 String history, Status status, List<Integer> itemid, YesNo marketable, YesNo usable, String sprites,
                 String flavortext, Status ingamestatus, String words, ItemClass itemclass, String primarytype,
                 String secondarytype, Integer lightcolor, Integer lightradius, Integer levelrequired,
                 String vocrequired, Integer mlrequired, Hands hands, WeaponType type, String attack,
                 String elementattack, Integer defense, String defensemod, Integer imbueslots, String imbuements,
                 YesNo enchantable, YesNo enchanted, String range, String attackModification, String hitpointModification,
                 Integer armor, String resist, Integer charges, Percentage criticalHitChance,
                 Percentage criticalHitExtraDamage, Percentage manaleechChance, Percentage manaleechAmount,
                 Percentage hitpointLeechChance, Percentage hitpointLeechAmount, String attrib, BigDecimal weight,
                 YesNo stackable, YesNo pickupable, YesNo immobile, YesNo walkable, YesNo unshootable, YesNo blockspath,
                 YesNo rotatable, Integer mapcolor, YesNo consumable, Integer regenseconds, List<String> sounds,
                 YesNo writable, YesNo rewritable, Integer writechars, YesNo hangable, YesNo holdsliquid, Integer mana,
                 DamageElement damagetype, String damage, Integer volume, String duration, YesNo destructible,
                 List<String> droppedby, String value, String npcvalue, String npcprice, String npcvaluerook,
                 String npcpricerook, String buyfrom, String sellto) {
        super(name, article, actualname, plural, implemented, notes, history, status);
        this.itemid = itemid;
        this.marketable = marketable;
        this.usable = usable;
        this.sprites = sprites;
        this.flavortext = flavortext;
        this.ingamestatus = ingamestatus;
        this.words = words;
        this.itemclass = itemclass;
        this.primarytype = primarytype;
        this.secondarytype = secondarytype;
        this.lightcolor = lightcolor;
        this.lightradius = lightradius;
        this.levelrequired = levelrequired;
        this.vocrequired = vocrequired;
        this.mlrequired = mlrequired;
        this.hands = hands;
        this.type = type;
        this.attack = attack;
        this.elementattack = elementattack;
        this.defense = defense;
        this.defensemod = defensemod;
        this.imbueslots = imbueslots;
        this.imbuements = imbuements;
        this.enchantable = enchantable;
        this.enchanted = enchanted;
        this.range = range;
        this.attackModification = attackModification;
        this.hitpointModification = hitpointModification;
        this.armor = armor;
        this.resist = resist;
        this.charges = charges;
        this.criticalHitChance = criticalHitChance;
        this.criticalHitExtraDamage = criticalHitExtraDamage;
        this.manaleechChance = manaleechChance;
        this.manaleechAmount = manaleechAmount;
        this.hitpointLeechChance = hitpointLeechChance;
        this.hitpointLeechAmount = hitpointLeechAmount;
        this.attrib = attrib;
        this.weight = weight;
        this.stackable = stackable;
        this.pickupable = pickupable;
        this.immobile = immobile;
        this.walkable = walkable;
        this.unshootable = unshootable;
        this.blockspath = blockspath;
        this.rotatable = rotatable;
        this.mapcolor = mapcolor;
        this.consumable = consumable;
        this.regenseconds = regenseconds;
        this.sounds = sounds;
        this.writable = writable;
        this.rewritable = rewritable;
        this.writechars = writechars;
        this.hangable = hangable;
        this.holdsliquid = holdsliquid;
        this.mana = mana;
        this.damagetype = damagetype;
        this.damage = damage;
        this.volume = volume;
        this.duration = duration;
        this.destructible = destructible;
        this.droppedby = droppedby;
        this.value = value;
        this.npcvalue = npcvalue;
        this.npcprice = npcprice;
        this.npcvaluerook = npcvaluerook;
        this.npcpricerook = npcpricerook;
        this.buyfrom = buyfrom;
        this.sellto = sellto;
    }

    @JsonGetter("atk_mod")
    public String getAttackModification() {
        return attackModification;
    }

    @JsonGetter("hit_mod")
    public String getHitpointModification() {
        return hitpointModification;
    }

    @JsonGetter("crithit_ch")
    public Percentage getCriticalHitChance() {
        return criticalHitChance;
    }

    @JsonGetter("critextra_dmg")
    public Percentage getCriticalHitExtraDamage() {
        return criticalHitExtraDamage;
    }

    @JsonGetter("manaleech_ch")
    public Percentage getManaleechChance() {
        return manaleechChance;
    }

    @JsonGetter("manaleech_am")
    public Percentage getManaleechAmount() {
        return manaleechAmount;
    }

    @JsonGetter("hpleech_ch")
    public Percentage getHitpointLeechChance() {
        return hitpointLeechChance;
    }

    @JsonGetter("hpleech_am")
    public Percentage getHitpointLeechAmount() {
        return hitpointLeechAmount;
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "article", "actualname", "plural", "itemid", "marketable", "usable", "sprites",
                "flavortext", "implemented", "words", "itemclass", "primarytype", "secondarytype", "lightcolor",
                "lightradius", "levelrequired", "vocrequired", "mlrequired", "hands", "type", "attack", "elementattack",
                "defense", "defensemod", "imbueslots", "imbuements", "enchantable", "enchanted", "range", "atk_mod",
                "hit_mod", "armor", "resist", "charges", "crithit_ch", "critextra_dmg", "manaleech_ch", "manaleech_am",
                "hpleech_ch", "hpleech_am", "attrib", "weight", "stackable", "pickupable", "immobile", "walkable",
                "unshootable", "blockspath", "rotatable", "mapcolor", "consumable", "regenseconds", "sounds", "writable",
                "rewritable", "writechars", "hangable", "holdsliquid", "mana", "damagetype", "damage", "volume",
                "duration", "destructible", "droppedby", "value", "npcvalue", "npcprice", "npcvaluerook", "npcpricerook",
                "buyfrom", "sellto", "notes", "history", "status");
    }
}