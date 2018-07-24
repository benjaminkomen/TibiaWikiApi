package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tibiawiki.domain.enums.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties({"objectType"})
@Getter
@NoArgsConstructor
public class Item extends WikiObject {

    private List<Integer> itemid;
    private YesNo marketable;
    private YesNo usable;
    private String sprites;
    private String flavortext;
    private Status ingamestatus;
    private String words;
    private ItemClass itemclass;
    private String primarytype;
    private String secondarytype;
    private Integer lightcolor;
    private Integer lightradius;
    private Integer levelrequired;
    private String vocrequired;
    private Integer mlrequired;
    private Hands hands;
    private WeaponType type;
    private String attack; // FIXME should be Integer
    private String elementattack;
    private Integer defense;
    private String defensemod;
    private Integer imbueslots;
    private String imbuements;
    private YesNo enchantable;
    private YesNo enchanted;
    private String range; // FIXME should be Integer
    private String atk_mod; // FIXME should be Integer
    private String hit_mod; // FIXME should be Integer
    private Integer armor;
    private String resist;
    private Integer charges;
    private Percentage crithit_ch;
    private Percentage critextra_dmg;
    private Percentage manaleech_ch;
    private Percentage manaleech_am;
    private Percentage hpleech_ch;
    private Percentage hpleech_am;
    private String attrib;
    private Double weight;
    private YesNo stackable;
    private YesNo pickupable;
    private YesNo immobile;
    private YesNo walkable;
    private YesNo unshootable;
    private YesNo blockspath;
    private YesNo rotatable;
    private Integer mapcolor;
    private YesNo consumable;
    private Integer regenseconds;
    private List<String> sounds;
    private YesNo writable;
    private YesNo rewritable;
    private Integer writechars;
    private YesNo hangable;
    private YesNo holdsliquid;
    private Integer mana;
    private DamageElement damagetype;
    private String damage; // FIXME should be Integer
    private Integer volume;
    private String duration; // FIXME should be Integer
    private YesNo destructible;
    private List<String> droppedby;
    private String value;
    private String npcvalue;
    private String npcprice;
    private Double npcvaluerook;
    private Double npcpricerook;
    private String buyfrom;
    private String sellto;

    @Override
    public List<String> fieldOrder() {
        return Collections.emptyList();
    }
}