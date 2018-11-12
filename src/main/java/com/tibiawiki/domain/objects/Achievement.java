package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tibiawiki.domain.enums.Grade;
import com.tibiawiki.domain.enums.YesNo;
import com.tibiawiki.domain.interfaces.Validatable;
import com.tibiawiki.domain.objects.validation.ValidationResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties({"templateType"})
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class Achievement extends WikiObject implements Validatable {

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

    @Override
    public List<ValidationResult> validate() {
        return Collections.emptyList();
    }
}