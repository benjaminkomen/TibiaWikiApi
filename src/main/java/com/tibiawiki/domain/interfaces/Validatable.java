package com.tibiawiki.domain.interfaces;

import com.tibiawiki.domain.objects.validation.ValidationResult;

import java.util.List;

public interface Validatable {

    List<ValidationResult> validate();
}
