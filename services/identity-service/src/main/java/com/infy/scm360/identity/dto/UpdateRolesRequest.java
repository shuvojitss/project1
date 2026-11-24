package com.infy.scm360.identity.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UpdateRolesRequest(
    @NotEmpty(message = "At least one role must be provided")
    List<String> roles
) {}
