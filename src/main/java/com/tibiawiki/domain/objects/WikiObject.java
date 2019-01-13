package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tibiawiki.domain.enums.Article;
import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.interfaces.Validatable;
import com.tibiawiki.domain.objects.validation.ValidationResult;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class WikiObject implements Validatable {

    private final String templateType;
    private final String name;
    private final Article article;
    private final String actualname;
    private final String plural;
    private final String implemented;
    private final String notes;
    private final String history;
    private final Status status;

    protected WikiObject() {
        templateType = null;
        name = null;
        article = null;
        actualname = null;
        plural = null;
        implemented = null;
        notes = null;
        history = null;
        status = null;
    }

    @SuppressWarnings("squid:S00107")
    public WikiObject(String name, Article article, String actualname, String plural, String implemented, String notes,
                      String history, Status status) {
        this.templateType = null;
        this.name = name;
        this.article = article;
        this.actualname = actualname;
        this.plural = plural;
        this.implemented = implemented;
        this.notes = notes;
        this.history = history;
        this.status = status;
    }

    public abstract List<String> fieldOrder();

    @JsonIgnore
    public String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return "Class: " + getClassName() + ", name: " + getName();
    }

    @Override
    public List<ValidationResult> validate() {
        return Collections.emptyList();
    }

    public static class WikiObjectImpl extends WikiObject {

        public WikiObjectImpl() {
            super();
        }

        @Override
        public List<String> fieldOrder() {
            return Arrays.asList("name", "article", "actualname", "plural", "implemented", "notes", "history", "status");
        }

        @Override
        public List<ValidationResult> validate() {
            return Collections.emptyList();
        }
    }
}
