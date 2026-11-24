package com.infy.scm360.identity.dto;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    UserResponse user
) {}
