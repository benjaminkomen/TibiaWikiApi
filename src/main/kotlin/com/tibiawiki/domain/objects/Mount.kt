package com.tibiawiki.domain.objects

import com.fasterxml.jackson.annotation.JsonProperty
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.enums.YesNo

data class Mount(
    override val name: String? = null,
    override val actualname: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val speed: Int? = null,
    @JsonProperty("taming_method") val tamingMethod: String? = null,
    val bought: YesNo? = null,
    val tournament: YesNo? = null,
    val price: Int? = null,
    val pricecurrency: String? = null,
    val colourisable: YesNo? = null,
    @JsonProperty("mount_id") val mountId: Int? = null,
    val achievement: String? = null,
    val lightradius: Int? = null,
    val lightcolor: Int? = null,
    val artwork: String? = null
) : WikiObject(
    name = name,
    actualname = actualname,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.MOUNT.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name",
            "actualname",
            "speed",
            "taming_method",
            "bought",
            "tournament",
            "price",
            "pricecurrency",
            "colourisable",
            "mount_id",
            "achievement",
            "lightcolor",
            "lightradius",
            "implemented",
            "artwork",
            "notes",
            "history",
            "status"
        )
    }
}
