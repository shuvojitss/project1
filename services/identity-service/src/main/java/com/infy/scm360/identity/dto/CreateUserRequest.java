package com.infy.scm360.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateUserRequest(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 64)
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,

    @NotBlank(message = "Full name is required")
    String fullName,

    List<String> roles,
    List<String> locationIds,
    String supplierId
) {}
