package com.cs203.smucode.consumers;

import com.cs203.smucode.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author jered
 * @version 1.0
 * @since 2024-10-14
 *
 * This class is used to consume API endpoints exposed by user microservice.
 */

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceConsumer {

    @GetMapping("/userId}")
    UserDTO getUserById(@PathVariable("userId") String username);

//    TODO: future optimisation - instead of iterative GET requests
}
