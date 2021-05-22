package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate

data class Charm(
    val type: Type,
    val cost: Int, // number of charm points
    val effect: String,
) : WikiObject() {
    enum class Type {
        Offensive, Defensive, Passive
    }

    override fun fieldOrder(): MutableList<String> {
        return mutableListOf(
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
