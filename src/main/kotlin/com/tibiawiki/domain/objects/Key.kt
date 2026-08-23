package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.KeyType
import com.tibiawiki.domain.enums.Status

data class Key(
    override val implemented: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val number: String? = null,
    val aka: String? = null,
    val primarytype: KeyType? = null,
    val secondarytype: KeyType? = null,
    val location: String? = null,
    val value: String? = null,
    val npcvalue: Int? = null,
    val npcprice: Int? = null,
    val buyfrom: String? = null,
    val sellto: String? = null,
    val origin: String? = null,
    val shortnotes: String? = null,
    val longnotes: String? = null
) : WikiObject(
    implemented = implemented,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.KEY.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "number", "implemented", "aka", "primarytype", "secondarytype", "location", "value",
            "npcvalue", "npcprice", "buyfrom", "sellto", "origin", "shortnotes", "longnotes", "history", "status"
        )
    }
}
