package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.enums.YesNo
import com.tibiawiki.domain.objects.validation.ValidationResult
import com.tibiawiki.domain.utils.concatenate

data class Achievement(
    override val name: String? = null,
    override val actualname: String? = null,
    override val implemented: String? = null,
    override val history: String? = null,
    override val status: Status? = null,
    val grade: Int? = null,
    val description: String? = null,
    val spoiler: String? = null,
    val premium: YesNo? = null,
    val points: Int? = null,
    val secret: YesNo? = null,
    val coincideswith: Int? = null,
    val achievementid: Int? = null,
    val relatedpages: String? = null
) : WikiObject(
    name = name,
    actualname = actualname,
    implemented = implemented,
    history = history,
    status = status
) {
    override fun fieldOrder(): List<String> {
        return listOf(
            "grade",
            "name",
            "actualname",
            "description",
            "spoiler",
            "premium",
            "points",
            "secret",
            "coincideswith",
            "implemented",
            "achievementid",
            "relatedpages",
            "history",
            "status"
        )
    }

    override fun getTemplateType(): String {
        return InfoboxTemplate.ACHIEVEMENT.templateName
    }

    override fun validate(): List<ValidationResult> {
        return concatenate(super.validate(), emptyList())
    }
}
