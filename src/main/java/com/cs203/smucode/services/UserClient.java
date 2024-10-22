package com.cs203.smucode.services;

import com.cs203.smucode.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "user-service", url = "${user.service.url}")
@FeignClient(name = "user-service", url = "localhost:9000/api")
public interface UserClient {

//    boolean userExists(String username);

    @GetMapping("/users/{userId}")
    UserDTO getUserById(@PathVariable("userId") String username);

//    TODO: future optimisation - instead of iterative GET requests
//    List<UserDTO> getUsers(List<String> usernames);
}
