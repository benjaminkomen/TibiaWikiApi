package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status

data class Update(
    override val name: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val date: String? = null
) : WikiObject(
    name = name,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.UPDATE.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "date", "implemented", "notes", "history", "status"
        )
    }
}
