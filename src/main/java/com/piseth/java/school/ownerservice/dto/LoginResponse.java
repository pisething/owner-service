package com.piseth.java.school.ownerservice.dto;
public record LoginResponse(
        String accessToken,
        String refreshToken,
        Integer expiresIn,
        String tokenType
) {
}