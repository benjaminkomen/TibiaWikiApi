package com.tibiawiki.domain.objects;

import com.tibiawiki.domain.enums.Status;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class Missile extends WikiObject {

    private final Integer missileid;
    private final String primarytype;
    private final String secondarytype;
    private final Integer lightradius;
    private final Integer lightcolor;
    private final String shotby;

    private Missile() {
        this.missileid = null;
        this.primarytype = null;
        this.secondarytype = null;
        this.lightradius = null;
        this.lightcolor = null;
        this.shotby = null;
    }

    @SuppressWarnings("squid:S00107")
    @Builder
    private Missile(String name, String implemented, String notes, String history, Status status, Integer missileid,
                    String primarytype, String secondarytype, Integer lightradius, Integer lightcolor, String shotby) {
        super(name, null, null, null, implemented, notes, history, status);
        this.missileid = missileid;
        this.primarytype = primarytype;
        this.secondarytype = secondarytype;
        this.lightradius = lightradius;
        this.lightcolor = lightcolor;
        this.shotby = shotby;
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "implemented", "missileid", "primarytype", "secondarytype", "lightradius", "lightcolor",
                "shotby", "notes", "history", "status");
    }
}