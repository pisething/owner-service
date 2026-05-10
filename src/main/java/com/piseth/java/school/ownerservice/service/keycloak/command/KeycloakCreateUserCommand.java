package com.piseth.java.school.ownerservice.service.keycloak.command;

import java.util.Map;

public record KeycloakCreateUserCommand(
        String username,
        String firstName,
        String lastName,
        String email,
        String password,
        boolean enabled,
        boolean emailVerified,
        Map<String, String> attributes
) {
}