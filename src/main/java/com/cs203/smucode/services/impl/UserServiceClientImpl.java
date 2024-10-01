package com.cs203.smucode.services.impl;

import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.services.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class UserServiceClientImpl implements UserServiceClient {

    private RestTemplate restTemplate;

    @Autowired
    public UserServiceClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean userExists(UUID userId) {
        String url = "user-microservice" + userId;
        try {
            restTemplate.getForObject(url, UserDTO.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }


}
