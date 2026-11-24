package com.infy.scm360.catalog.dto;

public record CategoryDto(
    String id,
    String name,
    String description,
    String parentId
) {}
