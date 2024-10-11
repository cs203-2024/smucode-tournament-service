package com.cs203.smucode.services;

import com.cs203.smucode.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface UserServiceClient {

//    boolean userExists(String username);

    UserDTO getUser(String username);

    List<UserDTO> getUsers(List<String> usernames);

}
