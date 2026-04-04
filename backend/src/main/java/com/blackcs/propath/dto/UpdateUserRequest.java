package com.blackcs.propath.dto;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @Size(max = 200) String name, @Size(max = 4000) String googleRefreshToken) {}
