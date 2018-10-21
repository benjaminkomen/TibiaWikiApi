package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"templateType"})
@Getter
@NoArgsConstructor
public class Outfit extends WikiObject {

    private String primarytype;
    private String secondarytype;
    private YesNo premium;
    @SuppressWarnings("squid:S1700") // class and field name are the same, but that's understandable
    private String outfit;
    private String addons;
    private YesNo bought;
    private Integer fulloutfitprice;
    private String achievement;
    private String artwork;

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "primarytype", "secondarytype", "premium", "outfit", "addons", "bought",
                "fulloutfitprice", "achievement", "implemented", "artwork", "notes", "history", "status");
    }
}