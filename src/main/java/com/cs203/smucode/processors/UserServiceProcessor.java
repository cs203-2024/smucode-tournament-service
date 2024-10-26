package com.cs203.smucode.processors;

import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceProcessor {

    private final UserServiceConsumer userServiceConsumer;

    @Autowired
    public UserServiceProcessor(UserServiceConsumer userServiceConsumer) {
        this.userServiceConsumer = userServiceConsumer;
    }

    public List<UserDTO> getUsers(List<String> usernames) {
        List<UserDTO> userDTOs = new ArrayList<>();
        for (String username : usernames) {
            userDTOs.add(userServiceConsumer.getUserById(username));
        }
        return userDTOs;
    }
}
