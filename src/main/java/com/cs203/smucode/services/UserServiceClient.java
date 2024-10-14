package com.cs203.smucode.services;

import com.cs203.smucode.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient {

//    boolean userExists(String username);

//    @GetMapping("/users/{id}")
//    UserDTO getUserById(@PathVariable("id") String username);

    List<UserDTO> getUsers(List<String> usernames);

}
