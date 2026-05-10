package com.piseth.java.school.ownerservice.service.keycloak;

import com.piseth.java.school.ownerservice.service.keycloak.command.KeycloakCreateUserCommand;

import reactor.core.publisher.Mono;

public interface KeycloakAdminClient {

    Mono<String> getAdminToken();

    Mono<String> createUser(KeycloakCreateUserCommand command);

    Mono<Void> resetPassword(String keycloakUserId, String password);
}