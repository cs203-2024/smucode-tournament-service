package com.cs203.smucode.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String profileImageUrl;
    private String role;
    private double mu;
    private double sigma;
    private double skillIndex;
}
