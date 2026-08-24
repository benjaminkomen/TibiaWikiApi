package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status

data class Charm(
    override val name: String? = null,
    override val actualname: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val type: Type? = null,
    val cost: String? = null, // charm points; wiki uses a single int or tiered "100 / 150 / 225"
    val effect: String? = null,
) : WikiObject(
    name = name,
    actualname = actualname,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    enum class Type {
        Minor,
        Major
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name",
            "actualname",
            "type",
            "cost",
            "effect",
            "implemented",
            "notes",
            "history",
            "status"
        )
    }

    override fun getTemplateType(): String {
        return InfoboxTemplate.CHARM.templateName
    }
}
