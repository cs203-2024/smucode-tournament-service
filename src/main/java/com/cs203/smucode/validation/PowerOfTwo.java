package com.cs203.smucode.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PowerOfTwoValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PowerOfTwo {

    String message() default "The capacity of a tournament must be to the power of two";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
