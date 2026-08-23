package com.tibiawiki.adapters.rest

import com.tibiawiki.domain.objects.validation.ValidationResult

data class ValidationErrorResponse(
    val message: String,
    val validationResults: List<ValidationResult>
)
