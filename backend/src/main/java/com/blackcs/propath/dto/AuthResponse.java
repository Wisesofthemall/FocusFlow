package com.blackcs.propath.dto;

public record AuthResponse(String token, long expiresAt, UserSummary user) {}
