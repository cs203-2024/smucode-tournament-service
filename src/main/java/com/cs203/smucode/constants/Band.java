package com.cs203.smucode.constants;

public enum Band {
    LOWER,
    MIDDLE,
    UPPER;


    @Override
    public String toString() {
        return name().toLowerCase(); // Converts enum value to lowercase when persisting
    }

    // Static method to convert a string (database value) to the corresponding enum value
    public static Band fromValue(String value) {
        return Band.valueOf(value.toUpperCase());
    }
}


