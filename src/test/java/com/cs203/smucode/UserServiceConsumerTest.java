package com.cs203.smucode;

import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.dto.UserRatingDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceConsumerTest {

    @Autowired
    private UserServiceConsumer userServiceConsumer;

    @Test
    void testRatingUpdate() {
        UserDTO userBefore = userServiceConsumer.getUserById("user1");
        System.out.println("Before update - mu: " + userBefore.mu() + ", sigma: " + userBefore.sigma());

        double newMu = userBefore.mu() + 2.0;  // Increase mu by 2
        double newSigma = userBefore.sigma() - 0.5;  // Decrease sigma by 0.5

        UserRatingDTO updateDTO = new UserRatingDTO(
                "user1",
                newMu,
                newSigma
        );

        assertDoesNotThrow(() -> userServiceConsumer.updateRating(updateDTO));

        UserDTO userAfter = userServiceConsumer.getUserById("user1");
        System.out.println("After update - mu: " + userAfter.mu() + ", sigma: " + userAfter.sigma());

        assertEquals(newMu, userAfter.mu(), 0.01, "Mu should be updated to new value");
        assertEquals(newSigma, userAfter.sigma(), 0.01, "Sigma should be updated to new value");

        UserRatingDTO resetDTO = new UserRatingDTO(
                "user1",
                userBefore.mu(),
                userBefore.sigma()
        );
        userServiceConsumer.updateRating(resetDTO);

        UserDTO userAfterReset = userServiceConsumer.getUserById("user1");
        assertEquals(userBefore.mu(), userAfterReset.mu(), 0.01, "Mu should be reset to original value");
        assertEquals(userBefore.sigma(), userAfterReset.sigma(), 0.01, "Sigma should be reset to original value");
    }
}