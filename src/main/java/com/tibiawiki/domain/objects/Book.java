package com.tibiawiki.domain.objects;

import com.tibiawiki.domain.enums.BookType;
import com.tibiawiki.domain.enums.InfoboxTemplate;
import com.tibiawiki.domain.enums.Status;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class Book extends WikiObject {

    private final BookType booktype;
    private final BookType booktype2;
    private final BookType booktype3;
    private final BookType booktype4;
    private final BookType booktype5;
    private final BookType booktype6;
    private final BookType booktype7;
    private final BookType booktype8;
    private final String title;
    private final String pagename;
    private final String location;
    private final String location2;
    private final String location3;
    private final String location4;
    private final String location5;
    private final String location6;
    private final String location7;
    private final String location8;
    private final String blurb;
    private final String author;
    private final String returnpage;
    private final String returnpage2;
    private final String returnpage3;
    private final String returnpage4;
    private final String returnpage5;
    private final String returnpage6;
    private final String returnpage7;
    private final String returnpage8;
    private final String returnpage9;
    private final String returnpage10;
    private final String returnpage11;
    private final String returnpage12;
    private final String returnpage13;
    private final String returnpage14;
    private final String returnpage15;
    private final String returnpage16;
    private final String prevbook;
    private final String nextbook;
    private final String relatedpages;
    private final String text;
    private final String text2;
    private final String text3;
    private final String text4;
    private final String text5;
    private final String text6;
    private final String text7;
    private final String text8;
    private final String implemented2;
    private final String implemented3;
    private final String implemented4;
    private final String implemented5;
    private final String implemented6;
    private final String implemented7;
    private final String implemented8;

    private Book() {
        this.booktype = null;
        this.booktype2 = null;
        this.booktype3 = null;
        this.booktype4 = null;
        this.booktype5 = null;
        this.booktype6 = null;
        this.booktype7 = null;
        this.booktype8 = null;
        this.title = null;
        this.pagename = null;
        this.location = null;
        this.location2 = null;
        this.location3 = null;
        this.location4 = null;
        this.location5 = null;
        this.location6 = null;
        this.location7 = null;
        this.location8 = null;
        this.blurb = null;
        this.author = null;
        this.returnpage = null;
        this.returnpage2 = null;
        this.returnpage3 = null;
        this.returnpage4 = null;
        this.returnpage5 = null;
        this.returnpage6 = null;
        this.returnpage7 = null;
        this.returnpage8 = null;
        this.returnpage9 = null;
        this.returnpage10 = null;
        this.returnpage11 = null;
        this.returnpage12 = null;
        this.returnpage13 = null;
        this.returnpage14 = null;
        this.returnpage15 = null;
        this.returnpage16 = null;
        this.prevbook = null;
        this.nextbook = null;
        this.relatedpages = null;
        this.text = null;
        this.text2 = null;
        this.text3 = null;
        this.text4 = null;
        this.text5 = null;
        this.text6 = null;
        this.text7 = null;
        this.text8 = null;
        this.implemented2 = null;
        this.implemented3 = null;
        this.implemented4 = null;
        this.implemented5 = null;
        this.implemented6 = null;
        this.implemented7 = null;
        this.implemented8 = null;
    }

    @SuppressWarnings("squid:S00107")
    @Builder
    public Book(String name, String implemented, String notes, String history, Status status, BookType booktype, String title,
                String pagename, String location, String blurb, String author, String returnpage, String prevbook,
                String nextbook, String relatedpages, String text) {
        super(name, null, null, null, implemented, notes, history, status);
        this.booktype = booktype;
        this.booktype2 = null;
        this.booktype3 = null;
        this.booktype4 = null;
        this.booktype5 = null;
        this.booktype6 = null;
        this.booktype7 = null;
        this.booktype8 = null;
        this.title = title;
        this.pagename = pagename;
        this.location = location;
        this.location2 = null;
        this.location3 = null;
        this.location4 = null;
        this.location5 = null;
        this.location6 = null;
        this.location7 = null;
        this.location8 = null;
        this.blurb = blurb;
        this.author = author;
        this.returnpage = returnpage;
        this.returnpage2 = null;
        this.returnpage3 = null;
        this.returnpage4 = null;
        this.returnpage5 = null;
        this.returnpage6 = null;
        this.returnpage7 = null;
        this.returnpage8 = null;
        this.returnpage9 = null;
        this.returnpage10 = null;
        this.returnpage11 = null;
        this.returnpage12 = null;
        this.returnpage13 = null;
        this.returnpage14 = null;
        this.returnpage15 = null;
        this.returnpage16 = null;
        this.prevbook = prevbook;
        this.nextbook = nextbook;
        this.relatedpages = relatedpages;
        this.text = text;
        this.text2 = null;
        this.text3 = null;
        this.text4 = null;
        this.text5 = null;
        this.text6 = null;
        this.text7 = null;
        this.text8 = null;
        this.implemented2 = null;
        this.implemented3 = null;
        this.implemented4 = null;
        this.implemented5 = null;
        this.implemented6 = null;
        this.implemented7 = null;
        this.implemented8 = null;
    }

    @Override
    public String getTemplateType() {
        return InfoboxTemplate.BOOK.getTemplateName();
    }

    @Override
    public String getName() {
        return getPagename();
    }

    @Override
    public List<String> fieldOrder() {
        return Arrays.asList("booktype", "booktype2", "booktype3", "booktype4", "booktype5", "booktype6", "booktype7",
                "booktype8", "title", "pagename", "location", "location2", "location3", "location4", "location5",
                "location6", "location7", "location8", "blurb", "author", "returnpage", "returnpage2", "returnpage3",
                "returnpage4", "returnpage5", "returnpage6", "returnpage7", "returnpage8", "returnpage9",
                "returnpage10", "returnpage11", "returnpage12", "returnpage13", "returnpage14", "returnpage15",
                "returnpage16", "prevbook", "nextbook", "relatedpages", "notes", "text", "text2", "text3", "text4",
                "text5", "text6", "text7", "text8", "implemented", "implemented2", "implemented3", "implemented4",
                "implemented5", "implemented6", "implemented7", "implemented8", "history", "status");
    }
}