package com.cs203.smucode.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = WeightSumValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WeightSum {
    String message() default "The values of timeWeight, memWeight and testCaseWeight must sum up to 100";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
