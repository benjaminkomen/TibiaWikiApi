package com.tibiawiki.domain.objects

import com.fasterxml.jackson.annotation.JsonIgnore
import com.tibiawiki.domain.enums.Article
import com.tibiawiki.domain.enums.InfoboxTemplate
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.interfaces.Validatable
import com.tibiawiki.domain.objects.validation.ValidationResult
import com.tibiawiki.domain.objects.validation.ValidationSeverity

abstract class WikiObject(
    open val name: String? = null,
    open val article: Article? = null,
    open val actualname: String? = null,
    open val plural: String? = null,
    open val implemented: String? = null,
    open val notes: String? = null,
    open val history: String? = null,
    open val status: Status? = null
) : Validatable {

    abstract fun fieldOrder(): List<String>

    abstract fun getTemplateType(): String

    @get:JsonIgnore
    val className: String
        get() = this::class.simpleName.orEmpty()

    override fun toString(): String {
        return "Class: $className, name: $name"
    }

    /**
     * Wiki page title used for reads/writes. Defaults to [name]; types that key
     * off another field (e.g. Key number) override this.
     */
    @JsonIgnore
    open fun articleTitle(): String {
        return name?.trim().orEmpty()
    }

    override fun validate(): List<ValidationResult> {
        val results = mutableListOf<ValidationResult>()
        if (articleTitle().isBlank()) {
            results += ValidationResult(ValidationSeverity.ERROR, NAME_REQUIRED)
        }
        val templateType = getTemplateType()
        if (templateType !in KNOWN_TEMPLATE_TYPES) {
            results += ValidationResult(ValidationSeverity.ERROR, "$UNKNOWN_TEMPLATE_TYPE$templateType")
        }
        return results
    }

    class WikiObjectImpl : WikiObject() {
        override fun fieldOrder(): List<String> {
            return listOf("name", "article", "actualname", "plural", "implemented", "notes", "history", "status")
        }

        override fun getTemplateType(): String {
            return "WikiObjectImpl"
        }
    }

    companion object {
        const val NAME_REQUIRED = "name is required"
        const val UNKNOWN_TEMPLATE_TYPE = "unknown templateType: "
        val KNOWN_TEMPLATE_TYPES: Set<String> = InfoboxTemplate.entries.map { it.templateName }.toSet()
    }
}
