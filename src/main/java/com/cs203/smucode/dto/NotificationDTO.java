package com.cs203.smucode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record NotificationDTO(
        @NotNull(message = "Tournament ID cannot be null")
        UUID tournamentId,

        @NotNull(message = "Username cannot be null")
        @NotBlank(message = "Username cannot be blank")
        String username,

        @NotNull(message = "Tournament name cannot be null")
        @NotBlank(message = "Tournament name cannot be blank")
        String tournamentName,

        @NotNull(message = "Message cannot be null")
        @NotBlank(message = "Message cannot be blank")
        String message,

        @NotNull(message = "Notification type cannot be null")
        String type
) {

        public static Builder builder() {
                return new Builder();
        }

        public static class Builder {
                private UUID tournamentId;
                private String username;
                private String tournamentName;
                private String message;
                private String type;

                public Builder tournamentId(UUID tournamentId) {
                        this.tournamentId = tournamentId;
                        return this;
                }

                public Builder lastName(String lastName) {
                        this.lastName = lastName;
                        return this;
                }

                public Builder age(int age) {
                        this.age = age;
                        return this;
                }

                public Builder email(String email) {
                        this.email = email;
                        return this;
                }

                public Person build() {
                        // You can add validation here if needed
                        return new Person(firstName, lastName, age, email);
                }
        }
}
