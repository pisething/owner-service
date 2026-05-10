package com.piseth.java.school.ownerservice.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.piseth.java.school.ownerservice.domain.CustomerProfile;

import reactor.core.publisher.Mono;

public interface CustomerProfileRepository extends ReactiveCrudRepository<CustomerProfile, UUID> {

    Mono<CustomerProfile> findByUserAccountId(UUID userAccountId);
}