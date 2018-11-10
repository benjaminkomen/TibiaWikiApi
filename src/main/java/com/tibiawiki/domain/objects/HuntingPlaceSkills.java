package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class HuntingPlaceSkills {

    private String areaname;
    private String lvlknights;
    private String lvlpaladins;
    private String lvlmages;
    private String skknights;
    private String skpaladins;
    private String skmages;
    private String defknights;
    private String defpaladins;
    private String defmages;

    @JsonBackReference
    private HuntingPlace huntingPlace;
}
