package com.tibiawiki.domain.objects;

import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.enums.YesNo;
import com.tibiawiki.domain.objects.validation.ValidationResult;
import com.tibiawiki.domain.utils.ListUtil;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Component
public class Achievement extends WikiObject {

    private final Integer grade;
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

    @SuppressWarnings("squid:S00107")
    @Builder
    public Achievement(String name, String actualname, String implemented, String history, Status status, Integer grade,
                       String description, String spoiler, YesNo premium, Integer points, YesNo secret, Integer coincideswith,
                       Integer achievementid, String relatedpages) {
        super(name, null, actualname, null, implemented, null, history, status);
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
    public String getTemplateType() {
        return InfoboxTemplate.ACHIEVEMENT.getTemplateName();
    }

    @Override
    public List<ValidationResult> validate() {
        return ListUtil.concatenate(super.validate(), Collections.emptyList());
    }
}