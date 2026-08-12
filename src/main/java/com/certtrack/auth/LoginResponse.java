package com.certtrack.auth;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInMinutes
) {
}
