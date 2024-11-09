package com.cs203.smucode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record NotificationDTO(
        @NotNull(message = "Tournament ID cannot be null")
        UUID tournamentId,

        @NotNull(message = "Tournament name cannot be null")
        @NotBlank(message = "Tournament name cannot be blank")
        String tournamentName,

        @NotNull(message = "Message cannot be null")
        @NotBlank(message = "Message cannot be blank")
        String message,

        @NotNull(message = "Notification type cannot be null")
        String type,

        @NotNull(message = "Notification category cannot be null")
        String category,

        @NotEmpty(message = "Recipients cannot be empty")
        List<String> recipients
) {
//
//        public static Builder builder() {
//                return new Builder();
//        }
//
//        public static class Builder {
//                private UUID tournamentId;
//                private String tournamentName;
//                private String message;
//                private String type;
//                private String category;
//                private List<String> recipients;
//
//                public Builder tournamentId(UUID tournamentId) {
//                        this.tournamentId = tournamentId;
//                        return this;
//                }
//
//                public Person build() {
//                        // You can add validation here if needed
//                        return new Person(firstName, lastName, age, email);
//                }
//        }
}
