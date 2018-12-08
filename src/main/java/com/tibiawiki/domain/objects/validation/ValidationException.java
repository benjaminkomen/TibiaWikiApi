package com.tibiawiki.domain.objects.validation;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {

    private List<ValidationResult> validationResults;

    public ValidationException() {
        super();
    }

    public ValidationException(String messsage) {
        super(messsage);
    }

    public ValidationException(Throwable throwable) {
        super(throwable);
    }

    public ValidationException(List<ValidationResult> validationResults) {
        this();
        this.validationResults = validationResults;
    }

    public static ValidationException fromResults(List<ValidationResult> validationResults) {
        return new ValidationException(validationResults);
    }

    public List<ValidationResult> getValidationResults() {
        return validationResults;
    }

    @Override
    public String getMessage() {
        return super.getMessage() != null
                ? super.getMessage()
                : validationResults != null && !validationResults.isEmpty()
                ? validationResults.stream().map(ValidationResult::getDescription).collect(Collectors.joining(", "))
                : "";
    }
}
