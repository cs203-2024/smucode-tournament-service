package com.cs203.smucode.consumers;

import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.dto.UserRatingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author jered
 * @version 1.0
 * @since 2024-10-14
 *
 * This class is used to consume API endpoints exposed by user microservice.
 */

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceConsumer {

    @GetMapping("/profile/{username}")
    UserDTO getUserById(@PathVariable String username);

    @PutMapping("/update-rating")
    void updateRating(@RequestBody UserRatingDTO ratingDTO);

//    TODO: future optimisation - instead of iterative GET requests
}
