package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.tibiawiki.domain.enums.Article;
import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.enums.YesNo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class Corpse extends WikiObject {

    private final String flavortext;
    private final YesNo skinable;
    private final String product;
    private final String liquid;
    private final Integer stages;
    private final String firstDecaytime; // FIXME should be Seconds
    private final String secondDecaytime; // FIXME should be Seconds
    private final String thirdDecaytime; // FIXME should be Seconds
    private final Integer firstVolume;
    private final Integer secondVolume;
    private final Integer thirdVolume;
    private final BigDecimal firstWeight;
    private final BigDecimal secondWeight;
    private final BigDecimal thirdWeight;
    private final String corpseof;
    private final String sellto;

    private Corpse() {
        this.flavortext = null;
        this.skinable = null;
        this.product = null;
        this.liquid = null;
        this.stages = null;
        this.firstDecaytime = null;
        this.secondDecaytime = null;
        this.thirdDecaytime = null;
        this.firstVolume = null;
        this.secondVolume = null;
        this.thirdVolume = null;
        this.firstWeight = null;
        this.secondWeight = null;
        this.thirdWeight = null;
        this.corpseof = null;
        this.sellto = null;
    }

    @Builder
    private Corpse(String name, Article article, String actualname, String implemented, String notes, String history,
                   Status status, String flavortext, YesNo skinable, String product, String liquid, Integer stages,
                   String firstDecaytime, String secondDecaytime, String thirdDecaytime, Integer firstVolume,
                   Integer secondVolume, Integer thirdVolume, BigDecimal firstWeight, BigDecimal secondWeight, BigDecimal thirdWeight,
                   String corpseof, String sellto) {
        super(name, article, actualname, null, implemented, notes, history, status);
        this.flavortext = flavortext;
        this.skinable = skinable;
        this.product = product;
        this.liquid = liquid;
        this.stages = stages;
        this.firstDecaytime = firstDecaytime;
        this.secondDecaytime = secondDecaytime;
        this.thirdDecaytime = thirdDecaytime;
        this.firstVolume = firstVolume;
        this.secondVolume = secondVolume;
        this.thirdVolume = thirdVolume;
        this.firstWeight = firstWeight;
        this.secondWeight = secondWeight;
        this.thirdWeight = thirdWeight;
        this.corpseof = corpseof;
        this.sellto = sellto;
    }

    @JsonGetter("1decaytime")
    public String getFirstDecaytime() {
        return firstDecaytime;
    }

    @JsonGetter("2decaytime")
    public String getSecondDecaytime() {
        return secondDecaytime;
    }

    @JsonGetter("3decaytime")
    public String getThirdDecaytime() {
        return thirdDecaytime;
    }

    @JsonGetter("1volume")
    public Integer getFirstVolume() {
        return firstVolume;
    }

    @JsonGetter("2volume")
    public Integer getSecondVolume() {
        return secondVolume;
    }

    @JsonGetter("3volume")
    public Integer getThirdVolume() {
        return thirdVolume;
    }

    @JsonGetter("1weight")
    public BigDecimal getFirstWeight() {
        return firstWeight;
    }

    @JsonGetter("2weight")
    public BigDecimal getSecondWeight() {
        return secondWeight;
    }

    @JsonGetter("3weight")
    public BigDecimal getThirdWeight() {
        return thirdWeight;
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("name", "article", "actualname", "flavortext", "skinable", "product", "liquid", "stages",
                "1decaytime", "2decaytime", "3decaytime", "1volume", "2volume", "3volume", "1weight", "2weight",
                "3weight", "corpseof", "sellto", "notes", "implemented", "history", "status");
    }
}