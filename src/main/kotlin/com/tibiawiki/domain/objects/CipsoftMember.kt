package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status

data class CipsoftMember(
    override val name: String? = null,
    override val actualname: String? = null,
    override val implemented: String? = null,
    override val notes: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val job: String? = null
) : WikiObject(
    name = name,
    actualname = actualname,
    implemented = implemented,
    notes = notes,
    history = history,
    status = status
) {
    override fun getTemplateType(): String {
        return InfoboxTemplate.CIPSOFT_MEMBER.templateName
    }

    override fun fieldOrder(): List<String> {
        return listOf(
            "name", "actualname", "job", "implemented", "notes", "history", "status"
        )
    }
}
