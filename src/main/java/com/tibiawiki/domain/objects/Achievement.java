package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tibiawiki.domain.enums.Grade;
import com.tibiawiki.domain.enums.YesNo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"objectType"})
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Achievement extends WikiObject {

    private Grade grade;
    private String description;
    private String spoiler;
    private YesNo premium;
    private Integer points;
    private YesNo secret;
    private Integer coincideswith;
    private Integer achievementid;
    private String relatedpages;

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("grade", "name", "description", "spoiler", "premium", "points", "secret", "coincideswith",
                "implemented", "achievementid", "relatedpages", "history", "status");
    }
}