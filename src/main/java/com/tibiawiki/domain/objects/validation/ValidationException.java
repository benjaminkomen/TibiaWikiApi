package com.tibiawiki.domain.objects.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {

    private final List<ValidationResult> validationResults = new ArrayList<>();

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
        this.validationResults.addAll(validationResults);
    }

    public static ValidationException fromResults(List<ValidationResult> validationResults) {
        return new ValidationException(validationResults);
    }

    public List<ValidationResult> getValidationResults() {
        return validationResults;
    }

    @SuppressWarnings("squid:S3358") // ternary operation is ok here
    @Override
    public String getMessage() {
        return super.getMessage() != null
                ? super.getMessage()
                : !validationResults.isEmpty()
                ? validationResults.stream().map(ValidationResult::getDescription).collect(Collectors.joining(", "))
                : "";
    }
}
