package com.habittracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDtos {

    public record ProfileResponse(
            Long id,
            String username,
            String email
    ) {}

    public record UpdateProfileRequest(
            @NotBlank String currentPassword,
            @Size(min = 3, max = 50) String newUsername,
            @Email String newEmail,
            @Size(min = 6, max = 100) String newPassword
    ) {}
}
