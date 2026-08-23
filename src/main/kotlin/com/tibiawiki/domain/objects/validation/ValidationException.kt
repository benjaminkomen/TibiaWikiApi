package com.tibiawiki.domain.objects.validation

class ValidationException : RuntimeException {
    val validationResults: List<ValidationResult>

    constructor() : super() {
        validationResults = emptyList()
    }

    constructor(message: String) : super(message) {
        validationResults = emptyList()
    }

    constructor(throwable: Throwable) : super(throwable) {
        validationResults = emptyList()
    }

    constructor(validationResults: List<ValidationResult>) : super() {
        this.validationResults = validationResults.toList()
    }

    override val message: String
        get() = super.message
            ?: if (validationResults.isNotEmpty()) {
                validationResults.joinToString(", ") { it.description.orEmpty() }
            } else {
                ""
            }

    companion object {
        fun fromResults(validationResults: List<ValidationResult>): ValidationException {
            return ValidationException(validationResults)
        }
    }
}
