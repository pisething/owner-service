package com.piseth.java.school.ownerservice.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.piseth.java.school.ownerservice.domain.UserAccount;

import reactor.core.publisher.Mono;

public interface UserAccountRepository extends ReactiveCrudRepository<UserAccount, UUID> {

    Mono<UserAccount> findByKeycloakUserId(String keycloakUserId);

    Mono<Boolean> existsByCountryCodeAndPhoneNumber(String countryCode, String phoneNumber);
}