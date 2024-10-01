package com.cs203.smucode.constants;

public enum SignupStatus {
    OPEN,
    CLOSED;

    @Override
    public String toString() {
        return name().toLowerCase(); // Converts enum value to lowercase when persisting
    }

    // Static method to convert a string (database value) to the corresponding enum value
    public static SignupStatus fromValue(String value) {
        return SignupStatus.valueOf(value.toUpperCase());
    }}

