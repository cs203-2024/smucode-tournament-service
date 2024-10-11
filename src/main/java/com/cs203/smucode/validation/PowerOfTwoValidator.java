package com.cs203.smucode.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PowerOfTwoValidator implements ConstraintValidator<PowerOfTwo, Integer> {

    @Override
    public boolean isValid (Integer value, ConstraintValidatorContext context) {

        if (value == 0) { return false; }

        return value > 0 && (value & (value - 1)) == 0;
    }
}
