package com.cs203.smucode.dto;


public record UserDTO(

        String username,

        String password,

        String email,
        String profileImageUrl,

        String role,

        double mu,
        double sigma,
        double skillIndex
) {}
