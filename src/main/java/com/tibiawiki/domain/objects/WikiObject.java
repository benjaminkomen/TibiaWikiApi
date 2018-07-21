package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tibiawiki.domain.enums.Article;
import com.tibiawiki.domain.enums.Status;
import com.tibiawiki.domain.interfaces.Description;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public abstract class WikiObject {

    private String name;
    private Article article;
    private String actualname;
    private String plural;
    private String implemented;
    private String notes;
    private String history;
    private Status status;

    protected WikiObject() {
        // no-args constructor
    }

    public abstract List<String> fieldOrder();

    @JsonIgnore
    public List<String> getFieldNames() {
        List<String> existingFieldNames = getFields().stream()
                .map(Field::getName)
                .collect(Collectors.toList());

        return fieldOrder().stream()
                .filter(existingFieldNames::contains)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Field> getFields() {
        List<Field> allFields = new ArrayList<>();

        for (Class<?> c = this.getClass(); c != null; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();
            allFields.addAll(Arrays.asList(fields));
        }

        return allFields.stream()
                .filter(this::fieldHasValue)
                .collect(Collectors.toList());
    }

    public int maxFieldSize() {
        return getFieldNames().stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);
    }

    @JsonIgnore
    public Object getValue(String fieldName) {
        return getFields().stream()
                .filter(this::fieldHasValue)
                .filter(f -> f.getName().equals(fieldName))
                .map(f -> {
                    try {
                        Object fieldValue = f.get(this);

                        if (fieldValue instanceof Description) {
                            return ((Description) fieldValue).getDescription();
                        } else {
                            return fieldValue;
                        }
                    } catch (IllegalAccessException e) {
                        throw new FieldAccessDeniedException(e);
                    }
                })
                .findAny()
                .orElse(null);
    }

    private boolean fieldHasValue(Field f) {
        try {
            f.setAccessible(true);
            return f.get(this) != null;
        } catch (IllegalAccessException e) {
            throw new FieldAccessDeniedException(e);
        }
    }

    @JsonIgnore
    public String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return "Class: " + getClassName() + ", name: " + getName();
    }

    public static class WikiObjectImpl extends WikiObject {

        public WikiObjectImpl() {
            super();
        }

        @Override
        public List<String> fieldOrder() {
            return Arrays.asList("name", "article", "actualname", "plural", "implemented", "notes", "history", "status");
        }
    }

    public static class FieldAccessDeniedException extends RuntimeException {

        public FieldAccessDeniedException(Exception e) {
            super(e);
        }
    }
}
