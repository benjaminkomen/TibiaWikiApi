package com.tibiawiki.domain.interfaces

import com.tibiawiki.domain.objects.validation.ValidationResult

interface Validatable {
    fun validate(): List<ValidationResult>
}
