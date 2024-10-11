package com.cs203.smucode.validation;

import com.cs203.smucode.models.Tournament;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WeightSumValidator implements ConstraintValidator<WeightSum, Tournament> {

    @Override
    public boolean isValid(Tournament tournament, ConstraintValidatorContext context) {

        if (tournament.getTimeWeight() < 0 ||
            tournament.getMemWeight() < 0 ||
            tournament.getTestCaseWeight() < 0) {
            return false;
        }

        int sum = tournament.getTimeWeight() + tournament.getMemWeight() + tournament.getTestCaseWeight();

        return sum == 100;
    }

}
