package com.piseth.java.school.ownerservice.service.keycloak;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.piseth.java.school.ownerservice.config.KeycloakProperties;
import com.piseth.java.school.ownerservice.dto.LoginResponse;
import com.piseth.java.school.ownerservice.service.keycloak.dto.KeycloakLoginResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KeycloakAuthClientImpl implements KeycloakAuthClient {

    private static final String GRANT_TYPE_PASSWORD = "password";

    private final WebClient keycloakWebClient;
    private final KeycloakProperties properties;

    @Override
    public Mono<LoginResponse> login(String username, String password) {
        return keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", properties.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", properties.client().clientId())
                        .with("client_secret", properties.client().clientSecret())
                        .with("username", username)
                        .with("password", password)
                        .with("grant_type", GRANT_TYPE_PASSWORD))
                .retrieve()
                .bodyToMono(KeycloakLoginResponse.class)
                .map(this::toLoginResponse);
    }

    private LoginResponse toLoginResponse(KeycloakLoginResponse response) {
        return new LoginResponse(
                response.accessToken(),
                response.refreshToken(),
                response.expiresIn(),
                response.tokenType()
        );
    }
}