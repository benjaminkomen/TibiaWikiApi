package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class HuntingPlaceSkills {

    private final String areaname;
    private final String lvlknights;
    private final String lvlpaladins;
    private final String lvlmages;
    private final String skknights;
    private final String skpaladins;
    private final String skmages;
    private final String defknights;
    private final String defpaladins;
    private final String defmages;

    private HuntingPlaceSkills() {
        this.areaname = null;
        this.lvlknights = null;
        this.lvlpaladins = null;
        this.lvlmages = null;
        this.skknights = null;
        this.skpaladins = null;
        this.skmages = null;
        this.defknights = null;
        this.defpaladins = null;
        this.defmages = null;
        this.huntingPlace = null;
    }

    @JsonBackReference
    private HuntingPlace huntingPlace;

    public static List<String> fieldOrder() {
        return Arrays.asList("areaname", "lvlknights", "lvlpaladins", "lvlmages", "skknights", "skpaladins", "skmages",
                "defknights", "defpaladins", "defmages");
    }
}
