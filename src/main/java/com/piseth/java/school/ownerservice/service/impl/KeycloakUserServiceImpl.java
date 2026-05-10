package com.piseth.java.school.ownerservice.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.piseth.java.school.ownerservice.dto.CreateKeycloakUserRequest;
import com.piseth.java.school.ownerservice.dto.CreateKeycloakUserResponse;
import com.piseth.java.school.ownerservice.dto.LoginRequest;
import com.piseth.java.school.ownerservice.dto.LoginResponse;
import com.piseth.java.school.ownerservice.dto.ResetPasswordRequest;
import com.piseth.java.school.ownerservice.service.KeycloakUserService;
import com.piseth.java.school.ownerservice.service.keycloak.KeycloakAdminClient;
import com.piseth.java.school.ownerservice.service.keycloak.KeycloakAuthClient;
import com.piseth.java.school.ownerservice.service.keycloak.command.KeycloakCreateUserCommand;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KeycloakUserServiceImpl implements KeycloakUserService{
	private final KeycloakAdminClient keycloakAdminClient;
	private final KeycloakAuthClient keycloakAuthClient;

	@Override
	public Mono<String> getAdminToken() {
		return keycloakAdminClient.getAdminToken();
	}
	
	@Override
    public Mono<CreateKeycloakUserResponse> createUser(CreateKeycloakUserRequest request) {
		//fdgsfgdfg
		
		
		//dfgdfgfg
        KeycloakCreateUserCommand command = toCommand(request);

        return keycloakAdminClient.createUser(command)
                .map(CreateKeycloakUserResponse::new);
    }
	
	@Override
    public Mono<Void> resetPassword(ResetPasswordRequest request) {
        return keycloakAdminClient.resetPassword(
                request.keycloakUserId(),
                request.password()
        );
    }
	
	@Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return keycloakAuthClient.login(
                request.username(),
                request.password()
        );
    }
	
	private KeycloakCreateUserCommand toCommand(CreateKeycloakUserRequest request) {
        return new KeycloakCreateUserCommand(
                request.username(),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password(),
                true,
                true,
                Map.of(
                        "phoneNumber", safeString(request.phoneNumber()),
                        "userType", safeString(request.userType())
                )
        );
    }
	
	private String safeString(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }

}
