package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.BuildingType
import com.tibiawiki.domain.enums.City
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.enums.YesNo

data class Building(
    override val name: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val type: BuildingType? = null,
    val location: String? = null,
    val posx: String? = null,
    val posy: String? = null,
    val posz: String? = null,
    val street: String? = null,
    val street2: String? = null,
    val street3: String? = null,
    val street4: String? = null,
    val street5: String? = null,
    val houseid: Int? = null,
    val size: Int? = null,
    val beds: Int? = null,
    val rent: Int? = null,
    val ownable: YesNo? = null,
    val city: City? = null,
    val openwindows: Int? = null,
    val floors: Int? = null,
    val rooms: Int? = null,
    val furnishings: String? = null,
    val image: String? = null
) : WikiObject(
    name = name,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.BUILDING.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "implemented", "type", "location", "posx", "posy", "posz", "street", "street2",
            "street3", "street4", "street5", "houseid", "size", "beds", "rent", "ownable", "city", "openwindows",
            "floors", "rooms", "furnishings", "notes", "history", "image", "status"
        )
    }
}
