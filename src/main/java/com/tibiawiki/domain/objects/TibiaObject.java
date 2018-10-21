package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties({"templateType"})
@Getter
@NoArgsConstructor
public class TibiaObject extends WikiObject {

    private List<Integer> itemid;
    private String objectclass;
    private String secondarytype;
    private String tertiarytype;
    private String flavortext;
    private Integer lightradius;
    private Integer lightcolor;
    private Integer volume;
    private YesNo destructable;
    private YesNo immobile;
    private String attrib;
    private YesNo walkable;
    private Integer walkingspeed;
    private YesNo unshootable;
    private YesNo blockspath;
    private YesNo pickupable;
    private YesNo holdsliquid;
    private YesNo usable;
    private YesNo writable;
    private YesNo rewritable;
    private Integer writechars;
    private YesNo rotatable;
    private Integer mapcolor;
    private String location;
    private String notes2;

    @Override
    public List<String> fieldOrder() {
        return Collections.emptyList();
    }
}