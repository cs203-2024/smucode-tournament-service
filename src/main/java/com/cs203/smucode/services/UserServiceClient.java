package com.cs203.smucode.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface UserServiceClient {

    boolean userExists(UUID username);

}
