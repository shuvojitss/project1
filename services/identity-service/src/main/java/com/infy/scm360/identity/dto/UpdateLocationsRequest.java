package com.infy.scm360.identity.dto;

import java.util.List;

public record UpdateLocationsRequest(
    List<String> locationIds
) {}
