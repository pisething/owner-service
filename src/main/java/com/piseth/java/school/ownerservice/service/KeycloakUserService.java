package com.piseth.java.school.ownerservice.service;

import com.piseth.java.school.ownerservice.dto.CreateKeycloakUserRequest;
import com.piseth.java.school.ownerservice.dto.CreateKeycloakUserResponse;
import com.piseth.java.school.ownerservice.dto.LoginRequest;
import com.piseth.java.school.ownerservice.dto.LoginResponse;
import com.piseth.java.school.ownerservice.dto.ResetPasswordRequest;

import reactor.core.publisher.Mono;

public interface KeycloakUserService {

    Mono<String> getAdminToken();

    Mono<CreateKeycloakUserResponse> createUser(CreateKeycloakUserRequest request);

    Mono<Void> resetPassword(ResetPasswordRequest request);

    Mono<LoginResponse> login(LoginRequest request);
}