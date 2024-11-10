package com.cs203.smucode.dtos.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantUserDTO {

    private String username;

    private String profileImageUrl;
}
