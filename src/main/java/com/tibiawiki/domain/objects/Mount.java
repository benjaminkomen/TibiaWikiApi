package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"objectType"})
@Getter
@NoArgsConstructor
public class Mount extends WikiObject {

    private Integer speed;
    private String tamingMethod;
    private YesNo bought;
    private Integer price; // unit is Tibia Coins
    private String achievement; // this could link to Achievement
    private Integer lightradius;
    private Integer lightcolor;
    private String artwork;

    @JsonGetter("taming_method")
    private String getTamingMethod() {
        return tamingMethod;
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "speed", "tamingMethod", "bought", "price", "achievement", "lightcolor",
                "lightradius", "implemented", "artwork", "notes", "history", "status");
    }
}