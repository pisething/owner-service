package com.piseth.java.school.ownerservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.piseth.java.school.ownerservice.dto.CreateKeycloakUserRequest;
import com.piseth.java.school.ownerservice.dto.CreateKeycloakUserResponse;
import com.piseth.java.school.ownerservice.dto.LoginRequest;
import com.piseth.java.school.ownerservice.dto.LoginResponse;
import com.piseth.java.school.ownerservice.dto.ResetPasswordRequest;
import com.piseth.java.school.ownerservice.service.KeycloakUserService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/keycloak/users")
public class KeycloakUserController {

    private final KeycloakUserService service;

    @GetMapping("/admin-token")
    public Mono<String> getAdminToken() {
        return service.getAdminToken();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreateKeycloakUserResponse> createUser(@RequestBody CreateKeycloakUserRequest request) {
        return service.createUser(request);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        return service.resetPassword(request);
    }

    @PostMapping("/login")
    public Mono<LoginResponse> login(@RequestBody LoginRequest request) {
        return service.login(request);
    }
}