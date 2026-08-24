package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status

data class World(
    override val name: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val location: String? = null,
    val pvpType: String? = null
) : WikiObject(
    name = name,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.WORLD.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "location", "pvpType", "implemented", "notes", "history", "status"
        )
    }
}
