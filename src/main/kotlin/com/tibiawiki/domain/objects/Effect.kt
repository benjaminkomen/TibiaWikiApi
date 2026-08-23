package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status

data class Effect(
    override val name: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val effectid: List<Int>? = null,
    val primarytype: String? = null,
    val secondarytype: String? = null,
    val lightradius: Int? = null,
    val lightcolor: Int? = null,
    val causes: String? = null,
    val effect: String? = null
) : WikiObject(
    name = name,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.EFFECT.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "implemented", "effectid", "primarytype", "secondarytype", "lightradius", "lightcolor",
            "causes", "effect", "notes", "history", "status"
        )
    }
}
