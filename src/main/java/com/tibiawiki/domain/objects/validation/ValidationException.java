package com.tibiawiki.domain.objects.validation;

import java.util.List;

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
}
