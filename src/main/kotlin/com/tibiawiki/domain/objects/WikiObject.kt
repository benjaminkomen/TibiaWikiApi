package com.tibiawiki.domain.objects

import com.fasterxml.jackson.annotation.JsonIgnore
import com.tibiawiki.domain.enums.Article
import com.tibiawiki.domain.enums.Status
import com.tibiawiki.domain.interfaces.Validatable
import com.tibiawiki.domain.objects.validation.ValidationResult

abstract class WikiObject @JvmOverloads constructor(
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

    override fun validate(): List<ValidationResult> {
        return emptyList()
    }

    class WikiObjectImpl : WikiObject() {
        override fun fieldOrder(): List<String> {
            return listOf("name", "article", "actualname", "plural", "implemented", "notes", "history", "status")
        }

        override fun getTemplateType(): String {
            return "WikiObjectImpl"
        }

        override fun validate(): List<ValidationResult> {
            return emptyList()
        }
    }
}
