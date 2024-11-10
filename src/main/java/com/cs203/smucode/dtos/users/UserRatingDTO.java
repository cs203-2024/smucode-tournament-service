package com.cs203.smucode.dtos.users;

public record UserRatingDTO(
        String username,
        double mu,
        double sigma
) {}