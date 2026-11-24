package com.infy.scm360.identity.dto;

import java.util.List;

public record UserResponse(
    String id,
    String username,
    String email,
    String fullName,
    String status,
    List<String> roles,
    List<String> locationIds,
    String supplierId
) {}
