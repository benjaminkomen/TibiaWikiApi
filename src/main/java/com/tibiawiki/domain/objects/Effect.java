package com.tibiawiki.domain.objects;

import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.enums.Status;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class Effect extends WikiObject {

    private final Integer effectid;
    private final String primarytype;
    private final String secondarytype;
    private final Integer lightradius;
    private final Integer lightcolor;
    private final String causes;
    @SuppressWarnings("squid:S1700") // class and field name are the same, but that's understandable
    private final String effect;

    private Effect() {
        this.effectid = null;
        this.primarytype = null;
        this.secondarytype = null;
        this.lightradius = null;
        this.lightcolor = null;
        this.causes = null;
        this.effect = null;
    }

    @SuppressWarnings("squid:S00107")
    @Builder
    private Effect(String name, String implemented, String notes, String history, Status status, Integer effectid,
                   String primarytype, String secondarytype, Integer lightradius, Integer lightcolor, String causes,
                   String effect) {
        super(name, null, null, null, implemented, notes, history, status);
        this.effectid = effectid;
        this.primarytype = primarytype;
        this.secondarytype = secondarytype;
        this.lightradius = lightradius;
        this.lightcolor = lightcolor;
        this.causes = causes;
        this.effect = effect;
    }

    @Override
    public String getTemplateType() {
        return InfoboxTemplate.EFFECT.getTemplateName();
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "implemented", "effectid", "primarytype", "secondarytype", "lightradius", "lightcolor",
                "causes", "effect", "notes", "history", "status");
    }
}