package com.tibiawiki.domain.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tibiawiki.domain.enums.BookType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties({"templateType"})
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class Book extends WikiObject {

    private BookType booktype;
    private BookType booktype2;
    private BookType booktype3;
    private BookType booktype4;
    private BookType booktype5;
    private BookType booktype6;
    private BookType booktype7;
    private BookType booktype8;
    private String title;
    private String pagename;
    private String location;
    private String location2;
    private String location3;
    private String location4;
    private String location5;
    private String location6;
    private String location7;
    private String location8;
    private String blurb;
    private String author;
    private String returnpage;
    private String returnpage2;
    private String returnpage3;
    private String returnpage4;
    private String returnpage5;
    private String returnpage6;
    private String returnpage7;
    private String returnpage8;
    private String returnpage9;
    private String returnpage10;
    private String returnpage11;
    private String returnpage12;
    private String returnpage13;
    private String returnpage14;
    private String returnpage15;
    private String returnpage16;
    private String prevbook;
    private String nextbook;
    private String relatedpages;
    private String text;
    private String text2;
    private String text3;
    private String text4;
    private String text5;
    private String text6;
    private String text7;
    private String text8;
    private String implemented2;
    private String implemented3;
    private String implemented4;
    private String implemented5;
    private String implemented6;
    private String implemented7;
    private String implemented8;

    @Override
    public List<String> fieldOrder() {
        return Collections.emptyList();
    }
}