package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status

data class Fansite(
    override val name: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val logo: String? = null,
    val url: String? = null,
    val language: String? = null,
    val type: String? = null,
    val fansiteitem: String? = null,
    val itemworth: String? = null
) : WikiObject(
    name = name,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.FANSITE.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "logo", "url", "language", "type", "implemented", "fansiteitem", "itemworth",
            "notes", "history", "status"
        )
    }
}
