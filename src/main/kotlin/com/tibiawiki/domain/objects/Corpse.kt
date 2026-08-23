package com.tibiawiki.domain.objects

import com.fasterxml.jackson.annotation.JsonProperty
import com.tibiawiki.domain.enums.Article
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.enums.YesNo
import java.math.BigDecimal

data class Corpse(
    override val name: String? = null,
    override val article: Article? = null,
    override val actualname: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val flavortext: String? = null,
    val skinable: YesNo? = null,
    val product: String? = null,
    val liquid: String? = null,
    val stages: Int? = null,
    @JsonProperty("1decaytime") val firstDecaytime: String? = null,
    @JsonProperty("2decaytime") val secondDecaytime: String? = null,
    @JsonProperty("3decaytime") val thirdDecaytime: String? = null,
    @JsonProperty("1volume") val firstVolume: Int? = null,
    @JsonProperty("2volume") val secondVolume: Int? = null,
    @JsonProperty("3volume") val thirdVolume: Int? = null,
    @JsonProperty("1weight") val firstWeight: BigDecimal? = null,
    @JsonProperty("2weight") val secondWeight: BigDecimal? = null,
    @JsonProperty("3weight") val thirdWeight: BigDecimal? = null,
    val corpseof: String? = null,
    val sellto: String? = null
) : WikiObject(
    name = name,
    article = article,
    actualname = actualname,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.CORPSE.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "article", "actualname", "flavortext", "skinable", "product", "liquid", "stages",
            "1decaytime", "2decaytime", "3decaytime", "1volume", "2volume", "3volume", "1weight", "2weight",
            "3weight", "corpseof", "sellto", "notes", "implemented", "history", "status"
        )
    }
}
