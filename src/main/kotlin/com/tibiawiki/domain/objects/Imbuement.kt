package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status

data class Imbuement(
    override val name: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val prefix: String? = null,
    val type: String? = null,
    val imbuement: String? = null,
    val category: String? = null,
    val effect: String? = null,
    val amount: String? = null,
    val effectpercent: String? = null,
    val slot: String? = null
) : WikiObject(
    name = name,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.IMBUEMENT.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "prefix", "type", "imbuement", "category", "effect", "amount",
            "effectpercent", "slot", "implemented", "notes", "history", "status"
        )
    }
}
