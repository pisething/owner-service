package com.piseth.java.school.ownerservice.dto;
public record CreateKeycloakUserRequest(
        String username,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String userType,
        String password
) {
}