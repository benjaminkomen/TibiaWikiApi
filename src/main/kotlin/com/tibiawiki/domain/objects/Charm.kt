package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate

data class Charm(
    val type: Type,
    val cost: String, // charm points; wiki uses a single int or tiered "100 / 150 / 225"
    val effect: String,
) : WikiObject() {
    enum class Type {
        Minor,
        Major
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name",
            "actualname",
            "type",
            "cost",
            "effect",
            "implemented",
            "notes",
            "history",
            "status"
        )
    }

    override fun getTemplateType(): String {
        return InfoboxTemplate.CHARM.templateName
    }
}
