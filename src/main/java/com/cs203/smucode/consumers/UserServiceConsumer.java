package com.cs203.smucode.consumers;

import com.cs203.smucode.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceConsumer {

//    boolean userExists(String username);

    @GetMapping("/userId}")
    UserDTO getUserById(@PathVariable("userId") String username);

//    TODO: future optimisation - instead of iterative GET requests
//    List<UserDTO> getUsers(List<String> usernames);
}
