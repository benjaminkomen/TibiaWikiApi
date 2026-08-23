package com.tibiawiki.domain.interfaces

import com.tibiawiki.domain.objects.validation.ValidationResult

fun interface Validatable {
    fun validate(): List<ValidationResult>
}
