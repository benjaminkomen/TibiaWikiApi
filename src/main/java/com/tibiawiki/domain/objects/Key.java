package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tibiawiki.domain.enums.KeyType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"objectType"})
@Getter
@NoArgsConstructor
public class Key extends WikiObject {

    private String number;
    private String aka;
    private KeyType primarytype;
    private KeyType secondarytype;
    private String location;
    private String value;
    private Integer npcvalue;
    private Integer npcprice;
    private String buyfrom;
    private String sellto;
    private String origin;
    private String shortnotes;
    private String longnotes;

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("number", "implemented", "aka", "primarytype", "secondarytype", "location", "value",
                "npcvalue", "npcprice", "buyfrom", "sellto", "origin", "shortnotes", "longnotes", "history", "status");
    }
}