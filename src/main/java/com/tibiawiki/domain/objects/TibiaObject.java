package com.tibiawiki.domain.objects;

import com.tibiawiki.domain.enums.Article;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class TibiaObject extends WikiObject {

    private final List<Integer> itemid;
    private final String objectclass;
    private final String secondarytype;
    private final String tertiarytype;
    private final String flavortext;
    private final Integer lightradius;
    private final Integer lightcolor;
    private final Integer volume;
    private final YesNo destructable;
    private final YesNo immobile;
    private final String attrib;
    private final YesNo walkable;
    private final Integer walkingspeed;
    private final YesNo unshootable;
    private final YesNo blockspath;
    private final YesNo pickupable;
    private final YesNo holdsliquid;
    private final YesNo usable;
    private final YesNo writable;
    private final YesNo rewritable;
    private final Integer writechars;
    private final YesNo rotatable;
    private final Integer mapcolor;
    private final String location;
    private final String notes2;

    private TibiaObject() {
        this.itemid = null;
        this.objectclass = null;
        this.secondarytype = null;
        this.tertiarytype = null;
        this.flavortext = null;
        this.lightradius = null;
        this.lightcolor = null;
        this.volume = null;
        this.destructable = null;
        this.immobile = null;
        this.attrib = null;
        this.walkable = null;
        this.walkingspeed = null;
        this.unshootable = null;
        this.blockspath = null;
        this.pickupable = null;
        this.holdsliquid = null;
        this.usable = null;
        this.writable = null;
        this.rewritable = null;
        this.writechars = null;
        this.rotatable = null;
        this.mapcolor = null;
        this.location = null;
        this.notes2 = null;
    }

    @SuppressWarnings("squid:S00107")
    @Builder
    private TibiaObject(String name, Article article, String actualname, String implemented, String notes, String history,
                        Status status, List<Integer> itemid, String objectclass, String secondarytype, String tertiarytype,
                        String flavortext, Integer lightradius, Integer lightcolor, Integer volume, YesNo destructable,
                        YesNo immobile, String attrib, YesNo walkable, Integer walkingspeed, YesNo unshootable,
                        YesNo blockspath, YesNo pickupable, YesNo holdsliquid, YesNo usable, YesNo writable,
                        YesNo rewritable, Integer writechars, YesNo rotatable, Integer mapcolor, String location,
                        String notes2) {
        super(name, article, actualname, null, implemented, notes, history, status);
        this.itemid = itemid;
        this.objectclass = objectclass;
        this.secondarytype = secondarytype;
        this.tertiarytype = tertiarytype;
        this.flavortext = flavortext;
        this.lightradius = lightradius;
        this.lightcolor = lightcolor;
        this.volume = volume;
        this.destructable = destructable;
        this.immobile = immobile;
        this.attrib = attrib;
        this.walkable = walkable;
        this.walkingspeed = walkingspeed;
        this.unshootable = unshootable;
        this.blockspath = blockspath;
        this.pickupable = pickupable;
        this.holdsliquid = holdsliquid;
        this.usable = usable;
        this.writable = writable;
        this.rewritable = rewritable;
        this.writechars = writechars;
        this.rotatable = rotatable;
        this.mapcolor = mapcolor;
        this.location = location;
        this.notes2 = notes2;
    }

    @Override
    public String getTemplateType() {
        return InfoboxTemplate.OBJECT.getTemplateName();
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "article", "actualname", "itemid", "objectclass", "secondarytype", "tertiarytype",
                "flavortext", "implemented", "lightradius", "lightcolor", "volume", "destructable", "immobile", "attrib",
                "walkable", "walkingspeed", "unshootable", "blockspath", "pickupable", "holdsliquid", "usable",
                "writable", "rewritable", "writechars", "rotatable", "mapcolor", "location", "notes", "notes2",
                "history", "status");
    }
}