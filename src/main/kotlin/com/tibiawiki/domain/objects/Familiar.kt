package com.tibiawiki.domain.objects

import com.fasterxml.jackson.annotation.JsonProperty
import com.tibiawiki.domain.enums.Article
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.enums.Vocation
import com.tibiawiki.domain.enums.YesNo

data class Familiar(
    override val name: String? = null,
    override val article: Article? = null,
    override val actualname: String? = null,
    override val plural: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val voc: String? = null,
    val type: String? = null,
    val words: String? = null,
    val mana: Int? = null,
    val cooldown: Int? = null,
    val premium: YesNo? = null
) : WikiObject(
    name = name,
    article = article,
    actualname = actualname,
    plural = plural,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    /**
     * Typed vocations parsed from wiki `voc` text. Not part of [fieldOrder];
     * write-back keeps the original `voc` string.
     */
    @get:JsonProperty("vocations")
    val vocations: List<Vocation>
        get() = Vocation.parseVoc(voc)

    override fun getTemplateType(): String {
        return InfoboxTemplate.FAMILIAR.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "article", "actualname", "plural", "voc", "type", "words", "mana",
            "cooldown", "premium", "implemented", "notes", "history", "status"
        )
    }
}
