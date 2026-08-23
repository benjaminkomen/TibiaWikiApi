package com.tibiawiki.domain.objects.validation

import java.io.Serializable

data class ValidationResult(
    val severity: ValidationSeverity? = null,
    val description: String? = null
) : Serializable
