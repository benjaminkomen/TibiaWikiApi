package com.tibiawiki.domain.objects

import com.fasterxml.jackson.annotation.JsonProperty
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.enums.YesNo

data class Outfit(
    override val name: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val primarytype: String? = null,
    val secondarytype: String? = null,
    val premium: YesNo? = null,
    val outfit: String? = null,
    val addons: String? = null,
    val bought: YesNo? = null,
    val store: String? = null,
    val fulloutfitprice: Int? = null,
    val achievement: String? = null,
    @JsonProperty("male_id") val maleId: Int? = null,
    @JsonProperty("female_id") val femaleId: Int? = null,
    val lightradius: Int? = null,
    val lightcolor: Int? = null,
    val artwork: String? = null,
    val artwork2: String? = null,
    val artwork3: String? = null,
    val labels: String? = null
) : WikiObject(
    name = name,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.OUTFIT.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name",
            "primarytype",
            "secondarytype",
            "premium",
            "outfit",
            "addons",
            "bought",
            "store",
            "fulloutfitprice",
            "achievement",
            "male_id",
            "female_id",
            "lightcolor",
            "lightradius",
            "implemented",
            "artwork",
            "artwork2",
            "artwork3",
            "labels",
            "notes",
            "history",
            "status"
        )
    }
}
