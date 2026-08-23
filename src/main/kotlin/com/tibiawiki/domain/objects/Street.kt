package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.City
import com.tibiawiki.domain.enums.InfoboxTemplate

data class Street(
    override val name: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    val city: City? = null,
    val city2: City? = null,
    val map: String? = null,
    val floor: String? = null
) : WikiObject(
    name = name,
    implemented = implemented,
    notes = notes
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.STREET.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf("name", "implemented", "city", "city2", "map", "floor", "notes")
    }
}
