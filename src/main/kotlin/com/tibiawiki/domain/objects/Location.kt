package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.enums.YesNo

data class Location(
    override val name: String? = null,
    override val implemented: String? = null,
    override val status: Status? = null,
    val ruler: String? = null,
    val population: String? = null,
    val near: String? = null,
    val organization: String? = null,
    val map: String? = null,
    val map2: String? = null,
    val map3: String? = null,
    val map4: String? = null,
    val map5: String? = null,
    val map6: String? = null,
    val links: YesNo? = null
) : WikiObject(
    name = name,
    implemented = implemented,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.GEOGRAPHY.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "implemented", "ruler", "population", "near", "organization", "map", "map2",
            "map3", "map4", "map5", "map6", "links", "status"
        )
    }
}
