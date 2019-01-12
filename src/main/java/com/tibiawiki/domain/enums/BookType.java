package com.tibiawiki.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tibiawiki.domain.interfaces.Description;

public enum BookType implements Description {

    BOOK_ATLAS("Book (Atlas)"),
    BOOK_BLACK("Book (Black)"),
    BOOK_BLUE("Book (Blue)"),
    BOOK_BROWN("Book (Brown)"),
    BOOK_BROWN_SQUARE("Book (Brown Square)"),
    BOOK_BROWN_THIN("Book (Brown Thin)"),
    BOOK_DRACONIA("Book (Draconia)"),
    BOOK_FAT_GREEN("Book (Fat Green)"),
    BOOK_GEMMED("Book (Gemmed)"),
    BOOK_GREEN("Book (Green)"),
    BOOK_GREY("Book (Grey)"),
    BOOK_ORANGE("Book (Orange)"),
    BOOK_RED("Book (Red)"),
    BOTANY_ALMANACH("Botany Almanach"),
    BLURRED_TRANSCRIPT("Blurred Transcript"),
    DOCUMENT("Document"),
    DOCUMENT_CERTIFICATE("Document (Certificate)"),
    JEAN_PIERRES_COOKBOOK_I("Jean Pierre's Cookbook I"),
    JEAN_PIERRES_COOKBOOK_II("Jean Pierre's Cookbook II"),
    HASTILY_SCRIBBLED_NOTE("Hastily Scribbled Note"),
    LARGE_BOOK("Large Book"),
    MAP_BROWN("Map (Brown)"),
    MAP_COLOUR("Map (Colour)"),
    MONKS_DIARY("Monk's Diary"),
    NOTES_OF_A_POACHER("Notes of a Poacher"),
    OLD_PARCHMENT_BROWN("Old Parchment (Brown)"),
    OLD_PIECE_OF_PAPER("Old Piece of Paper"),
    OLD_PIRATE_POEM("Old Pirate Poem"),
    OLD_TOME("Old Tome"),
    ORNATE_TOME("Ornate Tome"),
    PAPER("Paper"),
    PIECE_OF_PAPER("Piece of Paper"),
    PIECE_OF_PAPER_BLANK("Piece of Paper (Blank)"),
    PARCHMENT("Parchment"),
    PARCHMENT_QUESTIONNAIRE("Parchment (Questionnaire)"),
    PARCHMENT_REWRITABLE("Parchment (Rewritable)"),
    PARCHMENT_YELLOW("Parchment (Yellow)"),
    PARCHMENT_WHITE("Parchment (White)"),
    SCROLL("Scroll"),
    SCROLL_BROWN("Scroll (Brown)"),
    SEASHELL_BOOK_BLUE("Seashell Book (Library Blue)"),
    SEASHELL_BOOK_GREEN("Seashell Book (Library Green)"),
    SEASHELL_BOOK_YELLOW("Seashell Book (Library Yellow)"),
    SHEET_OF_PAPER("Sheet of Paper"),
    STAMPED_LETTER("Stamped Letter"),
    THEATRE_SCRIPT("Theatre Script"),
    THE_HOLY_TIBLE("The Holy Tible"),
    THE_PEACOCK_BALLAD("The Peacock Ballad"),
    TORN_BOOK("Torn Book"),
    TORN_BOOK_DIARY("Torn Book (Diary)"),
    WRINKLED_PARCHMENT("Wrinkled Parchment"),
    YOUR_STUDENT_BOOK("Your Student Book"),
    ;

    private String description;

    BookType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
