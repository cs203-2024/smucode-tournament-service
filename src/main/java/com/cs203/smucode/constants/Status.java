package com.cs203.smucode.constants;

public enum Status {
    UPCOMING,
    ONGOING,
    COMPLETED;

    @Override
    public String toString() {
        return name().toLowerCase(); // Converts enum value to lowercase when persisting
    }

    // Static method to convert a string (database value) to the corresponding enum value
    public static Status fromValue(String value) {
        return Status.valueOf(value.toUpperCase());
    }
}
