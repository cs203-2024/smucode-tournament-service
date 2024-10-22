package com.cs203.smucode.services;

import com.cs203.smucode.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserClient userClient;

    @Autowired
    public UserService(UserClient userClient) {
        this.userClient = userClient;
    }

    public List<UserDTO> getUsers(List<String> usernames) {
        List<UserDTO> userDTOs = new ArrayList<>();
        for (String username : usernames) {
            userDTOs.add(userClient.getUserById(username));
        }
        return userDTOs;
    }
}
