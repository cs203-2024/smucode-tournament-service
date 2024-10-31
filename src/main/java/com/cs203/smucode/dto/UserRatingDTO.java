package com.cs203.smucode.dto;

public record UserRatingDTO(
        String username,
        double mu,
        double sigma
) {}