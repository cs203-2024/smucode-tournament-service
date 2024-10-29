package com.cs203.smucode.processors;

import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.exceptions.UserNotFoundException;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gav
 * @version 1.0
 * @since 2024-10-25
 *
 * This class is used to handle consumed information from user microservice.
 */

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
            try {
                userDTOs.add(userServiceConsumer.getUserById(username));
            }
            catch (FeignException e) {
                throw new UserNotFoundException("User with username: " + username + " not found");
            }
        }
        return userDTOs;
    }
}
