package com.piseth.java.school.ownerservice.dto;
public record ResetPasswordRequest(
        String keycloakUserId,
        String password
) {
}