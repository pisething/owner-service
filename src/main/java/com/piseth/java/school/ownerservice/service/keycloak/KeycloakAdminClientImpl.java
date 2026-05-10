package com.piseth.java.school.ownerservice.service.keycloak;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.piseth.java.school.ownerservice.config.KeycloakProperties;
import com.piseth.java.school.ownerservice.exception.KeycloakIntegrationException;
import com.piseth.java.school.ownerservice.service.keycloak.command.KeycloakCreateUserCommand;
import com.piseth.java.school.ownerservice.service.keycloak.dto.KeycloakTokenResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAdminClientImpl implements KeycloakAdminClient{
	private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
	private static final String PASSWORD_CREDENTIAL_TYPE = "password";
	private final WebClient keycloakWebClient;
    private final KeycloakProperties properties;

	@Override
	public Mono<String> getAdminToken() {
		return keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", properties.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", properties.admin().clientId())
                        .with("client_secret", properties.admin().clientSecret())
                        .with("grant_type", GRANT_TYPE_CLIENT_CREDENTIALS))
                .retrieve()
                .bodyToMono(KeycloakTokenResponse.class)
                .map(KeycloakTokenResponse::accessToken);
	}

	@Override
    public Mono<String> createUser(KeycloakCreateUserCommand command) {
        return getAdminToken()
                .flatMap(token -> keycloakWebClient.post()
                        .uri("/admin/realms/{realm}/users", properties.realm())
                        .headers(headers -> headers.setBearerAuth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(buildCreateUserRequest(command))
                        .exchangeToMono(response -> {
                            if (response.statusCode().is2xxSuccessful()) {
                                String location = response.headers()
                                        .asHttpHeaders()
                                        .getFirst("Location");

                                return Mono.just(extractUserId(location));
                            }

                            return response.bodyToMono(String.class)
                                    .defaultIfEmpty("Failed to create user in Keycloak")
                                    .flatMap(body -> {
                                        log.error("Keycloak create user failed. body={}", body);
                                        return Mono.error(new KeycloakIntegrationException(body));
                                    });
                        }));
    }
	
	@Override
    public Mono<Void> resetPassword(String keycloakUserId, String password) {
        return getAdminToken()
                .flatMap(token -> keycloakWebClient.put()
                        .uri(
                                "/admin/realms/{realm}/users/{userId}/reset-password",
                                properties.realm(),
                                keycloakUserId
                        )
                        .headers(headers -> headers.setBearerAuth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(buildResetPasswordRequest(password))
                        .retrieve()
                        .bodyToMono(Void.class));
    }
	
	private Map<String, Object> buildResetPasswordRequest(String password) {
        return Map.of(
                "type", PASSWORD_CREDENTIAL_TYPE,
                "value", password,
                "temporary", false
        );
    }
	
	private Map<String, Object> buildCreateUserRequest(KeycloakCreateUserCommand command) {
        return Map.of(
                "username", command.username(),
                "enabled", command.enabled(),
                "emailVerified", command.emailVerified(),
                "firstName", safeString(command.firstName()),
                "lastName", safeString(command.lastName()),
                "email", safeString(command.email()),
                "attributes", command.attributes(),
                "requiredActions", List.of(),
                "credentials", List.of(
                        Map.of(
                                "type", PASSWORD_CREDENTIAL_TYPE,
                                "value", command.password(),
                                "temporary", false
                        )
                )
        );
    }
	
	private String safeString(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }
	
	private String extractUserId(String location) {
        if (location == null || location.isBlank()) {
            throw new KeycloakIntegrationException("Cannot extract Keycloak user id from response location");
        }

        return location.substring(location.lastIndexOf("/") + 1);
    }

}
