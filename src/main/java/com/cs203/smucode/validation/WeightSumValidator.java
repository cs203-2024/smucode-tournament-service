package com.cs203.smucode.validation;

import com.cs203.smucode.models.Tournament;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class WeightSumValidator implements ConstraintValidator<WeightSum, Tournament> {

    @Override
    public boolean isValid(Tournament tournament, ConstraintValidatorContext context) {

        if (tournament.getTimeWeight() == null ||
            tournament.getMemWeight() == null ||
            tournament.getTestCaseWeight() == null) {
            return false;
        }

        BigDecimal sum = tournament.getTimeWeight()
                                   .add(tournament.getMemWeight())
                                   .add(tournament.getTestCaseWeight());

        return sum.compareTo(BigDecimal.ONE) == 0;
    }

}
