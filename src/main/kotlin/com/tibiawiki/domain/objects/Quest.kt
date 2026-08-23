package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.QuestType
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.enums.YesNo

data class Quest(
    override val name: String? = null,
    override val implemented: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val aka: String? = null,
    val reward: String? = null,
    val location: String? = null,
    val rookgaardquest: YesNo? = null,
    val type: QuestType? = null,
    val lvl: Int? = null,
    val lvlrec: Int? = null,
    val lvlnote: String? = null,
    val log: YesNo? = null,
    val time: String? = null,
    val timealloc: String? = null,
    val premium: YesNo? = null,
    val transcripts: YesNo? = null,
    val dangers: String? = null,
    val legend: String? = null
) : WikiObject(
    name = name,
    implemented = implemented,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.QUEST.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "aka", "reward", "location", "rookgaardquest", "type", "lvl", "lvlrec", "lvlnote",
            "log", "time", "timealloc", "premium", "transcripts", "dangers", "legend", "implemented", "history",
            "status"
        )
    }
}
