package com.tibiawiki.domain.objects;

import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class Outfit extends WikiObject {

    private final String primarytype;
    private final String secondarytype;
    private final YesNo premium;
    @SuppressWarnings("squid:S1700") // class and field name are the same, but that's understandable
    private final String outfit;
    private final String addons;
    private final YesNo bought;
    private final Integer fulloutfitprice;
    private final String achievement;
    private final String artwork;

    private Outfit() {
        this.primarytype = null;
        this.secondarytype = null;
        this.premium = null;
        this.outfit = null;
        this.addons = null;
        this.bought = null;
        this.fulloutfitprice = null;
        this.achievement = null;
        this.artwork = null;
    }

    @Builder
    private Outfit(String name, String implemented, String notes, String history, Status status, String primarytype,
                   String secondarytype, YesNo premium, String outfit, String addons, YesNo bought,
                   Integer fulloutfitprice, String achievement, String artwork) {
        super(name, null, null, null, implemented, notes, history, status);
        this.primarytype = primarytype;
        this.secondarytype = secondarytype;
        this.premium = premium;
        this.outfit = outfit;
        this.addons = addons;
        this.bought = bought;
        this.fulloutfitprice = fulloutfitprice;
        this.achievement = achievement;
        this.artwork = artwork;
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "primarytype", "secondarytype", "premium", "outfit", "addons", "bought",
                "fulloutfitprice", "achievement", "implemented", "artwork", "notes", "history", "status");
    }
}