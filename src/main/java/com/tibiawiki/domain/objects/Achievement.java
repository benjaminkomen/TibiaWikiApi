package com.tibiawiki.domain.objects;

import com.tibiawiki.domain.enums.Grade;
import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.enums.YesNo;
import com.tibiawiki.domain.interfaces.Validatable;
import com.tibiawiki.domain.objects.validation.ValidationResult;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Component
public class Achievement extends WikiObject implements Validatable {

    private final Grade grade;
    private final String description;
    private final String spoiler;
    private final YesNo premium;
    private final Integer points;
    private final YesNo secret;
    private final Integer coincideswith;
    private final Integer achievementid;
    private final String relatedpages;

    private Achievement() {
        this.grade = null;
        this.description = null;
        this.spoiler = null;
        this.premium = null;
        this.points = null;
        this.secret = null;
        this.coincideswith = null;
        this.achievementid = null;
        this.relatedpages = null;
    }

    @Builder
    public Achievement(String name, String implemented, String history, Status status, Grade grade, String description,
                       String spoiler, YesNo premium, Integer points, YesNo secret, Integer coincideswith,
                       Integer achievementid, String relatedpages) {
        super(name, null, null, null, implemented, null, history, status);
        this.grade = grade;
        this.description = description;
        this.spoiler = spoiler;
        this.premium = premium;
        this.points = points;
        this.secret = secret;
        this.coincideswith = coincideswith;
        this.achievementid = achievementid;
        this.relatedpages = relatedpages;
    }

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