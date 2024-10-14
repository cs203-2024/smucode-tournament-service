package com.cs203.smucode.services.impl;

import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.services.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceClientImpl implements UserServiceClient {
//
//    private RestTemplate restTemplate;
////    TODO: change route to user microservice
//    private String url = "localhost:8080/api";
//
//    @Autowired
//    public UserServiceClientImpl(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }

//    public boolean userExists(UUID userId) {
//        String url = "user-microservice" + userId;
//        try {
//            restTemplate.getForObject(url, UserDTO.class);
//            return true;
//        } catch (HttpClientErrorException.NotFound e) {
//            return false;
//        }
//    }

//    public UserDTO getUser(String username) {
//        System.out.println(restTemplate.getForObject(url + "/users/" + username, UserDTO.class));
//        return restTemplate.getForObject(url + "/" + username, UserDTO.class);
//    }
//
    public List<UserDTO> getUsers(List<String> usernames) {
//        List<UserDTO> users = new ArrayList<>();
//        for (String username : usernames) {
//            users.add(getUser(username));
//        }
//        return users;
        return null;
    }

}
