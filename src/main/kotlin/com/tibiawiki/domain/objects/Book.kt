package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.BookType
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status

data class Book(
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val booktype: BookType? = null,
    val booktype2: BookType? = null,
    val booktype3: BookType? = null,
    val booktype4: BookType? = null,
    val booktype5: BookType? = null,
    val booktype6: BookType? = null,
    val booktype7: BookType? = null,
    val booktype8: BookType? = null,
    val title: String? = null,
    val pagename: String? = null,
    val location: String? = null,
    val location2: String? = null,
    val location3: String? = null,
    val location4: String? = null,
    val location5: String? = null,
    val location6: String? = null,
    val location7: String? = null,
    val location8: String? = null,
    val blurb: String? = null,
    val author: String? = null,
    val returnpage: String? = null,
    val returnpage2: String? = null,
    val returnpage3: String? = null,
    val returnpage4: String? = null,
    val returnpage5: String? = null,
    val returnpage6: String? = null,
    val returnpage7: String? = null,
    val returnpage8: String? = null,
    val returnpage9: String? = null,
    val returnpage10: String? = null,
    val returnpage11: String? = null,
    val returnpage12: String? = null,
    val returnpage13: String? = null,
    val returnpage14: String? = null,
    val returnpage15: String? = null,
    val returnpage16: String? = null,
    val prevbook: String? = null,
    val nextbook: String? = null,
    val relatedpages: String? = null,
    val text: String? = null,
    val text2: String? = null,
    val text3: String? = null,
    val text4: String? = null,
    val text5: String? = null,
    val text6: String? = null,
    val text7: String? = null,
    val text8: String? = null,
    val implemented2: String? = null,
    val implemented3: String? = null,
    val implemented4: String? = null,
    val implemented5: String? = null,
    val implemented6: String? = null,
    val implemented7: String? = null,
    val implemented8: String? = null
) : WikiObject(
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override val name: String?
        get() = pagename

    override fun getTemplateType(): String {
        return InfoboxTemplate.BOOK.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "booktype", "booktype2", "booktype3", "booktype4", "booktype5", "booktype6", "booktype7",
            "booktype8", "title", "pagename", "location", "location2", "location3", "location4", "location5",
            "location6", "location7", "location8", "blurb", "author", "returnpage", "returnpage2", "returnpage3",
            "returnpage4", "returnpage5", "returnpage6", "returnpage7", "returnpage8", "returnpage9",
            "returnpage10", "returnpage11", "returnpage12", "returnpage13", "returnpage14", "returnpage15",
            "returnpage16", "prevbook", "nextbook", "relatedpages", "notes", "text", "text2", "text3", "text4",
            "text5", "text6", "text7", "text8", "implemented", "implemented2", "implemented3", "implemented4",
            "implemented5", "implemented6", "implemented7", "implemented8", "history", "status"
        )
    }
}
